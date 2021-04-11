package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCommand.class);

    @Override
    public void handle(CommandContext ctx) throws Exception {
        LOGGER.debug("偵測到指令{}，開始執行指令", getName());

        IllustrationUtils.searchByKeywordsRandomAndSend(PixivSearchMode.SAFE, ctx);
    }

    @Override
    public String getName() {
        return "search";
    }
}
