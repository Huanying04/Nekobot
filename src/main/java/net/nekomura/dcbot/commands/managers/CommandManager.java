package net.nekomura.dcbot.commands.managers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.nekomura.dcbot.Config;
import net.nekomura.dcbot.enums.ConfigStringData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public void register(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("註冊了重複的指令");
        }

        commands.add(cmd);
    }

    @Nullable
    private ICommand getCommand(String keyword) {
        String lower = keyword.toLowerCase();

        for (ICommand cmd: this.commands) {
            if (cmd.getName().equalsIgnoreCase(lower) || cmd.getAliases().stream().anyMatch(it -> it.equalsIgnoreCase(lower))) {
                return cmd;
            }
        }

        return null;
    }

    public void handle(MessageReceivedEvent event) throws Exception {
        try {
            String[] split = event.getMessage().getContentRaw()
                    .replaceFirst("(?i)" + Pattern.quote(Objects.requireNonNull(Config.get(ConfigStringData.PREFIX))), "")  //大小寫不敏感?
                    .split("\\s+");
            String invoke = split[0].toLowerCase();
            ICommand cmd = this.getCommand(invoke);

            if (cmd != null) {
                event.getChannel().sendTyping().queue();
                List<String> args = Arrays.asList(split).subList(1, split.length);

                CommandContext ctx = new CommandContext(event, args);

                cmd.handle(ctx);
            }
        }catch (Throwable e) {
            event.getChannel().sendMessage("錯誤發生！\r\n```" + e.toString() + "```\r\n請聯繫管理員或稍後重試。").queue();
            e.printStackTrace();
        }
    }
}
