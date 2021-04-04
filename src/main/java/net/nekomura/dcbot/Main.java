package net.nekomura.dcbot;

import net.dv8tion.jda.api.JDABuilder;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.listener.CommandsListener;
import net.nekomura.dcbot.listener.PixivUrlListener;
import net.nekomura.utils.jixiv.Jixiv;

import javax.security.auth.login.LoginException;

public class Main {
    /**
     * @author 貓村幻影
     * 使用之前請先將config.json中的TOKEN、OWNER、PIXIV_PUSH_NOTIFICATION_CHANNEL、PIXIV_R18_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_UPDATE_CHECKER、PIXIV_UPDATE_CHECKER、FOLLOW_PIXIV、SAUCENAO_KEY設定好
     */
    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault(Config.get(ConfigStringData.TOKEN))
                .addEventListeners(new CommandsListener(), new PixivUrlListener())
                .build();

        Jixiv.loginByCookie(Config.get(ConfigStringData.PIXIV_PHPSESSID));
        Jixiv.setUserAgent(Config.get(ConfigStringData.USER_AGENT));
    }
}
