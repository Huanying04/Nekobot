package net.nekomura.dcbot.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.commands.*;
import net.nekomura.dcbot.enums.ConfigStringData;
import net.nekomura.dcbot.commands.managers.CommandManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandsListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsListener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(ReadyEvent event) {
        manager.register(new RandomAnimeIllustrationCommand());
        manager.register(new PixivIllustrationCommand());
        manager.register(new SauceCommand());
        manager.register(new RandomAnimeIllustrationNSFWAbleCommand());
        manager.register(new RandomAnimeIllustrationR18Command());
        manager.register(new SearchCommand());
        manager.register(new SearchNSFWAbleCommand());
        manager.register(new SearchR18Command());
        manager.register(new ShutdownCommand());
        manager.register(new MeowCommand());

        LOGGER.info("{} 準備完畢。", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String prefix = Config.get(ConfigStringData.PREFIX);
        String raw = event.getMessage().getContentRaw();

        if (raw.startsWith(prefix)) {
            try {
                manager.handle(event);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
