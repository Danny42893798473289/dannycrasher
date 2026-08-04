package me.dannycrasher.client.module;

import java.util.List;
import java.util.Map;
import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.chat.ChatHelper;
import me.dannycrasher.client.notify.NotificationType;

public record ModuleContext(ModuleManager moduleManager, Module module, Map<String, Object> arguments,
							List<String> rawArguments) {

	public int getInt(String name) {
		return getArgument(name, Integer.class);
	}

	public String getString(String name) {
		return getArgument(name, String.class);
	}

	public String getList(String name) {
		return getArgument(name, String.class);
	}

	public boolean getBoolean(String name) {
		return getArgument(name, Boolean.class);
	}

	public double getDouble(String name) {
		return getArgument(name, Double.class);
	}

	public void sendMessage(String message) {
		ChatHelper.send(message);
	}

	public void notifySuccess(String message) {
		DannyCrasher.getNotificationManager().show(NotificationType.SUCCESS, module.getName(), message);
	}

	public void notifyInfo(String message) {
		DannyCrasher.getNotificationManager().show(NotificationType.INFO, module.getName(), message);
	}

	public void notifyWarning(String message) {
		DannyCrasher.getNotificationManager().show(NotificationType.WARNING, module.getName(), message);
	}

	public void notifyError(String message) {
		DannyCrasher.getNotificationManager().show(NotificationType.ERROR, module.getName(), message);
	}

	private <T> T getArgument(String name, Class<T> type) {
		Object argument = arguments.get(name);

		if (argument == null) {
			throw new IllegalArgumentException("Argument '" + name + "' does not exist");
		}

		return type.cast(argument);
	}
}
