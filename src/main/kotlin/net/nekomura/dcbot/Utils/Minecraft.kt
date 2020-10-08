package net.nekomura.dcbot.Utils

import org.json.JSONObject
import java.net.URL

object Minecraft {

    fun versionManifest(): JSONObject{
        return JSONObject(URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").readText())
    }

    fun getLatestVersion(): String{
        return versionManifest().getJSONObject("latest").getString("release")
    }

    fun getLatestSnapshot(): String{
        return versionManifest().getJSONObject("latest").getString("snapshot")
    }

}