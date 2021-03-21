package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;

import java.util.concurrent.ThreadLocalRandom;

public class MeowCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        StringBuffer sb = new StringBuffer();
        String[] meow = {"喵", " ", ""};
        String[] end = {"！", "？", "", "喵"};

        int max = ThreadLocalRandom.current().nextInt(2, 13);
        for (int i = 0; i <= max; i++) {
            if (i != max) {
                sb.append(meow[ThreadLocalRandom.current().nextInt(meow.length)]);
            }else if (i == max && sb.toString().isEmpty()) {
                sb.append(new String[]{"！", "？", "喵"}[ThreadLocalRandom.current().nextInt(3)]);
            }else {
                sb.append(end[ThreadLocalRandom.current().nextInt(end.length)]);
            }
        }

        ctx.getChannel().sendMessage(sb.toString()).queue();
    }

    @Override
    public String getName() {
        return "meow";
    }
}
