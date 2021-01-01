package net.nekomura.dcbot.commands.Managers;

import java.util.Arrays;
import java.util.List;

public interface ICommand {
    void handle(net.nekomura.dcbot.commands.Managers.CommandContext ctx) throws Exception;

    String getName();

    default List<String> getAliases() {
        return Arrays.asList();
    }
}
