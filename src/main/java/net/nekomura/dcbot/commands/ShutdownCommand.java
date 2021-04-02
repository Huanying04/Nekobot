package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigLongData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.dcbot.enums.ConfigStringData;

public class ShutdownCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        if (ctx.event.getAuthor().getIdLong() == Config.get(ConfigLongData.OWNER)) {
            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("已關閉");
            ctx.event.getChannel().sendMessage(eb.build()).queue();

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
