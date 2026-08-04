package me.dannycrasher.client.gui;

import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleType;

public record GuiModuleEntry(String name, ModuleType moduleType, Command command, Module module) {
	public static GuiModuleEntry command(Command command) {
		return new GuiModuleEntry(command.getName(), command.getModuleType(), command, null);
	}

	public static GuiModuleEntry module(Module module) {
		return new GuiModuleEntry(module.getName(), module.getModuleType(), null, module);
	}

	public boolean isCommand() {
		return command != null;
	}

	public boolean isModule() {
		return module != null;
	}
}
