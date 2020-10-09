package net.nekomura.dcbot.ScheduledChecker

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.nekomura.dcbot.Config
import net.nekomura.dcbot.Enums.ConfigLongData
import net.nekomura.dcbot.Enums.ConfigStringData
import net.nekomura.dcbot.Utils.Minecraft
import org.json.JSONObject
import java.io.File

fun minecraftUpdateChecker(bot: JDA) {
    val file = File("./temp/minecraft/version_manifest.json")
    file.parentFile.mkdirs()
    val versionManifestNow = Minecraft.getVersionManifest()

    if (!file.createNewFile()) {  //file exists
        val versionManifestOld = JSONObject(file.readText())

        if (versionManifestNow.getJSONObject("latest").toString() != versionManifestOld.getJSONObject("latest").toString()) {
            val eb = EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16))
            if (versionManifestNow.getJSONObject("latest").getString("release") != versionManifestOld.getJSONObject("latest").getString("release")) {
                eb.setTitle("Minecraft發布最新版本了！")
            }else if (versionManifestNow.getJSONObject("snapshot").getString("snapshot") != versionManifestOld.getJSONObject("latest").getString("release")) {
                eb.setTitle("Minecraft發布最新快照了！")
            }
            eb.addField("當前正式版本", Minecraft.getLatestVersion(), true)
            eb.addField("當前快照版本", Minecraft.getLatestSnapshot(), true)
            eb.setThumbnail("https://static.wikia.nocookie.net/minecraft_zh_gamepedia/images/6/6a/Grass_Block_JE6_BE5.png/revision/latest")
            bot.getTextChannelById(Config.get(ConfigLongData.MINECRAFT_PUSH_NOTIFICATION_CHANNEL))!!.sendMessage(eb.build()).queue()
        }
        file.writeText(versionManifestNow.toString(), Charsets.UTF_8)
    }else {  //file not exists
        file.writeText(versionManifestNow.toString(), Charsets.UTF_8)
    }
}