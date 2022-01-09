package net.nekomura.dcbot.commands.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * Original from me.duncte123.botCommons
 */
public interface ICommandContext {

    default Guild getGuild() {
        return this.getEvent().getGuild();
    }

    MessageReceivedEvent getEvent();

    default TextChannel getChannel() {
        return this.getEvent().getTextChannel();
    }

    default Message getMessage() {
        return this.getEvent().getMessage();
    }

    default User getAuthor() {
        return this.getEvent().getAuthor();
    }

    default Member getMember() {
        return this.getEvent().getMember();
    }

    default JDA getJDA() {
        return this.getEvent().getJDA();
    }

    default ShardManager getShardManager() {
        return this.getJDA().getShardManager();
    }

    default User getSelfUser() {
        return this.getJDA().getSelfUser();
    }

    default Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }

}
