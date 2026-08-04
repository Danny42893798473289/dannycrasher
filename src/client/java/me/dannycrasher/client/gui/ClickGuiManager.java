package me.dannycrasher.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.gui.impl.ClickGuiScreen;
import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ClickGuiManager {
	private static final int GLFW_KEY_RIGHT_SHIFT = 344;
	private static final String FAVORITES_KEY = "favorites";
	private static final String ACCENT_KEY = "appearance:accent";
	private static final String OPACITY_KEY = "appearance:opacity";

	private final Map<String, String> configs = new HashMap<>();
	private final Set<String> favorites = new LinkedHashSet<>();
	private KeyMapping openKey;
	private long sessionStartMs = System.currentTimeMillis();

	public void initialize() {
		sessionStartMs = System.currentTimeMillis();
		openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.dannycrasher.click_gui",
				InputConstants.Type.KEYSYM,
				GLFW_KEY_RIGHT_SHIFT,
				"category.dannycrasher"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.consumeClick()) {
				toggle(client);
			}
		});

		applyAppearanceFromConfig();
	}

	private void toggle(Minecraft client) {
		if (client.screen instanceof ClickGuiScreen) {
			client.setScreen(null);
			return;
		}

		client.setScreen(new ClickGuiScreen());
	}

	public long getSessionStartMs() {
		return sessionStartMs;
	}

	public String getConfig(String key) {
		return configs.getOrDefault(key, "");
	}

	public void saveConfig(String key, String value) {
		configs.put(key, value);
		DannyCrasher.getConfigManager().saveCurrentServer();
	}

	public void clearConfig(String key) {
		configs.remove(key);
		DannyCrasher.getConfigManager().saveCurrentServer();
	}

	public Map<String, String> getConfigs() {
		return new HashMap<>(configs);
	}

	public void setConfigs(Map<String, String> configs) {
		this.configs.clear();
		favorites.clear();

		if (configs != null) {
			this.configs.putAll(configs);
		}

		loadFavoritesFromConfig();
		applyAppearanceFromConfig();
	}

	public Set<String> getFavorites() {
		return Collections.unmodifiableSet(favorites);
	}

	public boolean isFavorite(String key) {
		return favorites.contains(key);
	}

	public void toggleFavorite(String key) {
		if (key == null || key.isBlank()) {
			return;
		}

		if (!favorites.add(key)) {
			favorites.remove(key);
		}

		persistFavorites();
	}

	public ClickGuiTheme.Accent getAccent() {
		return ClickGuiTheme.Accent.fromId(getConfig(ACCENT_KEY));
	}

	public void setAccent(ClickGuiTheme.Accent accent) {
		ClickGuiTheme.Accent value = accent == null ? ClickGuiTheme.Accent.PURPLE : accent;
		ClickGuiTheme.setAccent(value);
		saveConfig(ACCENT_KEY, value.name().toLowerCase(Locale.ROOT));
	}

	public int getPanelOpacity() {
		String raw = getConfig(OPACITY_KEY);
		if (raw.isBlank()) {
			return ClickGuiTheme.getPanelOpacity();
		}

		try {
			return Math.max(100, Math.min(230, Integer.parseInt(raw.trim())));
		} catch (NumberFormatException ignored) {
			return ClickGuiTheme.getPanelOpacity();
		}
	}

	public void setPanelOpacity(int opacity) {
		int clamped = Math.max(100, Math.min(230, opacity));
		ClickGuiTheme.setPanelOpacity(clamped);
		saveConfig(OPACITY_KEY, Integer.toString(clamped));
	}

	private void loadFavoritesFromConfig() {
		favorites.clear();
		String raw = configs.getOrDefault(FAVORITES_KEY, "");
		if (raw.isBlank()) {
			return;
		}

		for (String entry : raw.split(";", -1)) {
			String key = entry.trim();
			if (!key.isEmpty()) {
				favorites.add(key);
			}
		}
	}

	private void persistFavorites() {
		saveConfig(FAVORITES_KEY, String.join(";", favorites));
	}

	private void applyAppearanceFromConfig() {
		ClickGuiTheme.setAccent(getAccent());
		ClickGuiTheme.setPanelOpacity(getPanelOpacity());
	}
}
