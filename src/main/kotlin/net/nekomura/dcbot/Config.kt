package net.nekomura.dcbot

import net.nekomura.dcbot.Enums.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Config {

    @JvmStatic
    fun getConfig(): JSONObject {
        val file = File("./config.json")
        val content = file.readText()
        return JSONObject(content)
    }

    @JvmStatic
    fun get(key: ConfigBooleanData): Boolean{
        return getConfig().getBoolean(key.name)
    }

    @JvmStatic
    fun get(key: ConfigStringData): String?{
        return getConfig().getString(key.name)
    }

    @JvmStatic
    fun get(key: ConfigLongData): Long{
        return getConfig().getLong(key.name)
    }

    @JvmStatic
    fun get(key: ConfigIntegerData): Int{
        return getConfig().getInt(key.name)
    }

    @JvmStatic
    fun get(key: ConfigJsonObjectData): JSONObject{
        return getConfig().getJSONObject(key.name)
    }

    @JvmStatic
    fun get(key: ConfigJsonArrayData): JSONArray {
        return getConfig().getJSONArray(key.name)
    }

    @JvmStatic
    fun JSONObject.get(key: ConfigStringData): String?{
        return this.getString(key.name)
    }

    @JvmStatic
    fun JSONObject.get(key: ConfigLongData): Long{
        return this.getLong(key.name)
    }

    @JvmStatic
    fun JSONObject.get(key: ConfigIntegerData): Int{
        return this.getInt(key.name)
    }

    @JvmStatic
    fun JSONObject.get(key: ConfigJsonObjectData): JSONObject{
        return this.getJSONObject(key.name)
    }

    @JvmStatic
    fun JSONObject.get(key: ConfigJsonArrayData): JSONArray{
        return this.getJSONArray(key.name)
    }

    @JvmStatic
    fun jsonArrayToArrayList(jsonArray: JSONArray): ArrayList<Any> {
        val arrayList = ArrayList<Any>()
        for (element in jsonArray) {
            arrayList.add(element)
        }
        return arrayList
    }

    @JvmStatic
    fun writeConfig(allJson: String) {
        val file = File("./config.json")
        file.writeText(allJson)
    }
}