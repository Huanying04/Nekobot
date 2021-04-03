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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class IllustrationUtils {
    public static void getRandomAndSend(PixivSearchMode mode, CommandContext ctx) throws IOException {
        ArrayList<Object> keywords = Config.jsonArrayToArrayList(Config.get(ConfigJsonArrayData.RANDOM_PIXIV_SEARCH_KEYWORDS));
        String keyword = (String) keywords.get(ThreadLocalRandom.current().nextInt(0, keywords.size()));
        PixivSearchOrder order = PixivSearchOrder.NEW_TO_OLD;
        SearchResult tempResult = Pixiv.search(keyword, 1, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);
        int lastPageOfSearching = tempResult.getLastPageIndex();
        int pageForSearching = ThreadLocalRandom.current().nextInt(1, lastPageOfSearching + 1);
        SearchResult result = Pixiv.search(keyword, pageForSearching, PixivSearchArtworkType.ILLUSTRATIONS, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.ILLUST);

        int max = result.getPageResultCount();
        int page = ThreadLocalRandom.current().nextInt(0, max);
        int artworkID = result.getIds()[page];

        IllustrationInfo info = Illustration.getInfo(artworkID);

        int artworkPage = ThreadLocalRandom.current().nextInt(0, info.getPageCount());
        byte[] image = info.getImage(artworkPage, PixivImageSize.ORIGINAL);
        if (image.length > 8388608) {
            image = info.getImage(artworkPage, PixivImageSize.REGULAR);
        }
        String type = info.getImageFileFormat(artworkPage);

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
        eb.setTitle("隨機pixiv圖片");
        eb.addField("ID", "[" + artworkID + "](https://www.pixiv.net/artworks/" + artworkID + ")", true);
        eb.addField("頁碼", String.valueOf(artworkPage), true);
        eb.setImage("attachment://" + info.getId() + "." + type);
        ctx.event.getChannel().sendFile(image, info.getId() + "." + type).embed(eb.build()).queue();
    }

    public static void searchByKeywordsRandomAndSend(PixivSearchMode mode, CommandContext ctx) throws IOException {
        if (ctx.args.size() == 0) {  //未輸入任何關鍵字
            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("請輸入搜尋關鍵字。");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
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
            ctx.event.getChannel().sendMessage(eb.build()).queue();
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
        ctx.event.getChannel().sendFile(image, info.getId() + "." + type).embed(eb.build()).queue();
    }
}
