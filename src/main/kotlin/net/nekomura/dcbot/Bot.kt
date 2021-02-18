package net.nekomura.dcbot

import net.dv8tion.jda.api.JDABuilder
import net.nekomura.dcbot.Enums.*
import net.nekomura.dcbot.ScheduledChecker.minecraftUpdateChecker
import net.nekomura.dcbot.ScheduledChecker.pixivUpdateChecker
import net.nekomura.utils.jixiv.Jixiv
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author 貓村幻影
 * 使用之前請先將config.json中的TOKEN、OWNER、PIXIV_PUSH_NOTIFICATION_CHANNEL、PIXIV_R18_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_UPDATE_CHECKER、PIXIV_UPDATE_CHECKER、FOLLOW_PIXIV設定好
 */
fun main(args: Array<String>) {
    if (Config.getConfig().getBoolean("first_use")) {
        val scanner = Scanner(System.`in`)
        if (Config.get(ConfigStringData.TOKEN).isNullOrEmpty()) {
            val config = Config.getConfig()
            print("請輸入你的Discord機器人的Token: ")
            config.put(ConfigStringData.TOKEN.toString(), scanner.next())
            Config.writeConfig(config.toString())
        }
        if (Config.get(ConfigLongData.OWNER) == 0L) {
            val config = Config.getConfig()
            print("請輸入你的Discord帳號ID: ")
            config.put(ConfigLongData.OWNER.toString(), scanner.nextLong())
            Config.writeConfig(config.toString())
        }
        if (Config.get(ConfigStringData.PIXIV_PHPSESSID).isNullOrEmpty()) {
            val config = Config.getConfig()
            print("請輸入你pixiv的PHPSESSID(沒有的話直接Enter就行): ")
            config.put(ConfigStringData.PIXIV_PHPSESSID.toString(), scanner.nextLine())
            Config.writeConfig(config.toString())
        }

        scanner.close()

        val config = Config.getConfig()
        config.put("first_use", false)  //下次開啟時不再詢問
        Config.writeConfig(config.toString())
    }

    val bot = JDABuilder.createDefault(Config.get(ConfigStringData.TOKEN))
            .addEventListeners(CommandListener())
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