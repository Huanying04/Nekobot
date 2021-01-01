package net.nekomura.dcbot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.nekomura.dcbot.Enums.*
import net.nekomura.dcbot.Utils.Md5.toMD5
import net.nekomura.utils.jixiv.Enums.*
import net.nekomura.utils.jixiv.Illustration
import net.nekomura.utils.jixiv.Pixiv
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class CommandListener: ListenerAdapter() {

    private var eb = EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16))

    override fun onMessageReceived(event: MessageReceivedEvent) {
        eb = EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16))

        val prefix = Config.get(ConfigStringData.PREFIX)

        val jda = event.jda

        val author = event.author
        val message = event.message
        val channel = event.channel
        val textChannel = event.textChannel

        val msg = message.contentDisplay

        val command: String =
            if (msg.startsWith(prefix!!)) msg.substring(prefix.length)
            else msg

        val isBot = author.isBot || message.isWebhookMessage

        if (isBot) {
            return
        }

        try {
            if (event.isFromType(ChannelType.TEXT)) {
                val guild = event.guild
                val textChannel = event.textChannel
                val member = event.member

                val name = if (message.isWebhookMessage) author.name
                else member?.effectiveName
                println("(${guild.name})[${textChannel.name}]<$name>: $msg")
            }else if (event.isFromType(ChannelType.PRIVATE)) {
                val privateChannel = event.privateChannel
                println("[PRIV]<${author.name}>: $msg")
            }

            //下面開始寫指令

            if (msg.startsWith(prefix))
                if (command == "shutdown"
                    && event.author.idLong == Config.get(ConfigLongData.OWNER)) {

                    event.jda.shutdownNow()

                    println("Shutdown done!")

                    exitProcess(0)

                }else if (command.startsWith("setting")
                        && event.author.idLong == Config.get(ConfigLongData.OWNER)) {
                    val frag = command.split(" ")
                    if (frag.size >= 4) {
                        when (frag[1]) {
                            "pixiv" -> {
                                when (frag[2]) {
                                    "follow" -> {
                                        if (frag[3] == "add" && frag.size >= 5) {
                                            val add = frag[4].toInt()
                                            val config = Config.getConfig()
                                            val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                            if (array.indexOf(add) == -1) {  //不存在
                                                array.put(add)
                                                config.put(ConfigJsonArrayData.FOLLOW_PIXIV.toString(), array)
                                                Config.writeConfig(config.toString())
                                                channel.sendMessage("添加成功! `$add`").queue()
                                            }else {  //存在
                                                channel.sendMessage("`$add`已經存在").queue()
                                            }
                                        }else if (frag[3] == "list") {
                                            val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                            val follow = Config.jsonArrayToArrayList(array) as ArrayList<Int>
                                            val sb = StringBuffer("> **pixiv推播用戶清單**")
                                            for (i in follow) {
                                                sb.append("\n$i")
                                            }
                                            channel.sendMessage(sb.toString()).queue()
                                        }else if (frag[3] == "remove" && frag.size >= 5) {
                                            val remove = frag[4].toInt()
                                            val config = Config.getConfig()
                                            val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                            if (array.indexOf(remove) != -1) {
                                                array.remove(array.indexOf(remove))
                                                config.put(ConfigJsonArrayData.FOLLOW_PIXIV.toString(), array)
                                                Config.writeConfig(config.toString())
                                                channel.sendMessage("成功移除! `$remove`").queue()
                                            }else {
                                                channel.sendMessage("`$remove`不存在").queue()
                                            }
                                        }
                                    }
                                }
                            }
                            "listener" -> {
                                when (frag[2]) {
                                    "pixiv" -> {
                                        val switch = frag[3].toBoolean()
                                        val config = Config.getConfig()
                                        config.put(ConfigBooleanData.PIXIV_UPDATE_CHECKER.toString(), switch)
                                        Config.writeConfig(config.toString())
                                        channel.sendMessage("已將`PIXIV_UPDATE_CHECKER`設為`$switch`").queue()
                                    }
                                    "minecraft" -> {
                                        val switch = frag[3].toBoolean()
                                        val config = Config.getConfig()
                                        config.put(ConfigBooleanData.MINECRAFT_UPDATE_CHECKER.toString(), switch)
                                        Config.writeConfig(config.toString())
                                        channel.sendMessage("已將`MINECRAFT_UPDATE_CHECKER`設為`$switch`").queue()
                                    }
                                }
                            }
                        }
                    }
                }else if (command == "meow") {

                    val sb = StringBuffer()
                    val meow = arrayOf("喵", " ", "")
                    val end = arrayOf("！", "？", "", "喵")

                    val max = (2..12).random()

                    for (i in 0..max) {
                        if (i != max) {
                            sb.append(meow[meow.indices.random()])
                        }else if (i == max && sb.toString().isEmpty()) {
                            sb.append(arrayOf("！", "？", "喵").random())
                        }else {
                            sb.append(end.random())
                        }
                    }

                    channel.sendMessage(sb.toString()).queue()

                }
        }catch (e: Throwable) {
            channel.sendMessage("錯誤發生！\r\n$e\r\n請聯繫管理員！").queue()
        }
    }
}
