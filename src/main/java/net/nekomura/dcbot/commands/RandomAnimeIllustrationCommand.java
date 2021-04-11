package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class RandomAnimeIllustrationCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomAnimeIllustrationCommand.class);

    @Override
    public void handle(net.nekomura.dcbot.commands.managers.CommandContext ctx) throws Exception {
        LOGGER.debug("偵測到指令{}，開始執行指令", getName());

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
