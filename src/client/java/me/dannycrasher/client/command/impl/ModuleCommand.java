package me.dannycrasher.client.command.impl;

import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandContext;
import me.dannycrasher.client.module.ModuleType;

public class ModuleCommand extends Command {
	public ModuleCommand() {
		super("module", "Send module packets", ModuleType.COMMAND, false, true);
		addArgument("name", ArgumentType.STRING);
	}

	@Override
	public boolean acceptsAdditionalArguments() {
		return true;
	}

	@Override
	public void executeCommand(CommandContext context) {
		DannyCrasher.getModuleManager().executeModule(context.getString("name"), context.getAdditionalArguments());
	}
}
