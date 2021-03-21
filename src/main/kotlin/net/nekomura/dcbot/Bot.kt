package net.nekomura.dcbot

import net.dv8tion.jda.api.JDABuilder
import net.nekomura.dcbot.enums.*
import net.nekomura.dcbot.scheduledChecker.minecraftUpdateChecker
import net.nekomura.dcbot.scheduledChecker.pixivUpdateChecker
import net.nekomura.utils.jixiv.Jixiv
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author 貓村幻影
 * 使用之前請先將config.json中的TOKEN、OWNER、PIXIV_PUSH_NOTIFICATION_CHANNEL、PIXIV_R18_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_UPDATE_CHECKER、PIXIV_UPDATE_CHECKER、FOLLOW_PIXIV設定好
 */
fun main(args: Array<String>) {
    val bot = JDABuilder.createDefault(Config.get(ConfigStringData.TOKEN))
            .addEventListeners(Listener())
            .build()

    Jixiv.loginByCookie(Config.get(ConfigStringData.PIXIV_PHPSESSID))
    Jixiv.setUserAgent(Config.get(ConfigStringData.USER_AGENT))

    val scheduledChecker: ScheduledExecutorService = Executors.newScheduledThreadPool(5)
    scheduledChecker.scheduleWithFixedDelay({
        try{
            if (Config.get(ConfigBooleanData.PIXIV_UPDATE_CHECKER))
                pixivUpdateChecker(bot)
            if (Config.get(ConfigBooleanData.MINECRAFT_UPDATE_CHECKER))
                minecraftUpdateChecker(bot)
        }catch (e: Throwable) {
            println("$e")
        }
    }, 5L, Config.get(ConfigLongData.SCHEDULED_CHECKER_DELAY), TimeUnit.SECONDS)
}