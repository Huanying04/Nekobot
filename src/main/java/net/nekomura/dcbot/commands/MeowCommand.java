package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class MeowCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeowCommand.class);

    @Override
    public void handle(CommandContext ctx) throws Exception {
        LOGGER.debug("偵測到指令{}，開始執行指令", getName());

        StringBuffer sb = new StringBuffer();
        String[] meow = {"喵", " ", ""};
        String[] end = {"！", "？", "", "喵"};

        int max = ThreadLocalRandom.current().nextInt(2, 13);

        LOGGER.debug("隨機產生隨機增加喵喵字符次數為{}的喵喵字串中", max);
        for (int i = 0; i <= max; i++) {
            if (i != max) {
                sb.append(meow[ThreadLocalRandom.current().nextInt(meow.length)]);
            }else if (i == max && sb.toString().isEmpty()) {
                sb.append(new String[]{"！", "？", "喵"}[ThreadLocalRandom.current().nextInt(3)]);
            }else {
                sb.append(end[ThreadLocalRandom.current().nextInt(end.length)]);
            }
        }

        LOGGER.debug("產生完畢，訊息發送中...");

        ctx.getChannel().sendMessage(sb.toString()).queue();

        LOGGER.debug("訊息發送完畢");
    }

    @Override
    public String getName() {
        return "meow";
    }
}
