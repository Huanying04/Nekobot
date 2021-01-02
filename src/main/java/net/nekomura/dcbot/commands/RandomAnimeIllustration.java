package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.Enums.ConfigJsonArrayData;
import net.nekomura.dcbot.Enums.ConfigStringData;
import net.nekomura.dcbot.Listener;
import net.nekomura.dcbot.Utils.Md5;
import net.nekomura.utils.jixiv.Enums.*;
import net.nekomura.utils.jixiv.Enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.Enums.search.*;
import net.nekomura.utils.jixiv.Illustration;
import net.nekomura.utils.jixiv.IllustrationInfo;
import net.nekomura.utils.jixiv.Pixiv;
import net.nekomura.utils.jixiv.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAnimeIllustration implements net.nekomura.dcbot.commands.Managers.ICommand {


    @Override
    public void handle(net.nekomura.dcbot.commands.Managers.CommandContext ctx) throws Exception {
        PixivSearchMode mode = PixivSearchMode.SAFE;
        Pixiv pixiv = new Pixiv(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT));

        ArrayList<Object> keywords = Config.jsonArrayToArrayList(Config.get(ConfigJsonArrayData.RANDOM_PIXIV_SEARCH_KEYWORDS));
        String keyword = (String) keywords.get(ThreadLocalRandom.current().nextInt(0, keywords.size()));
        PixivSearchOrder order = PixivSearchOrder.NEW_TO_OLD;
        SearchResult tempResult = pixiv.search(keyword, 1, PixivSearchArtworkType.Illustrations, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.Illust);
        int lastPage = tempResult.getLastPageIndex();
        int page = ThreadLocalRandom.current().nextInt(1, lastPage + 1);
        SearchResult result = pixiv.search(keyword, page, PixivSearchArtworkType.Illustrations, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.Illust);

        int max = result.getPageResultCount();
        int artworkID = result.getIds()[ThreadLocalRandom.current().nextInt(0, max)];

        Illustration i = new Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT));
        IllustrationInfo info = i.get(artworkID);

        int artworkPage = ThreadLocalRandom.current().nextInt(0, info.getPageCount());
        byte[] image = info.getImage(artworkPage, PixivImageSize.Original);
        if (image.length > 8388608) {
            image = info.getImage(artworkPage, PixivImageSize.Regular);
        }
        String type = info.getImageFileFormat(artworkPage);

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
        eb.setTitle("隨機pixiv圖片");
        eb.addField("ID", "[" + artworkID + "](https://www.pixiv.net/artworks/" + artworkID + ")", true);
        eb.setImage("attachment://" + Md5.INSTANCE.toMD5(image) + "." + type);
        ctx.event.getChannel().sendFile(image, Md5.INSTANCE.toMD5(image) + "." + type).embed(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "RandomIllustration";
    }

    @Override
    public List<String> getAliases() {
        String[] aliases = {"anime", "illustration", "randomAnime","ra", "ri", "a", "i"};
        return Arrays.asList(aliases);
    }
}
