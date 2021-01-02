package net.nekomura.dcbot.ScheduledChecker

import com.google.common.collect.Lists
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.nekomura.dcbot.Config
import net.nekomura.dcbot.Enums.ConfigJsonArrayData
import net.nekomura.dcbot.Enums.ConfigLongData
import net.nekomura.dcbot.Enums.ConfigStringData
import net.nekomura.dcbot.Utils.Md5.toMD5
import net.nekomura.utils.jixiv.Enums.artwork.PixivArtworkType
import net.nekomura.utils.jixiv.Enums.artwork.PixivImageSize
import net.nekomura.utils.jixiv.Illustration
import net.nekomura.utils.jixiv.Novel
import net.nekomura.utils.jixiv.Pixiv
import okhttp3.internal.userAgent
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

fun pixivUpdateChecker(bot: JDA) {
    val followsJsonArray = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
    val follows = ArrayList<String>()
    for (i in 0 until followsJsonArray.length()) {
        follows.add(followsJsonArray.get(i).toString())
    }

    for (s in follows) {
        val followID = s.toInt()
        val file = File("./temp/pixiv/pixiv-user-${followID}.json")
        var content: String

        val p = Pixiv(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
        val user = p.getUserInfo(followID)

        val latestIllustId =
                if (user.getUserArtworks(PixivArtworkType.Illusts).isNotEmpty())
                    user.getUserArtworks(PixivArtworkType.Illusts).first()
                else
                    0
        val latestMangaId =
                if (user.getUserArtworks(PixivArtworkType.Manga).isNotEmpty())
                    user.getUserArtworks(PixivArtworkType.Manga).first()
                else
                    0
        val latestNovelId =
                if (user.getUserArtworks(PixivArtworkType.Novels).isNotEmpty())
                    user.getUserArtworks(PixivArtworkType.Novels).first()
                else
                    0

        try {
            if (file.exists()) {
                content = file.readText()
                val json = JSONObject(content)
                val eb = EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16))

                if (latestIllustId > json.getInt("illustration")) {
                    val i = Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                    val info = i.get(latestIllustId)

                    val image = info.getImage(0, PixivImageSize.Regular)

                    eb.setImage("attachment://${image.toMD5()}.${info.getImageFileFormat(0)}")
                    eb.setTitle("pixiv畫師${info.authorName}更新啦！")
                    eb.addField("作者", "[${info.authorName}](https://www.pixiv.net/users/${info.authorID})", false)
                    eb.addField("ID", "[$latestIllustId](https://www.pixiv.net/artworks/$latestIllustId)", true)
                    eb.addField("頁數", info.pageCount.toString(), true)
                    eb.addField("標題", info.title, false)
                    eb.addField("簡介", info.rawDescription, false)
                    eb.addField("標籤", Arrays.toString(info.tags), false)

                    if (!info.isNSFW)
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_PUSH_NOTIFICATION_CHANNEL))!!.sendFile(image, "${image.toMD5()}.${info.getImageFileFormat(0)}").embed(eb.build()).queue()
                    else {
                        eb.setDescription("為了防止觸及Discord禁止兒童色情暴力等，不發送圖片")
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_R18_PUSH_NOTIFICATION_CHANNEL))!!.sendMessage(eb.build()).queue()
                    }
                }
                if (latestMangaId > json.getInt("manga")) {
                    val i = Illustration(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                    val info = i.get(latestMangaId)

                    val image = info.getImage(0, PixivImageSize.Regular)

                    eb.setImage("attachment://${image.toMD5()}.${info.getImageFileFormat(0)}")
                    eb.setTitle("pixiv畫師${info.authorName}更新啦！")
                    eb.addField("作者", "[${info.authorName}](https://www.pixiv.net/users/${info.authorID})", false)
                    eb.addField("ID", "[$latestMangaId](https://www.pixiv.net/artworks/$latestMangaId)", true)
                    eb.addField("頁數", info.pageCount.toString(), true)
                    eb.addField("標題", info.title, false)
                    eb.addField("簡介", info.rawDescription, false)
                    eb.addField("標籤", Arrays.toString(info.tags), false)

                    if (!info.isNSFW)
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_PUSH_NOTIFICATION_CHANNEL))!!.sendFile(image, "${image.toMD5()}.${info.getImageFileFormat(0)}").embed(eb.build()).queue()
                    else {
                        eb.setDescription("為了防止觸及Discord禁止兒童色情暴力等，不發送圖片")
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_R18_PUSH_NOTIFICATION_CHANNEL))!!.sendMessage(eb.build()).queue()
                    }
                }
                if (latestNovelId > json.getInt("novel")) {
                    val n = Novel(Config.get(ConfigStringData.PIXIV_PHPSESSID), Config.get(ConfigStringData.USER_AGENT))
                    val info = n.get(latestNovelId)

                    val image = info.cover

                    eb.setImage("attachment://${image.toMD5()}.jpg")
                    eb.setTitle("pixiv畫師${info.authorName}更新啦！")
                    eb.addField("作者", "[${info.authorName}](https://www.pixiv.net/users/${info.authorID})", false)
                    eb.addField("ID", "[$latestNovelId](https://www.pixiv.net/novel/show.php?id=$latestNovelId)", true)
                    eb.addField("頁數", info.pageCount.toString(), true)
                    eb.addField("標題", info.title, false)
                    eb.addField("簡介", info.rawDescription, false)
                    eb.addField("標籤", Arrays.toString(info.tags), false)

                    if (!info.isNSFW)
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_PUSH_NOTIFICATION_CHANNEL))!!.sendFile(image, "${image.toMD5()}.jpg").embed(eb.build()).queue()
                    else {
                        bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_R18_PUSH_NOTIFICATION_CHANNEL))!!.sendFile(image, "${image.toMD5()}.jpg").embed(eb.build()).queue()
                    }
                }
                val newFile = JSONObject()
                newFile.put("illustration", latestIllustId)
                newFile.put("manga", latestMangaId)
                newFile.put("novel", latestNovelId)
                file.writeText(newFile.toString(), Charsets.UTF_8)
            }else {
                file.parentFile.mkdirs()
                file.createNewFile()
                val json = JSONObject()
                json.put("illustration", latestIllustId)
                json.put("manga", latestMangaId)
                json.put("novel", latestNovelId)
                file.writeText(json.toString(), Charsets.UTF_8)
            }

        }catch(e: Throwable) {
            println("$followID $e")
        }
    }
}