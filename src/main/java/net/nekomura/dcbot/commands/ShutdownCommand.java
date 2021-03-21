package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigLongData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;

public class ShutdownCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        if (ctx.event.getAuthor().getIdLong() == Config.get(ConfigLongData.OWNER)) {
            ctx.event.getJDA().shutdown();
            System.exit(0);
        }else {
            ctx.getChannel().sendMessage("你不是機器人OWNER，無法使用該指令！").queue();
        }
    }

    @Override
    public String getName() {
        return "shutdown";
    }
}
