package net.nekomura.dcbot

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.nekomura.dcbot.Enums.*
import net.nekomura.dcbot.ScheduledChecker.minecraftUpdateChecker
import net.nekomura.dcbot.ScheduledChecker.pixivUpdateChecker
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author 貓村幻影
 * 使用之前請先將config.json中的TOKEN、OWNER、PIXIV_PUSH_NOTIFICATION_CHANNEL、PIXIV_R18_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_PUSH_NOTIFICATION_CHANNEL、MINECRAFT_UPDATE_CHECKER、PIXIV_UPDATE_CHECKER、FOLLOW_PIXIV設定好
 */
fun main(args: Array<String>) {
    if (Config.get(ConfigStringData.TOKEN)!!.isEmpty()) {
        val scanner = Scanner(System.`in`)
        val config = Config.getConfig()
        print("請輸入你的Discord機器人的Token: ")
        config.put(ConfigStringData.TOKEN.toString(), scanner.next())
        Config.writeConfig(config.toString())
    }
    if (Config.get(ConfigLongData.OWNER) == 0L) {
        val scanner = Scanner(System.`in`)
        val config = Config.getConfig()
        print("請輸入你的Discord帳號ID: ")
        config.put(ConfigLongData.OWNER.toString(), scanner.nextLong())
        Config.writeConfig(config.toString())
    }

    val bot = JDABuilder.createDefault(Config.get(ConfigStringData.TOKEN))
            .addEventListeners(CommandListener())
            .build()

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