package net.nekomura.dcbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.illustrations.IllustrationUtils;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.commands.managers.CommandContext;
import net.nekomura.dcbot.commands.managers.ICommand;
import net.nekomura.utils.jixiv.enums.search.*;
import java.util.Arrays;
import java.util.List;

public class RandomAnimeIllustrationR18Command implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws Exception {
        if (!ctx.event.getChannel().isNSFW()) {
            EmbedBuilder eb = new EmbedBuilder().setColor(Integer.parseInt(Config.get(ConfigStringData.EMBED_MESSAGE_COLOR), 16));
            eb.setDescription("此指令僅能在限制級頻道使用。");
            ctx.event.getChannel().sendMessage(eb.build()).queue();
        }else {
            IllustrationUtils.getRandomAndSend(PixivSearchMode.R18, ctx);
        }
    }

    @Override
    public String getName() {
        return "RandomR18Illustration";
    }

    @Override
    public List<String> getAliases() {
        String[] aliases = {"RandomR18Anime", "RandomR18", "rr18a","rr18i", "r18i", "r18a", "r18"};
        return Arrays.asList(aliases);
    }
}
