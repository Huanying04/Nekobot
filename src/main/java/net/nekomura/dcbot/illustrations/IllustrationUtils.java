package net.nekomura.dcbot.illustrations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.enums.ConfigJsonArrayData;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.utils.jixiv.IllustrationInfo;
import net.nekomura.utils.jixiv.Pixiv;
import net.nekomura.utils.jixiv.SearchResult;
import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.enums.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class IllustrationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IllustrationUtils.class);

    public static void getRandomAndSend(PixivSearchMode mode, CommandContext ctx) throws IOException {
        LOGGER.debug("隨機從config中挑選搜尋關鍵字");
        ArrayList<Object> keywords = Config.jsonArrayToArrayList(Config.get(ConfigJsonArrayData.RANDOM_PIXIV_SEARCH_KEYWORDS));
        String keyword = (String) keywords.get(ThreadLocalRandom.current().nextInt(0, keywords.size()));

        LOGGER.debug("隨機搜尋之關鍵字為{}", keyword);

        PixivSearchOrder order = PixivSearchOrder.NEW_TO_OLD;  //排序方式使用最新

        //先暫時搜尋，確認搜尋結果共有幾頁
        SearchResult tempResult = Pixiv.search(keyword, 1, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);
        int lastPageOfSearching = tempResult.getLastPageIndex();

        //獲取隨機作品時，隨機的頁碼
        int pageForSearching = ThreadLocalRandom.current().nextInt(1, lastPageOfSearching + 1);

        //獲取該頁的搜尋結果
        SearchResult result = Pixiv.search(keyword, pageForSearching, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);

        int max = result.getPageResultCount();  //該頁的作品數量
        int index = ThreadLocalRandom.current().nextInt(0, max);  //欲選定作品的位置
        int artworkID = result.getIds()[index];  //獲取該位置的作品id

        //獲取該作品資訊
        IllustrationInfo info = Illustration.getInfo(artworkID);

        //作品隨機頁碼
        int artworkPage = ThreadLocalRandom.current().nextInt(0, info.getPageCount());

        LOGGER.debug("下載pixiv插畫中，id為{}，頁碼為{}", artworkID, artworkPage);
        byte[] image = info.getImage(artworkPage, PixivImageSize.ORIGINAL);

        if (image.length > 8388608) {
            LOGGER.debug("檔案過大，下載大小較小的版本");
            image = info.getImage(artworkPage, PixivImageSize.REGULAR);
        }

        LOGGER.debug("id為{}、頁碼為{}的pixiv插畫下載完成", artworkID, artworkPage);

        String type = info.getImageFileFormat(artworkPage);

        LOGGER.debug("訊息準備發送");

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
        eb.setTitle("隨機pixiv圖片");
        eb.addField("ID", "[" + artworkID + "](https://www.pixiv.net/artworks/" + artworkID + ")", true);
        eb.addField("頁碼", String.valueOf(artworkPage), true);
        eb.setImage("attachment://" + info.getId() + "." + type);
        ctx.event.getChannel().sendFile(image, info.getId() + "." + type).setEmbeds(eb.build()).queue();

        LOGGER.debug("訊息發送完畢");
    }

    public static void searchByKeywordsRandomAndSend(PixivSearchMode mode, CommandContext ctx) throws IOException {
        if (ctx.args.size() == 0) {  //未輸入任何關鍵字
            LOGGER.debug("由於未輸入任何關鍵字，取消執行指令");

            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("請輸入搜尋關鍵字。");
            ctx.event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        StringBuilder orderKeywords;
        SearchResult tempResult;
        PixivSearchOrder order = PixivSearchOrder.NEW_TO_OLD;
        int testCount = 0;

        do {  //隨機加入XXXusers入り關鍵字"代替"熱門搜尋功能，最多嘗試3次
            orderKeywords = new StringBuilder();
            for (String s: ctx.args) {
                orderKeywords.append(s).append(" ");
            }

            ArrayList<Object> keywords = Config.jsonArrayToArrayList(Config.get(ConfigJsonArrayData.RANDOM_PIXIV_SEARCH_KEYWORDS));
            String keyword = (String) keywords.get(ThreadLocalRandom.current().nextInt(0, keywords.size()));
            boolean contain = false;

            for (Object o : keywords) {
                if (ctx.args.contains(o)) {
                    contain = true;
                    break;
                }
            }

            if (!contain) {
                orderKeywords.append(keyword).append(" ");
            }

            tempResult = Pixiv.search(orderKeywords.toString(), 1, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);
            testCount++;
            ctx.getChannel().sendTyping().queue();
        }while (tempResult.getResultCount() == 0 && testCount <= 3);

        if (tempResult.getResultCount() == 0) {  //如果還是沒有，乾脆不管了，那可能是關鍵字不夠熱門等
            orderKeywords = new StringBuilder();
            for (String s: ctx.args) {
                orderKeywords.append(s).append(" ");
            }

            tempResult = Pixiv.search(orderKeywords.toString(), 1, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);
        }

        if (tempResult.getResultCount() == 0) {
            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("沒有搜尋到圖。請嘗試使用其他關鍵字。");
            ctx.event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        int lastPage = tempResult.getLastPageIndex();
        int page = ThreadLocalRandom.current().nextInt(1, lastPage + 1);
        SearchResult result = Pixiv.search(orderKeywords.toString(), page, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);

        int max = result.getPageResultCount();
        int artworkID = result.getIds()[ThreadLocalRandom.current().nextInt(0, max)];

        IllustrationInfo info = Illustration.getInfo(artworkID);

        int artworkPage = ThreadLocalRandom.current().nextInt(0, info.getPageCount());
        byte[] image = info.getImage(artworkPage, PixivImageSize.ORIGINAL);
        if (image.length > 8388608) {
            image = info.getImage(artworkPage, PixivImageSize.REGULAR);
        }
        String type = info.getImageFileFormat(artworkPage);

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
        eb.setTitle("隨機搜尋pixiv圖片");
        eb.addField("ID", "[" + artworkID + "](https://www.pixiv.net/artworks/" + artworkID + ")", true);
        eb.addField("頁碼", String.valueOf(artworkPage), true);
        eb.setImage("attachment://" + info.getId() + "." + type);
        ctx.event.getChannel().sendFile(image, info.getId() + "." + type).setEmbeds(eb.build()).queue();
    }
}
