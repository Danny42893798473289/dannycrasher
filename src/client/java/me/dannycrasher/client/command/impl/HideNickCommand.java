package me.dannycrasher.client.command.impl;

import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandContext;
import me.dannycrasher.client.module.ModuleType;

public class HideNickCommand extends Command {
    private static boolean enabled = false;

    public HideNickCommand() {
        super("hidenick", "Change your displayed name to Danny Crasher (client-side)", ModuleType.COMMAND);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    @Override
    public void executeCommand(CommandContext context) {
        enabled = !enabled;
        DannyCrasher.getConfigManager().saveCurrentServer();
        context.sendMessage("Hide nick: " + (enabled ? "<green>enabled" : "<red>disabled"));
    }
}
