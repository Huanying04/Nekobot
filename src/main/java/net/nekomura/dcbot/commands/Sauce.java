package net.nekomura.dcbot.commands;

import jdk.nashorn.api.scripting.URLReader;
import kotlin.io.TextStreamsKt;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.Enums.ConfigStringData;
import net.nekomura.dcbot.commands.Managers.CommandContext;
import net.nekomura.dcbot.commands.Managers.ICommand;
import net.nekomura.utils.jixiv.artworks.Illustration;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Sauce implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        List<Message.Attachment> files = ctx.event.getMessage().getAttachments();
        for (Message.Attachment file: files) {
            if (file.isImage()) {
                String url = file.getUrl();
                JSONObject json = new JSONObject(TextStreamsKt.readText(new URLReader(new URL("https://saucenao.com/search.php?url=" + url + "&output_type=2"))));

                String warning = "";
                String result = "";

                if(json.getJSONObject("header").getInt("status") != 0) {
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

                    if (Float.parseFloat(json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity")) < 80) {
                        warning = "結果與原圖相似度只有" + json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity") + "%，結果僅供參考\r\n";
                    }else {
                        warning = "相似度: " + json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity") + "%\r\n";
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

                    EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
                    eb.setTitle("SauceNAO搜圖", "https://saucenao.com/search.php?url=" + url);
                    eb.addField("結果", result, false);
                    if (!warning.isEmpty())
                        eb.addField("提醒", warning, false);
                    ctx.event.getChannel().sendMessage(eb.build()).queue();
                }

            }else {
                EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
                eb.setDescription("必須上傳圖片才能搜圖");
                ctx.event.getChannel().sendMessage(eb.build()).queue();
            }
        }
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
}
