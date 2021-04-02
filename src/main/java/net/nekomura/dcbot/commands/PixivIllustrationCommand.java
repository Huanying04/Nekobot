package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.IllustrationInfo;

import java.util.Arrays;
import java.util.List;

public class PixivIllustrationCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        IllustrationInfo info = Illustration.getInfo(Integer.parseInt(ctx.args.get(0)));

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));

        if (info.isNSFW() && !ctx.event.getChannel().isNSFW()) {
            eb.setDescription("該作品為R18作品，請在限制級頻道裡執行此指令或使用其他作品ID");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
        }else {
            int page;
            boolean announcement = false;

            if (ctx.args.size() == 1) {
                page = 0;
            }else if (Integer.parseInt(ctx.args.get(1)) >= info.getPageCount()) {
                page = 0;
                announcement = true;
            }else {
                page = Integer.parseInt(ctx.args.get(1));
            }

            byte[] image = info.getImage(page, PixivImageSize.Original);

            eb.setImage("attachment://" + info.getId() + "." + info.getImageFileFormat(0));
            eb.addField("標題", info.getTitle(), false);
            eb.addField("簡介", info.getRawDescription(), false);
            eb.addField("標籤", Arrays.toString(info.getTags()), false);
            eb.addField("作者", "[" + info.getAuthorName() + "](https://www.pixiv.net/users/" + info.getAuthorID() + ")", false);
            eb.addField("ID", "[" + ctx.args.get(0) + "](https://www.pixiv.net/artworks/" + ctx.args.get(0) + ")", true);
            eb.addField("頁碼", String.valueOf(page), true);
            eb.addField("頁數", String.valueOf(info.getPageCount()), true);
            if (announcement) {
                eb.addField("提醒", "您輸入的頁碼`" + ctx.args.get(1) + "`大於或等於本作品總頁數(初始頁由`0`算起)`" + info.getPageCount() + "`。故將使用第`0`頁代替。", false);
            }
            ctx.event.getChannel().sendFile(image, info.getId() + "." + info.getImageFileFormat(0)).embed(eb.build()).queue();
        }
    }

    @Override
    public String getName() {
        return "pixiv";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }
}
