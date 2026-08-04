package me.dannycrasher.client.command.impl;

import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandContext;
import me.dannycrasher.client.module.ModuleType;

public class PrefixCommand extends Command {
	public PrefixCommand() {
		super("prefix", "Changes the client command prefix", ModuleType.COMMAND);
		addArgument("prefix", ArgumentType.STRING);
	}

	@Override
	public void executeCommand(CommandContext context) {
		String prefix = context.getString("prefix");

		context.getCommandManager().setPrefix(prefix);
		context.sendMessage("Client prefix changed to <aqua>" + prefix + "</aqua>");
	}
}
