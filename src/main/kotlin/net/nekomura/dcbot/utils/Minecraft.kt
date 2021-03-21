package net.nekomura.dcbot.utils

import org.json.JSONObject
import java.net.URL
import java.util.*

object Minecraft {

    fun getVersionManifest(): JSONObject{
        return JSONObject(URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").readText())
    }

    fun getLatestVersion(): String{
        return getVersionManifest().getJSONObject("latest").getString("release")
    }

    fun getLatestSnapshot(): String{
        return getVersionManifest().getJSONObject("latest").getString("snapshot")
    }

    fun getUUID(name: String): String{
        val json = JSONObject(URL("https://api.mojang.com/users/profiles/minecraft/$name").readText())
        return json.getString("id")
    }

    fun getUUID(name: String, unixTime: Long): String{
        val json = JSONObject(URL("https://api.mojang.com/users/profiles/minecraft/$name?at=$unixTime").readText())
        return json.getString("id")
    }

    fun getPlayerProfile(uuid: String): JSONObject{
        return JSONObject(URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid").readText())
    }

    fun getSkinURL(uuid: String): String{
        val profile = getPlayerProfile(uuid)
        val properties = JSONObject(String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value"))))
        return properties.getJSONObject("textures").getJSONObject("SKIN").getString("url")
    }

}