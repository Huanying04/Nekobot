package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigLongData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.dcbot.enums.ConfigStringData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownCommand.class);

    @Override
    public void handle(CommandContext ctx) throws Exception {
        if (ctx.event.getAuthor().getIdLong() == Config.get(ConfigLongData.OWNER)) {
            LOGGER.info("關閉機器人指令觸發，開始關閉機器人");

            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR),16));
            eb.setDescription("已關閉");
            ctx.event.getChannel().sendMessage(eb.build()).queue();

            Thread.sleep(1000);

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
