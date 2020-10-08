package net.nekomura.dcbot

import net.nekomura.dcbot.Enums.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Config {

    fun getConfig(): JSONObject {
        val file = File("./config.json")
        val content = file.readText()
        return JSONObject(content)
    }

    fun get(key: ConfigBooleanData): Boolean{
        return getConfig().getBoolean(key.name)
    }

    fun get(key: ConfigStringData): String?{
        return getConfig().getString(key.name)
    }

    fun get(key: ConfigLongData): Long{
        return getConfig().getLong(key.name)
    }

    fun get(key: ConfigIntegerData): Int{
        return getConfig().getInt(key.name)
    }

    fun get(key: ConfigJsonObjectData): JSONObject{
        return getConfig().getJSONObject(key.name)
    }

    fun get(key: ConfigJsonArrayData): JSONArray {
        return getConfig().getJSONArray(key.name)
    }

    fun JSONObject.get(key: ConfigStringData): String?{
        return this.getString(key.name)
    }

    fun JSONObject.get(key: ConfigLongData): Long{
        return this.getLong(key.name)
    }

    fun JSONObject.get(key: ConfigIntegerData): Int{
        return this.getInt(key.name)
    }

    fun JSONObject.get(key: ConfigJsonObjectData): JSONObject{
        return this.getJSONObject(key.name)
    }

    fun JSONObject.get(key: ConfigJsonArrayData): JSONArray{
        return this.getJSONArray(key.name)
    }

    fun jsonArrayToArrayList(jsonArray: JSONArray): ArrayList<Any> {
        val arrayList = ArrayList<Any>()
        for (element in jsonArray) {
            arrayList.add(element)
        }
        return arrayList
    }

    fun writeConfig(allJson: String) {
        val file = File("./config.json")
        file.writeText(allJson)
    }

    @Deprecated("Deprecated")
    fun getString(key: ConfigData): String?{
        return getConfig().getString(key.name)
    }

    @Deprecated("Deprecated")
    fun getInt(key: ConfigData): Int{
        return getConfig().getInt(key.name)
    }

    @Deprecated("Deprecated")
    fun getLong(key: ConfigData): Long{
        return getConfig().getLong(key.name)
    }

    @Deprecated("Deprecated")
    fun getDouble(key: ConfigData): Double{
        return getConfig().getDouble(key.name)
    }

    @Deprecated("Deprecated")
    fun getArray(key: ConfigData): ArrayList<String> {
        val array = getConfig().getJSONArray(key.name)
        val arrayList = ArrayList<String>()

        for (i in 0 until array.length()) {
            arrayList.add(array.get(i).toString())
        }

        return arrayList
    }
}