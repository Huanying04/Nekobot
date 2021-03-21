package net.nekomura.dcbot.commands

import net.nekomura.dcbot.Config
import net.nekomura.dcbot.enums.ConfigBooleanData
import net.nekomura.dcbot.enums.ConfigJsonArrayData
import net.nekomura.dcbot.enums.ConfigLongData
import net.nekomura.dcbot.commands.managers.CommandContext
import net.nekomura.dcbot.commands.managers.ICommand

class SettingCommand: ICommand {
    override fun handle(ctx: CommandContext?) {
        if (ctx!!.event.author.idLong == Config.get(ConfigLongData.OWNER)) {
            if (ctx.args.size >= 3) {
                when (ctx.args[0]) {
                    "pixiv" -> {
                        when (ctx.args[1]) {
                            "follow" -> {
                                if (ctx.args[2] == "add" && ctx.args.size >= 4) {
                                    val add = ctx.args[3].toInt()
                                    val config = Config.getConfig()
                                    val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                    if (array.indexOf(add) == -1) {  //不存在
                                        array.put(add)
                                        config.put(ConfigJsonArrayData.FOLLOW_PIXIV.toString(), array)
                                        Config.writeConfig(config.toString())
                                        ctx.channel.sendMessage("添加成功! `$add`").queue()
                                    }else {  //存在
                                        ctx.channel.sendMessage("`$add`已經存在").queue()
                                    }
                                }else if (ctx.args[2] == "list") {
                                    val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                    val follow = Config.jsonArrayToArrayList(array) as ArrayList<Int>
                                    val sb = StringBuffer("> **pixiv推播用戶清單**")
                                    for (i in follow) {
                                        sb.append("\n$i")
                                    }
                                    ctx.channel.sendMessage(sb.toString()).queue()
                                }else if (ctx.args[2] == "remove" && ctx.args.size >= 4) {
                                    val remove = ctx.args[3].toInt()
                                    val config = Config.getConfig()
                                    val array = Config.get(ConfigJsonArrayData.FOLLOW_PIXIV)
                                    if (array.indexOf(remove) != -1) {
                                        array.remove(array.indexOf(remove))
                                        config.put(ConfigJsonArrayData.FOLLOW_PIXIV.toString(), array)
                                        Config.writeConfig(config.toString())
                                        ctx.channel.sendMessage("成功移除! `$remove`").queue()
                                    }else {
                                        ctx.channel.sendMessage("`$remove`不存在").queue()
                                    }
                                }
                            }
                        }
                    }
                    "listener" -> {
                        when (ctx.args[1]) {
                            "pixiv" -> {
                                val switch = ctx.args[2].toBoolean()
                                val config = Config.getConfig()
                                config.put(ConfigBooleanData.PIXIV_UPDATE_CHECKER.toString(), switch)
                                Config.writeConfig(config.toString())
                                ctx.channel.sendMessage("已將`PIXIV_UPDATE_CHECKER`設為`$switch`").queue()
                            }
                            "minecraft" -> {
                                val switch = ctx.args[2].toBoolean()
                                val config = Config.getConfig()
                                config.put(ConfigBooleanData.MINECRAFT_UPDATE_CHECKER.toString(), switch)
                                Config.writeConfig(config.toString())
                                ctx.channel.sendMessage("已將`MINECRAFT_UPDATE_CHECKER`設為`$switch`").queue()
                            }
                        }
                    }
                }
            }
        }else {
            ctx.channel.sendMessage("你不是機器人OWNER，無法使用該指令！").queue()
        }
    }

    override fun getName(): String {
        return "setting"
    }
}