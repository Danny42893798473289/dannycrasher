package me.dannycrasher.client.command.impl;

import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandContext;
import me.dannycrasher.client.module.ModuleType;

public class AuthorCommand extends Command {
    public AuthorCommand() {
        super("author", "Informations about author", ModuleType.COMMAND, true, true);
    }

    @Override
    public void executeCommand(CommandContext context) {
        context.sendMessage("<white>Author: <aqua>danny");
        context.sendMessage("<white>Project: <aqua>Danny Crasher");
        context.sendMessage(" ");
        context.sendMessage("<gray>Source:");
        context.sendMessage("<white>GitHub: <aqua>https://github.com/Danny42893798473289/dannycrasher");
    }
}
