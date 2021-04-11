package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.UrlReader;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.artworks.Illustration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SauceCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SauceCommand.class);

    @Override
    public void handle(CommandContext ctx) throws Exception {
        LOGGER.debug("偵測到指令{}，開始執行指令", getName());

        Message reference = ctx.event.getMessage().getReferencedMessage();
        List<Message.Attachment> files;
        List<MessageEmbed> embeds;
        if (reference == null) {  //若不是回復訊息，則獲取該訊息的附件和embed
            LOGGER.debug("以該訊息之圖片進行搜圖");
            files = ctx.event.getMessage().getAttachments();
            embeds = ctx.event.getMessage().getEmbeds();
        }else {  //若是回復訊息，則獲取回復訊息的附件和embed
            LOGGER.debug("以回復訊息之圖片進行搜圖");
            files = reference.getAttachments();
            embeds = reference.getEmbeds();
        }

        searchByAttachmentsAndEmbeds(files, embeds, ctx);
    }

    @Override
    public String getName() {
        return "SauceNAO";
    }

    @Override
    public List<String> getAliases() {
        String[] aliases = {"sauce"};
        return Arrays.asList(aliases);
    }

    /**
     * 以附件或Embed訊息搜圖
     * @param files 附件
     * @param embeds embeds
     * @param ctx Command Context Object
     * @throws IOException
     */
    private static void searchByAttachmentsAndEmbeds(List<Message.Attachment> files, List<MessageEmbed> embeds, CommandContext ctx) throws IOException {
        if (files.isEmpty() && embeds.isEmpty()) {  //若是完全沒有圖片
            LOGGER.debug("訊息中完全沒有附帶圖片，停止執行指令。");

            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("訊息必須附帶圖片才能搜圖");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        if (!embeds.isEmpty()) {  //先以embed訊息為優先
            for (MessageEmbed embed: embeds) {  //逐項檢查embed
                MessageEmbed.Thumbnail thumbnail = embed.getThumbnail();
                MessageEmbed.ImageInfo image = embed.getImage();
                String url;
                if (image == null && thumbnail == null) {
                    //若是embed中沒有圖片，則取消執行
                    LOGGER.debug("訊息中沒有附帶圖片，停止執行指令。");

                    EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
                    eb.setDescription("訊息必須附帶圖片才能搜圖");
                    ctx.event.getChannel().sendMessage(eb.build()).queue();
                    return;
                }else if (image != null){  //如果有，則獲取圖片URL
                    url = image.getUrl();
                    LOGGER.debug("成功獲取圖片URL: {}", url);
                }else {  //同上述
                    url = thumbnail.getUrl();
                    LOGGER.debug("成功獲取圖片URL: {}", url);
                }

                LOGGER.debug("準備以SauceNAO API開始搜圖");

                search(url, ctx);
            }
        }else {  //附件圖片
            for (Message.Attachment file: files) {
                if (file.isImage()) {
                    String url = file.getUrl();
                    LOGGER.debug("成功獲取圖片URL: {}", url);

                    LOGGER.debug("準備以SauceNAO API開始搜圖");

                    search(url, ctx);
                }else {
                    LOGGER.debug("訊息中沒有附帶圖片，停止執行指令。");

                    EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
                    eb.setDescription("訊息必須附帶圖片才能搜圖");
                    ctx.event.getChannel().sendMessage(eb.build()).queue();
                }
            }
        }
    }

    private static void search(String url, CommandContext ctx) throws IOException {
        JSONObject json = new JSONObject(UrlReader.read("https://saucenao.com/search.php?url=" + url + "&output_type=2&api_key=" + Config.get(ConfigStringData.SAUCENAO_KEY)));

        String warning = "";
        String result = "";

        if(json.getJSONObject("header").getInt("status") != 0) {
            LOGGER.debug("API出現錯誤，傳送錯誤訊息。");
            ctx.event.getChannel().sendMessage("API出現錯誤\r\n錯誤內容：" + json.getJSONObject("header").getString("message")).queue();
        }else {
            int index = 0; //JSONArray中第幾個
            int type = 0; //哪一類(相當於SauceNao的index)

            for (int test = 0; test < json.getJSONArray("results").length(); test++) {
                int index_id = json.getJSONArray("results").getJSONObject(test).getJSONObject("header").getInt("index_id");
                if (index_id == 5) {
                    //pixiv
                    boolean exist;

                    try {
                        Illustration.getInfo(json.getJSONArray("results").getJSONObject(test).getJSONObject("data").getInt("pixiv_id"));
                        exist = true;
                    }catch (Throwable e) {
                        exist = false;
                    }

                    if (exist) {
                        index = test;
                        type = 5;
                        break;
                    }
                }else if (index_id == 18) {
                    //H-Misc (nh之類的)
                    index = test;
                    type = 18;
                    break;
                }else if (index_id == 37) {
                    //MangaIndex
                    //ignore
                    continue;
                }else if (index_id == 38) {
                    //H-Misc (nh之類的)
                    index = test;
                    type = 38;
                    break;
                }else if (index_id == 41) {
                    //Twitter
                    index = test;
                    type = 41;
                    break;
                }else {
                    index = test;
                    type = 5;
                    break;
                }
            }

            String similarity = json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity");

            if (Float.parseFloat(json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity")) < 80) {
                warning = "結果與原圖相似度只有" + similarity + "%，結果僅供參考\r\n";
            }else {
                warning = "相似度: " + similarity + "%\r\n";
            }
            switch (type) {
                case 18:
                case 38:
                    result = json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getString("jp_name");
                    break;
                case 5:
                case 41:
                default:
                    result = json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getJSONArray("ext_urls").getString(0);
                    break;
            }

            LOGGER.debug("成功獲得結果: {}，相似度為{}%", result, similarity);

            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setTitle("SauceNAO搜圖", "https://saucenao.com/search.php?url=" + url);
            eb.addField("結果", result, false);
            if (!warning.isEmpty())
                eb.addField("提醒", warning, false);
            ctx.event.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
