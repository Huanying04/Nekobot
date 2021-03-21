package net.nekomura.dcbot.commands.managers;

import java.util.Arrays;
import java.util.List;

public interface ICommand {
    void handle(net.nekomura.dcbot.commands.managers.CommandContext ctx) throws Exception;

    String getName();

    default List<String> getAliases() {
        return Arrays.asList();
    }
}
