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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PixivUrlListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PixivUrlListener.class);

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String raw = event.getMessage().getContentRaw();

        if (raw.matches("http[s]?://(www\\.)?pixiv\\.net/artworks/[0-9]+")) {
            LOGGER.debug("偵測到pixiv網址，開始抓取作品資訊");

            try {
                String[] args = raw.split("/");

                int id = Integer.parseInt(args[args.length - 1]);

                LOGGER.debug("開始抓取id為{}的插畫", id);

                IllustrationInfo info = Illustration.getInfo(id);
                EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));

                if (info.isNSFW() && !event.getChannel().isNSFW()) {
                    LOGGER.debug("頻道不為NSFW且作品為R18作品，取消執行指令");
                    return;
                }

                event.getChannel().sendTyping().queue();

                LOGGER.debug("下載pixiv插畫中，id為{}", id);
                byte[] image = info.getImage(0, PixivImageSize.ORIGINAL);

                if (image.length > 8388608) {
                    LOGGER.debug("檔案過大，下載大小較小的版本");
                    image = info.getImage(0, PixivImageSize.REGULAR);
                }

                LOGGER.debug("id為{}的pixiv插畫下載完成", id);

                LOGGER.debug("訊息準備發送");

                eb.setImage("attachment://" + info.getId() + "." + info.getImageFileFormat(0));
                eb.addField("標題", info.getTitle(), false);
                eb.addField("簡介", info.getRawDescription(), false);
                eb.addField("標籤", Arrays.toString(info.getTags()), false);
                eb.addField("作者", "[" + info.getAuthorName() + "](https://www.pixiv.net/users/" + info.getAuthorID() + ")", false);
                eb.addField("ID", "[" + args[args.length - 1] + "](https://www.pixiv.net/artworks/" + args[args.length - 1] + ")", true);
                eb.addField("頁碼", "0", true);
                eb.addField("頁數", String.valueOf(info.getPageCount()), true);

                event.getChannel().sendFile(image, info.getId() + "." + info.getImageFileFormat(0)).embed(eb.build()).queue();

                LOGGER.debug("訊息發送完畢");
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
