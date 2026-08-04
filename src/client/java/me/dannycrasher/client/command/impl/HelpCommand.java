package me.dannycrasher.client.command.impl;

import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandContext;
import me.dannycrasher.client.module.ModuleType;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Commands list", ModuleType.COMMAND, true, false);
    }

    @Override
    public void executeCommand(CommandContext context) {
        String prefix = context.getCommandManager().getPrefix();

        context.sendMessage("<aqua>Commands list: ");
        for (Command command : context.getCommandManager().getCommands()) {
            if (!command.isVisibleInHelp()) continue;
            context.sendMessage("<aqua>" + command.getUsage(prefix) + " <dark_gray>- <white>" + command.getDescription());
        }
    }
}
