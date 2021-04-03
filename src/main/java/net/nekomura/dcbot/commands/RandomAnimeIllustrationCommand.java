package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;
import java.util.Arrays;
import java.util.List;

public class RandomAnimeIllustrationCommand implements ICommand {

    @Override
    public void handle(net.nekomura.dcbot.commands.managers.CommandContext ctx) throws Exception {
        IllustrationUtils.getRandomAndSend(PixivSearchMode.SAFE, ctx);
    }

    @Override
    public String getName() {
        return "RandomIllustration";
    }

    @Override
    public List<String> getAliases() {
        String[] aliases = {"anime", "illustration", "randomAnime","ra", "ri", "a", "i"};
        return Arrays.asList(aliases);
    }
}
