package me.dannycrasher.client.gui.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.gui.GuiModuleEntry;
import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import me.dannycrasher.client.module.ModuleType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ModuleListPanel {
	private static final int CARD_HEIGHT = 36;
	private static final int CARD_GAP = 6;
	private static final int STAR_SIZE = 14;

	private final List<GuiModuleEntry> allEntries = new ArrayList<>();
	private final List<GuiModuleEntry> visibleEntries = new ArrayList<>();

	private int x;
	private int y;
	private int width;
	private int height;
	private int scroll;
	private String searchQuery = "";
	private boolean favoritesOnly;
	private ModuleType filterType;
	private GuiModuleEntry selected;

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		clampScroll();
	}

	public void setEntries(List<GuiModuleEntry> entries) {
		allEntries.clear();
		allEntries.addAll(entries);
		refreshFilter();
	}

	public void setFilterType(ModuleType filterType) {
		this.filterType = filterType;
		refreshFilter();
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery == null ? "" : searchQuery.trim().toLowerCase(Locale.ROOT);
		refreshFilter();
	}

	public void setFavoritesOnly(boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
		refreshFilter();
	}

	public boolean isFavoritesOnly() {
		return favoritesOnly;
	}

	public void setSelected(GuiModuleEntry selected) {
		this.selected = selected;
	}

	public GuiModuleEntry getSelected() {
		return selected;
	}

	public int getVisibleCount() {
		return visibleEntries.size();
	}

	public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
		ClickGuiTheme.renderCard(graphics, x, y, width, height, false, false);

		int listTop = y + 10;
		int listBottom = y + height - 10;
		graphics.enableScissor(x + 4, listTop, x + width - 4, listBottom);

		if (visibleEntries.isEmpty()) {
			ClickGuiTheme.label(graphics, font, "No modules found", x + 14, listTop + 10, ClickGuiTheme.MUTED);
			graphics.disableScissor();
			return;
		}

		int cardY = listTop - scroll;
		for (GuiModuleEntry entry : visibleEntries) {
			if (cardY + CARD_HEIGHT >= listTop && cardY <= listBottom) {
				boolean hovered = isInside(mouseX, mouseY, x + 8, cardY, width - 16, CARD_HEIGHT);
				boolean selectedCard = selected != null && selected.name().equals(entry.name()) && selected.moduleType().equals(entry.moduleType());
				ClickGuiTheme.renderCard(graphics, x + 8, cardY, width - 16, CARD_HEIGHT, hovered, selectedCard);

				String favoriteKey = favoriteKey(entry);
				boolean favorite = DannyCrasher.getClickGuiManager().isFavorite(favoriteKey);
				renderStar(graphics, font, x + 16, cardY + 11, favorite || isInside(mouseX, mouseY, x + 16, cardY + 11, STAR_SIZE, STAR_SIZE));

				ClickGuiTheme.label(graphics, font, entry.name(), x + 38, cardY + 8, ClickGuiTheme.TEXT);
				ClickGuiTheme.label(graphics, font, getSubtitle(entry), x + 38, cardY + 20, ClickGuiTheme.MUTED);
			}

			cardY += CARD_HEIGHT + CARD_GAP;
		}

		graphics.disableScissor();
	}

	public ClickResult mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0 || !isInside(mouseX, mouseY, x, y, width, height)) {
			return ClickResult.none();
		}

		int listTop = y + 10;
		int cardY = listTop - scroll;

		for (GuiModuleEntry entry : visibleEntries) {
			int cardX = x + 8;
			int cardW = width - 16;

			if (isInside(mouseX, mouseY, cardX, cardY, cardW, CARD_HEIGHT)) {
				if (isInside(mouseX, mouseY, x + 16, cardY + 11, STAR_SIZE, STAR_SIZE)) {
					DannyCrasher.getClickGuiManager().toggleFavorite(favoriteKey(entry));
					refreshFilter();
					return ClickResult.favoriteToggled(entry);
				}

				selected = entry;
				return ClickResult.selected(entry);
			}

			cardY += CARD_HEIGHT + CARD_GAP;
		}

		return ClickResult.none();
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!isInside(mouseX, mouseY, x, y, width, height)) {
			return false;
		}

		scroll = Math.max(0, scroll - (int) Math.round(amount * 18));
		clampScroll();
		return true;
	}

	private void refreshFilter() {
		visibleEntries.clear();

		for (GuiModuleEntry entry : allEntries) {
			if (filterType != null && !filterType.equals(entry.moduleType())) {
				continue;
			}

			if (favoritesOnly && !DannyCrasher.getClickGuiManager().isFavorite(favoriteKey(entry))) {
				continue;
			}

			if (!searchQuery.isEmpty()) {
				String haystack = (entry.name() + " " + getSubtitle(entry)).toLowerCase(Locale.ROOT);
				if (!haystack.contains(searchQuery)) {
					continue;
				}
			}

			visibleEntries.add(entry);
		}

		if (selected != null && visibleEntries.stream().noneMatch(entry -> entry.name().equals(selected.name()) && entry.moduleType().equals(selected.moduleType()))) {
			selected = null;
		}

		clampScroll();
	}

	private void clampScroll() {
		int total = visibleEntries.isEmpty() ? 0 : visibleEntries.size() * (CARD_HEIGHT + CARD_GAP) - CARD_GAP;
		int visible = Math.max(0, height - 16);
		int maxScroll = Math.max(0, total - visible);
		scroll = Math.max(0, Math.min(scroll, maxScroll));
	}

	private String getSubtitle(GuiModuleEntry entry) {
		if (entry.isModule()) {
			String description = entry.module().getDescription();
			return description == null || description.isBlank() ? "Module" : truncate(description, 34);
		}

		if (entry.isCommand()) {
			String description = entry.command().getDescription();
			return description == null || description.isBlank() ? "Command" : truncate(description, 34);
		}

		return "Hidden module details";
	}

	private String truncate(String value, int max) {
		return value.length() <= max ? value : value.substring(0, max - 1) + "…";
	}

	public static String favoriteKey(GuiModuleEntry entry) {
		if (entry.isModule()) {
			return "module:" + entry.module().getName();
		}

		if (entry.isCommand()) {
			return "command:" + entry.command().getName();
		}

		return entry.name();
	}

	private void renderStar(GuiGraphics graphics, Font font, int x, int y, boolean active) {
		String star = active ? "*" : "o";
		graphics.drawString(font, star, x + 3, y + 3, active ? ClickGuiTheme.accent() : ClickGuiTheme.MUTED, false);
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public record ClickResult(GuiModuleEntry entry, boolean favoriteToggled, boolean selected) {
		public static ClickResult none() {
			return new ClickResult(null, false, false);
		}

		public static ClickResult selected(GuiModuleEntry entry) {
			return new ClickResult(entry, false, true);
		}

		public static ClickResult favoriteToggled(GuiModuleEntry entry) {
			return new ClickResult(entry, true, false);
		}
	}
}
