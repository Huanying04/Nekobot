package net.nekomura.dcbot.commands;

import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;

public class SearchCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        IllustrationUtils.searchByKeywordsRandomAndSend(PixivSearchMode.SAFE, ctx);
    }

    @Override
    public String getName() {
        return "search";
    }
}
