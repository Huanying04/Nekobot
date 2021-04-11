package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SearchR18Command implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchR18Command.class);

    @Override
    public void handle(CommandContext ctx) throws Exception {
        LOGGER.debug("偵測到指令{}，開始執行指令", getName());

        if (!ctx.event.getChannel().isNSFW()) {
            LOGGER.debug("頻道不為NSFW，取消執行指令");

            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR), 16));
            eb.setDescription("此指令僅能在限制級頻道使用。");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
        }else {
            IllustrationUtils.searchByKeywordsRandomAndSend(PixivSearchMode.R18, ctx);
        }
    }

    @Override
    public String getName() {
        return "searchR18";
    }

    @Override
    public List<String> getAliases() {
        String[] aliases = {"r18Search"};
        return Arrays.asList(aliases);
    }
}
