package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.Enums.ConfigStringData;
import net.nekomura.dcbot.Utils.Md5;
import net.nekomura.dcbot.commands.Managers.CommandContext;
import net.nekomura.dcbot.commands.Managers.ICommand;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.Illustration;
import net.nekomura.utils.jixiv.IllustrationInfo;

import java.util.Arrays;
import java.util.List;

public class PixivIllustration implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        Illustration i = new Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT));
        IllustrationInfo info = i.get(Integer.parseInt(ctx.args.get(0)));

        EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));

        if (info.isNSFW() && !ctx.event.getChannel().isNSFW()) {
            eb.setDescription("該作品為R18作品，請在限制級頻道裡執行此指令或使用其他作品ID");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
        }else {
            byte[] image = info.getImage(0, PixivImageSize.Original);

            eb.setImage("attachment://" + Md5.INSTANCE.toMD5(image) + "." + info.getImageFileFormat(0));
            eb.addField("標題", info.getTitle(), false);
            eb.addField("簡介", info.getRawDescription(), false);
            eb.addField("標籤", Arrays.toString(info.getTags()), false);
            eb.addField("作者", "[" + info.getAuthorName() + "](https://www.pixiv.net/users/" + info.getAuthorID() + ")", false);
            eb.addField("ID", "[" + ctx.args.get(0) + "](https://www.pixiv.net/artworks/" + ctx.args.get(0) + ")", true);
            eb.addField("頁數", String.valueOf(info.getPageCount()), true);
            ctx.event.getChannel().sendFile(image, Md5.INSTANCE.toMD5(image) + "." + info.getImageFileFormat(0)).embed(eb.build()).queue();
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
