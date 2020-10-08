package net.nekomura.dcbot.ScheduledChecker

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.nekomura.dcbot.Config
import net.nekomura.dcbot.Enums.ConfigJsonArrayData
import net.nekomura.dcbot.Enums.ConfigLongData
import net.nekomura.dcbot.Enums.PiXivUserArtistType
import net.nekomura.dcbot.Utils.Md5
import net.nekomura.dcbot.Utils.Md5.toMD5
import net.nekomura.dcbot.Utils.PiXiv
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.ArrayList

fun pixivUpdateChecker(bot: JDA) {
    val followIDsJsonArray = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)

    val followIDs = ArrayList<String>()

    for (i in 0 until followIDsJsonArray.length()) {
        followIDs.add(followIDsJsonArray.get(i).toString())
    }

    for (s in followIDs) {
        val followID = s.toLong()
        var file = File("./temp/pixiv/pixiv-user-${followID}.json")
        file.parentFile.mkdirs()
        var content: String
        val userNow = PiXiv.getUserProfileInfo(followID)

        try {
            if (file.exists()) {
                content = file.readText()
                val jsonNow = JSONObject(userNow)
                val jsonOld = JSONObject(content)
                var illustsList = ArrayList<Long>()
                var mangaList = ArrayList<Long>()
                var illustsOldList = ArrayList<Long>()
                var mangaOldList = ArrayList<Long>()

                if ((jsonNow.getJSONObject("body").get("illusts") is JSONObject
                                && jsonOld.getJSONObject("body").get("illusts") is JSONObject)
                        || (jsonNow.getJSONObject("body").get("manga") is JSONObject
                                && jsonOld.getJSONObject("body").get("manga") is JSONObject)) {
                    if (jsonNow.getJSONObject("body").get("illusts") is JSONObject
                            && jsonOld.getJSONObject("body").get("illusts") is JSONObject) {
                        illustsList = PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.ILLUSTS)
                        illustsOldList = PiXiv.getUserArtistList(jsonOld, PiXivUserArtistType.ILLUSTS)
                    }
                    if (jsonNow.getJSONObject("body").get("manga") is JSONObject
                            && jsonOld.getJSONObject("body").get("manga") is JSONObject) {
                        mangaList = PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.MANGA)
                        mangaOldList = PiXiv.getUserArtistList(jsonOld, PiXivUserArtistType.MANGA)
                    }
                    if ((jsonNow.getJSONObject("body").get("illusts") is JSONObject
                                    && jsonOld.getJSONObject("body").get("illusts") is JSONObject
                                    && illustsList.last() > illustsOldList.last())
                            || (jsonNow.getJSONObject("body").get("manga") is JSONObject
                                    && jsonOld.getJSONObject("body").get("manga") is JSONObject
                                    && mangaList.last() > mangaOldList.last())) {
                        val illustId = if (illustsList.last() > illustsOldList.last()) {
                            illustsList.last()
                        } else {
                            mangaList.last()
                        }

                        val eb = EmbedBuilder().setColor(0xde452a)

                        val byte = PiXiv.getImage(illustId)

                        eb.setImage("attachment://${byte.toMD5()}.${PiXiv.getImageType(illustId)}")
                        eb.setTitle("pixiv畫師${PiXiv.getAuthorName(illustId)}更新啦！")
                        eb.addField("作者", "[${PiXiv.getAuthorName(illustId)}](https://www.pixiv.net/users/${PiXiv.getUserID(illustId)})", false)
                        eb.addField("ID", "[$illustId](https://www.pixiv.net/artworks/$illustId)", true)
                        eb.addField("頁數", PiXiv.getPageCount(illustId).toString(), true)
                        eb.addField("標題", PiXiv.getTitle(illustId), false)
                        eb.addField("簡介", PiXiv.getRawDescription(illustId), false)
                        eb.addField("標籤", PiXiv.getTagsArrayList(illustId).toString(), false)
                        if (!PiXiv.isAdult(illustId))
                            bot.getTextChannelById(759391018729734164)!!.sendFile(byte, "${byte.toMD5()}.${PiXiv.getImageType(illustId)}").embed(eb.build()).queue()
                        else {
                            eb.setDescription("為了防止觸及Discord禁止兒童色情暴力等，不發送圖片")
                            bot.getTextChannelById(759751788009095188)!!.sendMessage(eb.build()).queue()
                        }
                    }
                } else if ((jsonNow.getJSONObject("body").get("illusts") is JSONObject
                                && jsonOld.getJSONObject("body").get("illusts") is JSONArray) //JSONArray -> length = 0
                        || (jsonNow.getJSONObject("body").get("manga") is JSONObject
                                && jsonOld.getJSONObject("body").get("manga") is JSONArray)) {

                    illustsList = if (jsonNow.getJSONObject("body").get("illusts") is JSONObject
                            && PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.ILLUSTS).size > jsonOld.getJSONObject("body").getJSONArray("illusts").length()) {
                        PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.ILLUSTS)
                    } else {
                        ArrayList<Long>()
                    }

                    mangaList = if (jsonNow.getJSONObject("body").get("manga") is JSONObject) {
                        PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.MANGA)
                    } else {
                        ArrayList<Long>()
                    }

                    if (illustsList.size > jsonOld.getJSONObject("body").getJSONArray("illusts").length()
                            || mangaList.size > jsonOld.getJSONObject("body").getJSONArray("manga").length()) {
                        val illustId = if (illustsList.size > jsonOld.getJSONObject("body").getJSONArray("illusts").length()) {
                            illustsList = PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.ILLUSTS)
                            illustsList.last()
                        } else {
                            mangaList = PiXiv.getUserArtistList(jsonNow, PiXivUserArtistType.MANGA)
                            mangaList.last()
                        }

                        val eb = EmbedBuilder().setColor(0xde452a)

                        val byte = PiXiv.getImage(illustId)

                        eb.setImage("attachment://${byte.toMD5()}.${PiXiv.getImageType(illustId)}")
                        eb.setTitle("pixiv畫師${PiXiv.getAuthorName(illustId)}更新啦！")
                        eb.addField("作者", "[${PiXiv.getAuthorName(illustId)}](https://www.pixiv.net/users/${PiXiv.getUserID(illustId)})", false)
                        eb.addField("ID", "[$illustId](https://www.pixiv.net/artworks/$illustId)", true)
                        eb.addField("頁數", PiXiv.getPageCount(illustId).toString(), true)
                        eb.addField("標題", PiXiv.getTitle(illustId), false)
                        eb.addField("簡介", PiXiv.getRawDescription(illustId), false)
                        eb.addField("標籤", PiXiv.getTagsArrayList(illustId).toString(), false)
                        if (!PiXiv.isAdult(illustId))  //not r-18 or r-18g
                            bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_PUSH_NOTIFICATION_CHANNEL))!!.sendFile(byte, "${byte.toMD5()}.${PiXiv.getImageType(illustId)}").embed(eb.build()).queue()
                        else {
                            eb.setDescription("為了防止觸及Discord禁止兒童色情暴力等，不發送圖片")
                            bot.getTextChannelById(Config.get(ConfigLongData.PIXIV_R18_PUSH_NOTIFICATION_CHANNEL))!!.sendMessage(eb.build()).queue()
                        }
                    }
                }
                file.writeText(userNow, Charsets.UTF_8)
            } else {
                file.createNewFile()
                file.writeText(userNow, Charsets.UTF_8)
            }
        }catch (e: Throwable) {
            println("$followID $e")
        }
    }
}