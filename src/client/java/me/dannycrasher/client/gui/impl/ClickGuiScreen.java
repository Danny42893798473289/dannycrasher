package me.dannycrasher.client.gui.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.gui.GuiModuleEntry;
import me.dannycrasher.client.gui.menu.MenuTextField;
import me.dannycrasher.client.gui.panel.ModuleDetailPanel;
import me.dannycrasher.client.gui.panel.ModuleListPanel;
import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {
	private enum Category {
		HOME("Home", null),
		CRASHERS("Crashers", ModuleType.CRASHER),
		PAPER("Paper", ModuleType.PAPER),
		EXPLOITS("Exploits", ModuleType.EXPLOIT),
		COMMANDS("Commands", ModuleType.COMMAND),
		APPEARANCE("Appearance", null);

		private final String label;
		private final ModuleType moduleType;

		Category(String label, ModuleType moduleType) {
			this.label = label;
			this.moduleType = moduleType;
		}
	}

	private static final int SIDEBAR_WIDTH = 160;
	private static final int LIST_WIDTH = 300;
	private static final int GAP = 10;
	private static final int NAV_HEIGHT = 28;
	private static final int CHIP_HEIGHT = 20;
	private static final int CHIP_WIDTH = 78;

	private final List<GuiModuleEntry> allEntries = new ArrayList<>();
	private final Map<ModuleType, Integer> countsByType = new LinkedHashMap<>();
	private final ModuleListPanel listPanel = new ModuleListPanel();
	private final ModuleDetailPanel detailPanel = new ModuleDetailPanel();
	private final MenuTextField searchField = new MenuTextField("Search modules");

	private Category selectedCategory = Category.CRASHERS;
	private int panelX;
	private int panelY;
	private int panelW;
	private int panelH;

	public ClickGuiScreen() {
		super(Component.literal("Danny Crasher ClickGUI"));
		searchField.setGlassStyle(true);
		buildEntries();
	}

	@Override
	protected void init() {
		layoutPanel();
		applyCategory();
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		super.resize(minecraft, width, height);
		layoutPanel();
		applyCategory();
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBlurredBackground();
		ClickGuiTheme.renderOverlay(graphics, width, height);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		ClickGuiTheme.renderPanel(graphics, panelX, panelY, panelW, panelH);

		renderSidebar(graphics, mouseX, mouseY);

		if (selectedCategory == Category.HOME) {
			renderHome(graphics);
			return;
		}

		if (selectedCategory == Category.APPEARANCE) {
			renderAppearance(graphics, mouseX, mouseY);
			return;
		}

		renderCenterHeader(graphics, mouseX, mouseY);
		searchField.render(graphics, font, mouseX, mouseY);
		listPanel.render(graphics, font, mouseX, mouseY);
		detailPanel.render(graphics, font, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0) {
			return super.mouseClicked(mouseX, mouseY, button);
		}

		Category nav = getCategoryAt(mouseX, mouseY);
		if (nav != null) {
			selectedCategory = nav;
			applyCategory();
			return true;
		}

		if (selectedCategory == Category.APPEARANCE) {
			return handleAppearanceClick(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
		}

		if (selectedCategory == Category.HOME) {
			return super.mouseClicked(mouseX, mouseY, button);
		}

		if (searchField.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}

		int favoritesX = getFavoritesChipX();
		int stopX = getStopChipX();
		int chipY = getChipY();

		if (isInside(mouseX, mouseY, favoritesX, chipY, CHIP_WIDTH, CHIP_HEIGHT)) {
			listPanel.setFavoritesOnly(!listPanel.isFavoritesOnly());
			syncSelectionAfterFilter();
			return true;
		}

		if (isInside(mouseX, mouseY, stopX, chipY, CHIP_WIDTH, CHIP_HEIGHT)) {
			DannyCrasher.getModuleManager().handleDisconnect();
			return true;
		}

		ModuleListPanel.ClickResult listResult = listPanel.mouseClicked(mouseX, mouseY, button);
		if (listResult.selected()) {
			detailPanel.setEntry(listResult.entry(), font);
			return true;
		}

		if (listResult.favoriteToggled()) {
			syncSelectionAfterFilter();
			return true;
		}

		if (detailPanel.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (selectedCategory != Category.HOME && selectedCategory != Category.APPEARANCE && detailPanel.mouseDragged(mouseX, mouseY, button)) {
			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		detailPanel.mouseReleased();
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (selectedCategory != Category.HOME && selectedCategory != Category.APPEARANCE) {
			if (listPanel.mouseScrolled(mouseX, mouseY, scrollY)) {
				return true;
			}

			if (detailPanel.mouseScrolled(mouseX, mouseY, scrollY)) {
				return true;
			}
		}

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (selectedCategory != Category.HOME && selectedCategory != Category.APPEARANCE) {
			if (searchField.keyPressed(keyCode, scanCode, modifiers)) {
				listPanel.setSearchQuery(searchField.getValue());
				syncSelectionAfterFilter();
				return true;
			}

			if (detailPanel.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (selectedCategory != Category.HOME && selectedCategory != Category.APPEARANCE) {
			if (searchField.charTyped(codePoint)) {
				listPanel.setSearchQuery(searchField.getValue());
				syncSelectionAfterFilter();
				return true;
			}

			if (detailPanel.charTyped(codePoint, modifiers)) {
				return true;
			}
		}

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void buildEntries() {
		allEntries.clear();
		countsByType.clear();

		for (Command command : DannyCrasher.getCommandManager().getCommands()) {
			if (command.isVisibleInGui()) {
				GuiModuleEntry entry = GuiModuleEntry.command(command);
				allEntries.add(entry);
				countsByType.merge(command.getModuleType(), 1, Integer::sum);
			}
		}

		for (Module module : DannyCrasher.getModuleManager().getModules()) {
			if (module.isVisibleInGui()) {
				GuiModuleEntry entry = GuiModuleEntry.module(module);
				allEntries.add(entry);
				countsByType.merge(module.getModuleType(), 1, Integer::sum);
			}
		}

		listPanel.setEntries(allEntries);
	}

	private void layoutPanel() {
		panelW = Math.min(920, Math.max(720, (int) (width * 0.78)));
		panelH = Math.min(560, Math.max(420, (int) (height * 0.78)));
		panelX = (width - panelW) / 2;
		panelY = (height - panelH) / 2;

		int contentX = panelX + SIDEBAR_WIDTH + GAP;
		int contentY = panelY + 56;
		int contentH = panelH - 68;
		int detailX = contentX + LIST_WIDTH + GAP;
		int detailW = panelW - SIDEBAR_WIDTH - LIST_WIDTH - GAP * 3;

		searchField.setBounds(contentX, panelY + 48, LIST_WIDTH - CHIP_WIDTH * 2 - 16, 18);
		listPanel.setBounds(contentX, contentY + 28, LIST_WIDTH, contentH - 28);
		detailPanel.setBounds(detailX, panelY + 16, detailW, panelH - 32);
	}

	private void applyCategory() {
		layoutPanel();

		if (selectedCategory == Category.HOME || selectedCategory == Category.APPEARANCE) {
			detailPanel.setEntry(null, font);
			listPanel.setSelected(null);
			return;
		}

		listPanel.setFilterType(selectedCategory.moduleType);
		listPanel.setSearchQuery(searchField.getValue());
		syncSelectionAfterFilter();
	}

	private void syncSelectionAfterFilter() {
		GuiModuleEntry selected = listPanel.getSelected();
		if (selected == null) {
			detailPanel.setEntry(null, font);
			return;
		}

		detailPanel.setEntry(selected, font);
	}

	private void renderSidebar(GuiGraphics graphics, int mouseX, int mouseY) {
		int sidebarX = panelX + 10;
		int sidebarY = panelY + 10;

		ClickGuiTheme.renderCard(graphics, sidebarX, sidebarY, SIDEBAR_WIDTH - 10, 42, false, true);
		ClickGuiTheme.label(graphics, font, "Danny Crasher", sidebarX + 12, sidebarY + 12, ClickGuiTheme.TEXT);
		ClickGuiTheme.label(graphics, font, "v1.1.0", sidebarX + 12, sidebarY + 24, ClickGuiTheme.MUTED);

		ClickGuiTheme.label(graphics, font, "CATEGORIES", sidebarX + 12, sidebarY + 56, ClickGuiTheme.MUTED);

		int navY = sidebarY + 74;
		for (Category category : Category.values()) {
			boolean selected = category == selectedCategory;
			boolean hovered = isInside(mouseX, mouseY, sidebarX, navY, SIDEBAR_WIDTH - 10, NAV_HEIGHT);
			ClickGuiTheme.renderNavItem(graphics, font, category.label, sidebarX, navY, SIDEBAR_WIDTH - 10, NAV_HEIGHT, selected, hovered);
			navY += NAV_HEIGHT + 4;
		}

		int footerY = panelY + panelH - 58;
		ClickGuiTheme.renderCard(graphics, sidebarX, footerY, SIDEBAR_WIDTH - 10, 40, false, false);
		String username = minecraft != null && minecraft.getUser() != null ? minecraft.getUser().getName() : "Player";
		ClickGuiTheme.label(graphics, font, username, sidebarX + 12, footerY + 10, ClickGuiTheme.TEXT);
		ClickGuiTheme.label(graphics, font, "Session: " + formatSession(), sidebarX + 12, footerY + 22, ClickGuiTheme.MUTED);
	}

	private void renderCenterHeader(GuiGraphics graphics, int mouseX, int mouseY) {
		int contentX = panelX + SIDEBAR_WIDTH + GAP;
		ClickGuiTheme.label(graphics, font, selectedCategory.label, contentX, panelY + 18, ClickGuiTheme.TEXT);
		ClickGuiTheme.label(graphics, font, listPanel.getVisibleCount() + " available modules", contentX, panelY + 32, ClickGuiTheme.MUTED);

		int chipY = getChipY();
		ClickGuiTheme.renderChip(graphics, font, "Favorites", getFavoritesChipX(), chipY, CHIP_WIDTH, CHIP_HEIGHT, listPanel.isFavoritesOnly(), isInside(mouseX, mouseY, getFavoritesChipX(), chipY, CHIP_WIDTH, CHIP_HEIGHT));
		ClickGuiTheme.renderChip(graphics, font, "Stop", getStopChipX(), chipY, CHIP_WIDTH, CHIP_HEIGHT, false, isInside(mouseX, mouseY, getStopChipX(), chipY, CHIP_WIDTH, CHIP_HEIGHT));
	}

	private void renderHome(GuiGraphics graphics) {
		int contentX = panelX + SIDEBAR_WIDTH + GAP;
		int contentY = panelY + 20;
		int contentW = panelW - SIDEBAR_WIDTH - GAP * 2;

		ClickGuiTheme.renderCard(graphics, contentX, contentY, contentW, panelH - 40, false, false);
		ClickGuiTheme.label(graphics, font, "Danny Crasher", contentX + 20, contentY + 20, ClickGuiTheme.TEXT);
		ClickGuiTheme.label(graphics, font, "Fabric crash/exploit toolkit for authorized testing", contentX + 20, contentY + 36, ClickGuiTheme.MUTED);
		ClickGuiTheme.label(graphics, font, "Session " + formatSession(), contentX + 20, contentY + 60, ClickGuiTheme.SOFT);

		int rowY = contentY + 90;
		for (Category category : Category.values()) {
			if (category.moduleType == null) {
				continue;
			}

			int count = countsByType.getOrDefault(category.moduleType, 0);
			ClickGuiTheme.renderCard(graphics, contentX + 20, rowY, 180, 36, false, false);
			ClickGuiTheme.label(graphics, font, category.label, contentX + 34, rowY + 8, ClickGuiTheme.TEXT);
			ClickGuiTheme.label(graphics, font, count + " modules", contentX + 34, rowY + 20, ClickGuiTheme.MUTED);
			rowY += 44;
		}
	}

	private void renderAppearance(GuiGraphics graphics, int mouseX, int mouseY) {
		int contentX = panelX + SIDEBAR_WIDTH + GAP;
		int contentY = panelY + 20;
		int contentW = panelW - SIDEBAR_WIDTH - GAP * 2;

		ClickGuiTheme.renderCard(graphics, contentX, contentY, contentW, panelH - 40, false, false);
		ClickGuiTheme.label(graphics, font, "Appearance", contentX + 20, contentY + 20, ClickGuiTheme.TEXT);
		ClickGuiTheme.label(graphics, font, "Accent color", contentX + 20, contentY + 48, ClickGuiTheme.MUTED);

		int accentX = contentX + 20;
		int accentY = contentY + 68;
		for (ClickGuiTheme.Accent accent : ClickGuiTheme.Accent.values()) {
			boolean selected = ClickGuiTheme.getAccent() == accent;
			boolean hovered = isInside(mouseX, mouseY, accentX, accentY, 90, 28);
			ClickGuiTheme.renderChip(graphics, font, accent.name(), accentX, accentY, 90, 28, selected, hovered);
			accentX += 100;
		}

		ClickGuiTheme.label(graphics, font, "Panel opacity", contentX + 20, contentY + 120, ClickGuiTheme.MUTED);
		int opacity = DannyCrasher.getClickGuiManager().getPanelOpacity();
		ClickGuiTheme.label(graphics, font, Integer.toString(opacity), contentX + 140, contentY + 120, ClickGuiTheme.SOFT);

		int sliderX = contentX + 20;
		int sliderY = contentY + 144;
		int sliderW = 280;
		ClickGuiTheme.renderField(graphics, sliderX, sliderY + 4, sliderW, 12, false, false);
		int fill = (int) Math.round(((opacity - 100) / 130.0) * sliderW);
		graphics.fill(sliderX + 2, sliderY + 7, sliderX + Math.max(2, fill), sliderY + 13, ClickGuiTheme.accent());
		graphics.fill(sliderX + fill - 3, sliderY + 2, sliderX + fill + 3, sliderY + 18, ClickGuiTheme.TEXT);

		ClickGuiTheme.renderButton(graphics, font, "-10", contentX + 20, contentY + 180, 60, 22, isInside(mouseX, mouseY, contentX + 20, contentY + 180, 60, 22));
		ClickGuiTheme.renderButton(graphics, font, "+10", contentX + 90, contentY + 180, 60, 22, isInside(mouseX, mouseY, contentX + 90, contentY + 180, 60, 22));
	}

	private boolean handleAppearanceClick(double mouseX, double mouseY) {
		int contentX = panelX + SIDEBAR_WIDTH + GAP;
		int contentY = panelY + 20;

		int accentX = contentX + 20;
		int accentY = contentY + 68;
		for (ClickGuiTheme.Accent accent : ClickGuiTheme.Accent.values()) {
			if (isInside(mouseX, mouseY, accentX, accentY, 90, 28)) {
				DannyCrasher.getClickGuiManager().setAccent(accent);
				return true;
			}

			accentX += 100;
		}

		int sliderX = contentX + 20;
		int sliderY = contentY + 144;
		int sliderW = 280;
		if (isInside(mouseX, mouseY, sliderX, sliderY, sliderW, 20)) {
			double ratio = (mouseX - sliderX) / sliderW;
			int opacity = 100 + (int) Math.round(ratio * 130);
			DannyCrasher.getClickGuiManager().setPanelOpacity(opacity);
			return true;
		}

		if (isInside(mouseX, mouseY, contentX + 20, contentY + 180, 60, 22)) {
			DannyCrasher.getClickGuiManager().setPanelOpacity(DannyCrasher.getClickGuiManager().getPanelOpacity() - 10);
			return true;
		}

		if (isInside(mouseX, mouseY, contentX + 90, contentY + 180, 60, 22)) {
			DannyCrasher.getClickGuiManager().setPanelOpacity(DannyCrasher.getClickGuiManager().getPanelOpacity() + 10);
			return true;
		}

		return false;
	}

	private Category getCategoryAt(double mouseX, double mouseY) {
		int sidebarX = panelX + 10;
		int navY = panelY + 10 + 74;

		for (Category category : Category.values()) {
			if (isInside(mouseX, mouseY, sidebarX, navY, SIDEBAR_WIDTH - 10, NAV_HEIGHT)) {
				return category;
			}

			navY += NAV_HEIGHT + 4;
		}

		return null;
	}

	private int getFavoritesChipX() {
		return panelX + SIDEBAR_WIDTH + GAP + LIST_WIDTH - CHIP_WIDTH * 2 - 8;
	}

	private int getStopChipX() {
		return getFavoritesChipX() + CHIP_WIDTH + 8;
	}

	private int getChipY() {
		return panelY + 48;
	}

	private String formatSession() {
		long elapsed = Math.max(0L, System.currentTimeMillis() - DannyCrasher.getClickGuiManager().getSessionStartMs()) / 1000L;
		long hours = elapsed / 3600L;
		long minutes = (elapsed % 3600L) / 60L;
		long seconds = elapsed % 60L;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
