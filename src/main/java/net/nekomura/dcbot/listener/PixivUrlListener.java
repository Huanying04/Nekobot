package net.nekomura.dcbot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.utils.jixiv.IllustrationInfo;
import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PixivUrlListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PixivUrlListener.class);
    private static final Pattern urlRegex = Pattern.compile("(http(s)?://)(www\\.)?pixiv\\.net/(en/)?artworks/[0-9]+((/)?(\\?)[a-zA-Z0-9.,%_=?&#-+()\\[\\]*$~@!:/{};']*)?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String raw = event.getMessage().getContentRaw().replace("/?", "?");
        Matcher rawMatcher = urlRegex.matcher(raw);

        if (rawMatcher.find()) {
            int matchStart = rawMatcher.start(1);
            int matchEnd = rawMatcher.end();
            String url = raw.substring(matchStart, matchEnd);
            LOGGER.debug("偵測到pixiv網址，開始抓取作品資訊");

            try {
                String[] args = url.split("/");

                int id = Integer.parseInt(args[args.length - 1].split("\\?")[0]);

                LOGGER.debug("開始抓取id為{}的插畫", id);

                IllustrationInfo info = Illustration.getInfo(id);
                EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));

                if (info.isNSFW() && !event.getTextChannel().isNSFW()) {
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
                eb.addField("ID", "[" + id + "](https://www.pixiv.net/artworks/" + id + ")", true);
                eb.addField("頁碼", "0", true);
                eb.addField("頁數", String.valueOf(info.getPageCount()), true);

                event.getChannel().sendFile(image, info.getId() + "." + info.getImageFileFormat(0)).setEmbeds(eb.build()).queue();

                LOGGER.debug("訊息發送完畢");
            }catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
