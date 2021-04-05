package net.nekomura.dcbot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.utils.jixiv.IllustrationInfo;
import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;

import java.util.Arrays;

public class PixivUrlListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String raw = event.getMessage().getContentRaw();

        if (raw.matches("http[s]?://(www\\.)?pixiv\\.net/artworks/[0-9]+")) {
            try {
                String[] args = raw.split("/");

                IllustrationInfo info = Illustration.getInfo(Integer.parseInt(args[args.length - 1]));
                EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));

                if (info.isNSFW() && !event.getChannel().isNSFW()) {
                    return;
                }

                event.getChannel().sendTyping().queue();
                byte[] image = info.getImage(0, PixivImageSize.ORIGINAL);

                eb.setImage("attachment://" + info.getId() + "." + info.getImageFileFormat(0));
                eb.addField("標題", info.getTitle(), false);
                eb.addField("簡介", info.getRawDescription(), false);
                eb.addField("標籤", Arrays.toString(info.getTags()), false);
                eb.addField("作者", "[" + info.getAuthorName() + "](https://www.pixiv.net/users/" + info.getAuthorID() + ")", false);
                eb.addField("ID", "[" + args[args.length - 1] + "](https://www.pixiv.net/artworks/" + args[args.length - 1] + ")", true);
                eb.addField("頁碼", "0", true);
                eb.addField("頁數", String.valueOf(info.getPageCount()), true);

                event.getChannel().sendFile(image, info.getId() + "." + info.getImageFileFormat(0)).embed(eb.build()).queue();
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
