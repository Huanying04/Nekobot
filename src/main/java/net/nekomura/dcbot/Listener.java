package net.nekomura.dcbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nekomura.dcbot.Enums.ConfigStringData;
import net.nekomura.dcbot.commands.Managers.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends ListenerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
