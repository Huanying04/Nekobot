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
            if (msg.startsWith(prefix!!)) msg.substring(1)
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
                }else if (command == "hi") {

                    channel.sendMessage("你好呀!").queue()

                }else if (command == "anime" || command == "illustration" || command == "i" || command == "a") {

                    val mode = PixivSearchMode.SAFE

                    val pixiv = Pixiv(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))


                    val keywords = (Config.jsonArrayToArrayList(Config.get(ConfigJsonArrayData.RANDOM_PIXIV_SEARCH_KEYWORDS)) as ArrayList<String>).random()
                    val order = arrayOf(PixivSearchOrder.NEW_TO_OLD, PixivSearchOrder.OLD_TO_NEW).random()
                    val tempResult = pixiv.search(keywords, 1, PixivSearchArtworkType.Illustrations, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.Illust)
                    val lastPage = tempResult.lastPageIndex
                    val page = (1..lastPage).random()
                    val result = pixiv.search(keywords, page, PixivSearchArtworkType.Illustrations, order, mode, PixivSearchSMode.S_TAG, PixivSearchType.Illust)

                    val max = result.pageResultCount
                    val artwork = result.ids[(0 until max).random()]

                    val i = Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                    val info = i.get(artwork)

                    val artistPage = (0 until info.pageCount).random()
                    var image = info.getImage(artistPage, PixivImageSize.Original)
                    if (image.size > 8388608)
                        image = info.getImage(artistPage, PixivImageSize.Regular)
                    val type = info.getImageFileFormat(artistPage)

                    eb.setTitle("隨機pixiv圖片")
                    eb.addField("ID", "[$artwork](https://www.pixiv.net/artworks/$artwork)", true)
                    eb.setImage("attachment://${image.toMD5()}.$type")
                    channel.sendFile(image, "${image.toMD5()}.$type").embed(eb.build()).queue()
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

                }else if (command.startsWith("pixiv")) {
                    val frag = command.split(" ")

                    val illustId = frag[1].toInt()

                    val i = Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                    val info = i.get(illustId)

                    val byte = info.getImage(0, PixivImageSize.Original)

                    eb.setImage("attachment://${byte.toMD5()}.${info.getImageFileFormat(0)}")
                    eb.addField("作者", "[${info.authorName}](https://www.pixiv.net/users/${info.authorID})", false)
                    eb.addField("ID", "[$illustId](https://www.pixiv.net/artworks/$illustId)", true)
                    eb.addField("頁數", info.pageCount.toString(), true)
                    eb.addField("標題", info.title, false)
                    eb.addField("簡介", info.rawDescription, false)
                    eb.addField("標籤", Arrays.toString(info.tags), false)
                    channel.sendFile(byte, "${byte.toMD5()}.${info.getImageFileFormat(0)}").embed(eb.build()).queue()
                }else if (command.startsWith("saucenao")) {
                    val file = event.message.attachments
                    for (f in file) {
                        if (f.isImage) {
                            val url = f.url
                            val json = JSONObject(URL("https://saucenao.com/search.php?url=$url&output_type=2").readText())

                            var warning = String()
                            var result: String
                            if(json.getJSONObject("header").getInt("status") != 0)
                                channel.sendMessage("API出現錯誤\r\n錯誤內容：${json.getJSONObject("header").getString("message")}").queue()
                            else {
                                var index = 0 //JSONArray中第幾個
                                var type = 0 //哪一類(相當於SauceNao的index)
                                for (test in 0 until json.getJSONArray("results").length()) {
                                    when (json.getJSONArray("results").getJSONObject(test).getJSONObject("header").getInt("index_id")) {
                                        5 -> {
                                            //pixiv
                                            val i = Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                                            var exist: Boolean

                                            try {
                                                i.get(json.getJSONArray("results").getJSONObject(test).getJSONObject("data").getInt("pixiv_id"))
                                                exist = true
                                            }catch (e: Throwable) {
                                                exist = false
                                            }

                                            if (exist) {
                                                index = test
                                                type = 5
                                                break
                                            }
                                        }
                                        18 -> {
                                            //H-Misc (nh之類的)
                                            index = test
                                            type = 18
                                            break
                                        }
                                        37 -> {
                                            //MangaDex
                                            //忽略
                                        }
                                        38 -> {
                                            //H-Misc (nh之類的)
                                            index = test
                                            type = 38
                                            break
                                        }
                                        else -> {
                                            index = test
                                            type = 999
                                            break
                                        }
                                    }
                                }
                                if (json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity").toFloat() < 80)
                                    warning = "結果與原圖相似度只有${json.getJSONArray("results").getJSONObject(index).getJSONObject("header").getString("similarity")}%，結果僅供參考\r\n"
                                result = when (type) {
                                    5 -> json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getJSONArray("ext_urls").getString(0)
                                    18 -> json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getString("jp_name")
                                    38 -> json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getString("jp_name")
                                    else -> json.getJSONArray("results").getJSONObject(index).getJSONObject("data").getJSONArray("ext_urls").getString(0)
                                }
                                eb.setTitle("SauceNAO搜圖", "https://saucenao.com/search.php?url=$url")
                                eb.addField("結果", result, false)
                                if (warning.isNotEmpty())
                                    eb.addField("提醒", warning, false)
                                channel.sendMessage(eb.build()).queue()
                            }
                        }else {
                            eb.setDescription("必須上傳圖片才能搜圖")
                            channel.sendMessage(eb.build()).queue()
                        }
                    }
                }
        }catch (e: Throwable) {
            channel.sendMessage("錯誤發生！\r\n$e\r\n請聯繫管理員！").queue()
        }
    }
}
