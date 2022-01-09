package net.nekomura.dcbot.commands.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandContext implements ICommandContext {
    public final MessageReceivedEvent event;
    public final List<String> args;

    public CommandContext(MessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public List<String> getArgs() {
        return args;
    }
}
