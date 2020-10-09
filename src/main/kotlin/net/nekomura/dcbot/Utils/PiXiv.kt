package net.nekomura.dcbot.Utils

import net.nekomura.dcbot.Config.get
import net.nekomura.dcbot.Enums.*
import net.nekomura.dcbot.Sort
import net.nekomura.dcbot.Utils.StringUtils.subString
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder
import kotlin.math.ceil

object PiXiv {
    /**
     * 獲取插畫或漫畫網頁頁面的HTML
     * @param id 作品ID
     */
    fun getArtworkPageHtml(id: Long): String{
        val okHttpClient = OkHttpClient()
        val requestBuilder = Request.Builder().url("https://www.pixiv.net/artworks/${id}")

        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        return res.body!!.string()
    }

    /**
     * 獲取插畫或漫畫的Preload Data
     * @param id 作品ID
     */
    fun getArtworkPreloadData(id: Long): JSONObject{
        val html = getArtworkPageHtml(id)
        val targetJsonString = html.subString("<meta name=\"preload-data\" id=\"meta-preload-data\" content='", "'>")
        return JSONObject(targetJsonString)
    }

    /**
     * 獲取插畫或漫畫的第一頁的圖片網址
     * @param id 作品ID
     */
    private fun getImageUrl(id: Long): String {
        return getImageUrl(id, PiXivImageUrlType.REGULAR)
    }

    /**
     * 獲取插畫或漫畫的第page頁的圖片網址.
     * @param id 作品ID
     * @param page 頁碼
     */
    private fun getImageUrl(id: Long, page: Int): String {
        return getImageUrl(id, page, PiXivImageUrlType.REGULAR)
    }

    /**
     * 獲取插畫或漫畫的第一頁的圖片網址
     * @param id 作品ID
     * @param type 圖片大小類型
     */
    private fun getImageUrl(id: Long, type: PiXivImageUrlType): String {
        val json = getArtworkPreloadData(id)
        return json.getJSONObject("illust").getJSONObject(id.toString()).getJSONObject("urls").getString(type.name.toLowerCase())
    }

    /**
     * 獲取插畫或漫畫的第page頁的圖片網址
     * @param id 作品ID
     * @param page 頁碼
     * @param type 圖片大小類型
     */
    private fun getImageUrl(id: Long, page: Int, type: PiXivImageUrlType): String {
        val json = getArtworkPreloadData(id)
        val page0 = json.getJSONObject("illust").getJSONObject(id.toString()).getJSONObject("urls").getString(type.name.toLowerCase())
        val urlSplit = page0.split("${id}_p0")
        return urlSplit[0] + "${id}_p" + page + urlSplit[1]
    }

    /**
     * 獲取插畫或漫畫的第page頁的圖片
     * @param id 作品ID
     */
    fun getImage(id: Long): ByteArray {
        return getImage(id, 0)
    }

    /**
     * 獲取插畫或漫畫的第page頁的圖片
     * @param id 作品ID
     * @param page 頁碼
     * @param type 圖片大小類型
     */
    fun getImage(id: Long, page: Int, type: PiXivImageUrlType): ByteArray{
        val okHttpClient = OkHttpClient()
        val requestBuilder = Request.Builder().url(getImageUrl(id, page, type))
        requestBuilder.addHeader("Referer", "https://www.pixiv.net/artworks")
        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()
        return res.body!!.bytes()
    }

    /**
     * 獲取插畫或漫畫的第page頁的圖片
     * @param id 作品ID
     * @param page 頁碼
     */
    fun getImage(id: Long, page: Int): ByteArray{
        return getImage(id, page, PiXivImageUrlType.REGULAR)
    }

    /**
     * 獲取插畫或漫畫的第一頁的圖片類型
     * @param id 作品ID
     */
    fun getImageType(id: Long): String {
        return getImageUrl(id).split(".").last()
    }

    /**
     * 獲取插畫或漫畫的標題
     * @param id 作品ID
     */
    fun getTitle(id: Long): String{
        val json = JSONObject(getArtworkInfo(id))
        return json.getJSONObject("illust").getJSONObject(id.toString()).getString("title")
    }

    /**
     * 獲取插畫或漫畫的描述
     * @param id 作品ID
     */
    fun getDescription(id: Long): String{
        val json = JSONObject(getArtworkInfo(id))
        return json.getJSONObject("illust").getJSONObject(id.toString()).getString("description")
    }

    /**
     * 獲取插畫或漫畫的人類可讀描述
     * @param id 作品ID
     */
    fun getRawDescription(id: Long): String{
        val json = getArtworkPreloadData(id)
        val description = json.getJSONObject("illust").getJSONObject(id.toString()).getString("description")

        return Jsoup.parse(StringEscapeUtils.unescapeHtml4(description).replace(Regex("(?i)<br[^>]*>"), "br2n")).text().replace("br2n", "\r\n")
    }

    /**
     * 獲取插畫或漫畫的標籤
     * @param id 作品ID
     */
    private fun getTags(id: Long): JSONArray {
        val json = getArtworkPreloadData(id)
        return json.getJSONObject("illust").getJSONObject(id.toString()).getJSONObject("tags").getJSONArray("tags")
    }

    /**
     * 獲取插畫或漫畫的標籤
     * @param id 作品ID
     */
    fun getTagsArrayList(id: Long): ArrayList<String> {
        val jsonArray = getTags(id)
        val list = ArrayList<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getJSONObject(i).getString("tag"))
        }
        return list
    }

    /**
     * 獲取插畫或漫畫的頁數
     * @param id 作品ID
     */
    fun getPageCount(id: Long): Int{
        return JSONObject(getArtworkInfo(id)).getJSONObject("illust").getJSONObject("$id").getInt("pageCount")
    }

    /**
     * 此作品是否為R-18或R-18G
     * @param id 作品ID
     */
    fun isAdult(id: Long): Boolean{
        return getTagsArrayList(id).contains("R-18") || getTagsArrayList(id).contains("R-18G")
    }

    /**
     * 此作品是否為R-18
     * @param id 作品ID
     */
    fun isR18(id: Long): Boolean{
        return getTagsArrayList(id).contains("R-18")
    }

    /**
     * 此作品是否為R-18G
     * @param id 作品ID
     */
    fun isR18G(id: Long): Boolean{
        return getTagsArrayList(id).contains("R-18G")
    }

    /**
     * 獲取插畫或漫畫的作者ID
     * @param illustrationId 作品ID
     */
    fun getUserID(illustrationId: Long): Long{
        val json = getArtworkPreloadData(illustrationId)
        return json.getJSONObject("illust").getJSONObject(illustrationId.toString()).getString("userId").toLong()
    }

    /**
     * 獲取插畫或漫畫的作者名
     * @param illustrationId 作品ID
     */
    fun getAuthorName(illustrationId: Long): String{
        val json = getArtworkPreloadData(illustrationId)
        return json.getJSONObject("illust").getJSONObject(illustrationId.toString()).getString("userName")
    }

    /**
     * 獲取用戶的Profile Info
     * @param id 用戶ID
     */
    fun getUserProfileInfo(id: Long): String {
        val url = "https://www.pixiv.net/ajax/user/$id/profile/all"

        val okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder().url(url)
        requestBuilder.addHeader(":authority", "www.pixiv.net")
        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        return res.body!!.string()
    }

    /**
     * 獲取作品的資訊
     * @param id 作品ID
     */
    fun getArtworkInfo(id: Long): String{
        val url = "https://www.pixiv.net/artworks/$id"
        val okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder().url(url)
        requestBuilder.addHeader(":authority", "www.pixiv.net")
        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        return res.body!!.string().subString("<meta name=\"preload-data\" id=\"meta-preload-data\" content='", "'>")
    }

    /**
     * 獲取用戶的名稱
     * @param id 用戶ID
     */
    fun getUserName(id: Long): String {
        val url = "https://www.pixiv.net/users/$id"
        val okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder().url(url)
        requestBuilder.addHeader(":authority", "www.pixiv.net")
        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        val body = res.body!!.string()

        return JSONObject(body.subString("<meta name=\"preload-data\" id=\"meta-preload-data\" content='", "'>")).getJSONObject("user").getJSONObject(id.toString()).getString("name")
    }

    /**
     * 獲取指定的排行榜
     * @param mode 排行棒模式
     */
    fun rank(mode: PiXivRankMode): String{
        val url = "https://www.pixiv.net/ranking.php?mode=${mode.toString().toLowerCase()}&format=json"
        val okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder().url(url)
        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        return res.body!!.string()
    }

    /**
     * 獲取當天的排行榜
     */
    fun rank(): String{
        return rank(PiXivRankMode.DAILY)
    }

    /**
     * 獲取指定用戶的所有作品
     * @param jsonObject 用戶的Profile Info的JSON
     * @param piXivUserArtistType 欲獲取的作品類型
     */
    fun getUserArtistList(jsonObject: JSONObject, piXivUserArtistType: PiXivUserArtistType): ArrayList<Long> {
        val artistKeys = jsonObject.getJSONObject("body").getJSONObject(piXivUserArtistType.name.toLowerCase()).keys()
        val illustListNow = java.util.ArrayList<Long>()

        for (it in artistKeys) {
            illustListNow.add(it.toLong())
        }

        return Sort.bubble(illustListNow)
    }

    /**
     * pixiv搜尋
     * @param keywords 搜尋關鍵字
     * @param page 搜尋結果之頁數，pixiv搜尋一頁最多60個作品
     * @param artistType 欲搜尋的作品類型
     * @param order 排序方式
     * @param mode 搜尋模式
     * @param sMode 關鍵字搜尋模式
     * @param type 搜尋類型
     */
    fun search(keywords: String, page: Int, artistType: PiXivSearchArtistType, order: PiXivSearchOrder, mode: PiXivSearchMode, sMode: PiXivSearchSMode, type: PiXivSearchType): String {
        val url = "https://www.pixiv.net/ajax/search/${artistType.name.toLowerCase()}/${URLEncoder.encode(keywords, Charsets.UTF_8.name())}?word=${URLEncoder.encode(keywords, Charsets.UTF_8.name())}&order=$order&mode=${mode.name.toLowerCase()}&p=$page&s_mode=${sMode.name.toLowerCase()}&type=${type.name.toLowerCase()}&lang=zh_tw"
        val okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder().url(url)
        requestBuilder.addHeader("Referer", "https://www.pixiv.net")
        requestBuilder.addHeader("cookie", "PHPSESSID=${get(ConfigStringData.PIXIV_PHPSESSID)}")
        requestBuilder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

        requestBuilder.method("GET", null)

        val res = okHttpClient.newCall(requestBuilder.build()).execute()

        return res.body!!.string()
    }

    /**
     * pixiv搜尋
     * @param keywords 搜尋關鍵字
     * @param page 搜尋結果之頁數，pixiv搜尋一頁最多60個作品
     */
    fun search(keywords: String, page: Int): String {
        return search(keywords, page, PiXivSearchArtistType.Illustrations, PiXivSearchOrder.NEW_TO_OLD, PiXivSearchMode.SAFE, PiXivSearchSMode.S_TAG, PiXivSearchType.ILLUST)
    }

    /**
     * pixiv搜尋的最大頁數
     * @param keywords 搜尋關鍵字
     * @param artistType 欲搜尋的作品類型
     * @param order 排序方式
     * @param mode 搜尋模式
     * @param sMode 關鍵字搜尋模式
     * @param type 搜尋類型
     */
    fun getSearchMaxPage(keywords: String, artistType: PiXivSearchArtistType, order: PiXivSearchOrder, mode: PiXivSearchMode, sMode: PiXivSearchSMode, type: PiXivSearchType): Int {
        val json = JSONObject(search(keywords, 1, artistType, order, mode, sMode, type))
        val dataType = when (artistType) {
            PiXivSearchArtistType.Illustrations -> "illust"
            PiXivSearchArtistType.Manga -> "manga"
            PiXivSearchArtistType.Novels -> "novel"
        }
        return ceil(json.getJSONObject("body").getJSONObject(dataType).getInt("total").toDouble() / 60).toInt()
    }
}