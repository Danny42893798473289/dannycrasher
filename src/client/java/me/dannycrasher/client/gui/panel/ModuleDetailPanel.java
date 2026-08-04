package me.dannycrasher.client.gui.panel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.command.Command;
import me.dannycrasher.client.command.CommandArgument;
import me.dannycrasher.client.gui.GuiModuleEntry;
import me.dannycrasher.client.gui.GuiModuleExecutor;
import me.dannycrasher.client.gui.component.ConfigCheckbox;
import me.dannycrasher.client.gui.component.ConfigComponent;
import me.dannycrasher.client.gui.component.ConfigListSelect;
import me.dannycrasher.client.gui.component.ConfigSliderField;
import me.dannycrasher.client.gui.component.ConfigTextBox;
import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import me.dannycrasher.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ModuleDetailPanel {
	private static final int FIELD_HEIGHT = 18;
	private static final int FIELD_GAP = 26;
	private static final int BUTTON_HEIGHT = 22;
	private static final int BUTTON_GAP = 8;
	private static final int CONTENT_PADDING = 14;
	private static final int INFO_LINE_HEIGHT = 11;

	private GuiModuleEntry entry;
	private String type = "";
	private String name = "";
	private String description = "";
	private String configKey = "";
	private List<CommandArgument> arguments = List.of();
	private final List<ArgumentField> argumentFields = new ArrayList<>();

	private int x;
	private int y;
	private int width;
	private int height;
	private int scroll;
	private Font font;

	public void setBounds(int x, int y, int width, int height) {
		boolean changed = this.x != x || this.y != y || this.width != width || this.height != height;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		if (changed && entry != null && font != null) {
			rebuildFields();
		}
	}

	public void setEntry(GuiModuleEntry entry, Font font) {
		this.font = font;
		this.entry = entry;
		this.scroll = 0;

		if (entry == null) {
			clear();
			return;
		}

		if (entry.isCommand()) {
			Command command = entry.command();
			this.type = "Command";
			this.name = command.getName();
			this.description = command.getDescription();
			this.arguments = command.getArguments();
			this.configKey = GuiModuleExecutor.getCommandConfigKey(command);
		} else if (entry.isModule()) {
			Module module = entry.module();
			this.type = module.getModuleType().getDisplayName();
			this.name = module.getName();
			this.description = module.getDescription();
			this.arguments = module.getArguments();
			this.configKey = GuiModuleExecutor.getModuleConfigKey(module);
		} else {
			clear();
			return;
		}

		rebuildFields();
	}

	public GuiModuleEntry getEntry() {
		return entry;
	}

	public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY, float partialTick) {
		this.font = font;
		ClickGuiTheme.renderCard(graphics, x, y, width, height, false, false);

		if (entry == null) {
			ClickGuiTheme.label(graphics, font, "Select a module", x + CONTENT_PADDING, y + 18, ClickGuiTheme.MUTED);
			ClickGuiTheme.label(graphics, font, "Configure arguments and execute here.", x + CONTENT_PADDING, y + 34, ClickGuiTheme.MUTED);
			return;
		}

		ClickGuiTheme.label(graphics, font, type, x + CONTENT_PADDING, y + 12, ClickGuiTheme.MUTED);
		ClickGuiTheme.label(graphics, font, name, x + CONTENT_PADDING, y + 26, ClickGuiTheme.TEXT);

		List<String> infoLines = getInfoLines(font);
		int infoTop = y + 46;
		for (int index = 0; index < infoLines.size(); index++) {
			ClickGuiTheme.label(graphics, font, infoLines.get(index), x + CONTENT_PADDING, infoTop + index * INFO_LINE_HEIGHT, ClickGuiTheme.SOFT);
		}

		int contentTop = getContentTop(font);
		int contentBottom = getButtonY() - 10;
		int contentHeight = Math.max(0, contentBottom - contentTop);

		graphics.enableScissor(x + 6, contentTop, x + width - 6, contentBottom);

		for (ArgumentField field : argumentFields) {
			if (!isOpenListSelect(field.input())) {
				int labelY = field.baseY() - scroll + 5;
				if (labelY + FIELD_HEIGHT >= contentTop && labelY <= contentBottom) {
					ClickGuiTheme.label(graphics, font, field.argument().name() + ":", x + CONTENT_PADDING, labelY, ClickGuiTheme.SOFT);
					field.input().render(graphics, font, mouseX, mouseY, partialTick);
				}
			}
		}

		if (argumentFields.isEmpty()) {
			ClickGuiTheme.label(graphics, font, "No configurable arguments", x + CONTENT_PADDING, contentTop + 4, ClickGuiTheme.MUTED);
		}

		graphics.disableScissor();

		int buttonY = getButtonY();
		int buttonWidth = (width - CONTENT_PADDING * 2 - BUTTON_GAP * 2) / 3;
		ClickGuiTheme.renderButton(graphics, font, "Save", x + CONTENT_PADDING, buttonY, buttonWidth, BUTTON_HEIGHT, isInside(mouseX, mouseY, x + CONTENT_PADDING, buttonY, buttonWidth, BUTTON_HEIGHT));
		ClickGuiTheme.renderButton(graphics, font, "Clear", x + CONTENT_PADDING + buttonWidth + BUTTON_GAP, buttonY, buttonWidth, BUTTON_HEIGHT, isInside(mouseX, mouseY, x + CONTENT_PADDING + buttonWidth + BUTTON_GAP, buttonY, buttonWidth, BUTTON_HEIGHT));
		ClickGuiTheme.renderAccentButton(graphics, font, "Execute", x + CONTENT_PADDING + (buttonWidth + BUTTON_GAP) * 2, buttonY, buttonWidth, BUTTON_HEIGHT, isInside(mouseX, mouseY, x + CONTENT_PADDING + (buttonWidth + BUTTON_GAP) * 2, buttonY, buttonWidth, BUTTON_HEIGHT));

		graphics.enableScissor(x + 6, contentTop, x + width - 6, contentBottom);
		for (ArgumentField field : argumentFields) {
			if (isOpenListSelect(field.input())) {
				graphics.pose().pushPose();
				graphics.pose().translate(0.0F, 0.0F, 300.0F);
				field.input().render(graphics, font, mouseX, mouseY, partialTick);
				graphics.pose().popPose();
			}
		}
		graphics.disableScissor();
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (entry == null || button != 0 || !isInside(mouseX, mouseY, x, y, width, height)) {
			return false;
		}

		int buttonY = getButtonY();
		int buttonWidth = (width - CONTENT_PADDING * 2 - BUTTON_GAP * 2) / 3;

		if (isInside(mouseX, mouseY, x + CONTENT_PADDING, buttonY, buttonWidth, BUTTON_HEIGHT)) {
			saveConfig();
			return true;
		}

		if (isInside(mouseX, mouseY, x + CONTENT_PADDING + buttonWidth + BUTTON_GAP, buttonY, buttonWidth, BUTTON_HEIGHT)) {
			clearConfig();
			return true;
		}

		if (isInside(mouseX, mouseY, x + CONTENT_PADDING + (buttonWidth + BUTTON_GAP) * 2, buttonY, buttonWidth, BUTTON_HEIGHT)) {
			execute();
			return true;
		}

		int contentTop = getContentTop(font);
		int contentBottom = getButtonY() - 10;
		if (mouseY < contentTop || mouseY > contentBottom) {
			clearFocusExcept(null);
			return true;
		}

		for (ArgumentField field : argumentFields) {
			if (field.input().mouseClicked(mouseX, mouseY, button)) {
				clearFocusExcept(field.input());
				return true;
			}
		}

		clearFocusExcept(null);
		return true;
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		if (entry == null || button != 0) {
			return false;
		}

		for (ArgumentField field : argumentFields) {
			if (field.input().mouseDragged(mouseX, mouseY, button)) {
				return true;
			}
		}

		return false;
	}

	public void mouseReleased() {
		for (ArgumentField field : argumentFields) {
			field.input().mouseReleased();
		}
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (entry == null || !isInside(mouseX, mouseY, x, y, width, height)) {
			return false;
		}

		scroll = Math.max(0, scroll - (int) Math.round(amount * 14));
		clampScroll(font);
		rebuildFieldPositions();
		return true;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (ArgumentField field : argumentFields) {
			if (field.input().keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}
		}

		return false;
	}

	public boolean charTyped(char codePoint, int modifiers) {
		for (ArgumentField field : argumentFields) {
			if (field.input().charTyped(codePoint, modifiers)) {
				return true;
			}
		}

		return false;
	}

	private void clear() {
		type = "";
		name = "";
		description = "";
		configKey = "";
		arguments = List.of();
		argumentFields.clear();
		scroll = 0;
	}

	private void rebuildFields() {
		if (font == null || width <= 0) {
			return;
		}

		Map<String, String> savedValues = deserializeConfig(DannyCrasher.getClickGuiManager().getConfig(configKey));
		Map<String, String> currentValues = new LinkedHashMap<>();
		for (ArgumentField field : argumentFields) {
			currentValues.put(field.argument().name(), field.input().getValue());
		}

		argumentFields.clear();
		int fieldX = x + 94;
		int fieldWidth = Math.max(60, width - 108);
		int startY = getContentTop(font);

		for (int index = 0; index < arguments.size(); index++) {
			CommandArgument argument = arguments.get(index);
			int fieldY = startY + index * FIELD_GAP - scroll;
			ConfigComponent input = createInput(argument, fieldX, fieldY, fieldWidth);
			String value = currentValues.getOrDefault(argument.name(), savedValues.getOrDefault(argument.name(), argument.defaultValue()));
			input.setValue(value);
			argumentFields.add(new ArgumentField(argument, input, startY + index * FIELD_GAP));
		}

		if (!argumentFields.isEmpty()) {
			argumentFields.getFirst().input().setFocused(true);
		}
	}

	private void rebuildFieldPositions() {
		if (font == null) {
			return;
		}

		Map<String, String> values = new LinkedHashMap<>();
		for (ArgumentField field : argumentFields) {
			values.put(field.argument().name(), field.input().getValue());
		}

		argumentFields.clear();
		int fieldX = x + 94;
		int fieldWidth = Math.max(60, width - 108);
		int startY = getContentTop(font);

		for (int index = 0; index < arguments.size(); index++) {
			CommandArgument argument = arguments.get(index);
			int fieldY = startY + index * FIELD_GAP - scroll;
			ConfigComponent input = createInput(argument, fieldX, fieldY, fieldWidth);
			input.setValue(values.getOrDefault(argument.name(), argument.defaultValue()));
			argumentFields.add(new ArgumentField(argument, input, startY + index * FIELD_GAP));
		}
	}

	private void saveConfig() {
		DannyCrasher.getClickGuiManager().saveConfig(configKey, serializeConfig());
	}

	private void clearConfig() {
		for (ArgumentField field : argumentFields) {
			field.input().setValue("");
		}

		DannyCrasher.getClickGuiManager().clearConfig(configKey);
	}

	private void execute() {
		saveConfig();
		String config = buildArgumentLine();

		if ("Command".equals(type)) {
			String commandLine = DannyCrasher.getCommandManager().getPrefix() + name + (config.isBlank() ? "" : " " + config);
			DannyCrasher.getCommandManager().handleChatMessage(commandLine);
			return;
		}

		DannyCrasher.getModuleManager().executeModule(name, splitArguments(config));
	}

	private String buildArgumentLine() {
		List<String> values = new ArrayList<>();

		for (ArgumentField field : argumentFields) {
			values.add(formatArgumentValue(field.input().getValue()));
		}

		return String.join(" ", values).trim();
	}

	private String formatArgumentValue(String value) {
		String trimmed = value.trim();

		if (trimmed.contains(" ")) {
			return "\"" + trimmed.replace("\"", "") + "\"";
		}

		return trimmed;
	}

	private String serializeConfig() {
		List<String> entries = new ArrayList<>();

		for (ArgumentField field : argumentFields) {
			entries.add(field.argument().name() + "=" + field.input().getValue().replace(";", "\\;"));
		}

		return String.join(";", entries);
	}

	private Map<String, String> deserializeConfig(String config) {
		Map<String, String> values = new LinkedHashMap<>();

		if (config == null || config.isBlank()) {
			return values;
		}

		for (String entry : config.split(";", -1)) {
			int separator = entry.indexOf('=');

			if (separator <= 0) {
				continue;
			}

			String key = entry.substring(0, separator);
			String value = entry.substring(separator + 1).replace("\\;", ";");
			values.put(key, value);
		}

		return values;
	}

	private List<String> splitArguments(String input) {
		if (input.isBlank()) {
			return List.of();
		}

		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean insideQuotes = false;

		for (int index = 0; index < input.length(); index++) {
			char character = input.charAt(index);

			if (character == '"') {
				insideQuotes = !insideQuotes;
				continue;
			}

			if (Character.isWhitespace(character) && !insideQuotes) {
				addPart(parts, current);
				continue;
			}

			current.append(character);
		}

		addPart(parts, current);
		return parts;
	}

	private void addPart(List<String> parts, StringBuilder current) {
		if (!current.isEmpty()) {
			parts.add(current.toString());
			current.setLength(0);
		}
	}

	private ConfigComponent createInput(CommandArgument argument, int x, int y, int width) {
		if (argument.type() == ArgumentType.LIST) {
			return new ConfigListSelect(x, y, width, FIELD_HEIGHT, argument.options());
		}

		if (argument.type() == ArgumentType.BOOLEAN) {
			return new ConfigCheckbox(x, y, width, FIELD_HEIGHT);
		}

		if (argument.hasRange()) {
			return new ConfigSliderField(x, y, width, FIELD_HEIGHT, argument.min(), argument.max(), argument.type());
		}

		return new ConfigTextBox(x, y, width, FIELD_HEIGHT, getInputMode(argument));
	}

	private ConfigTextBox.InputMode getInputMode(CommandArgument argument) {
		return switch (argument.type()) {
			case INT -> ConfigTextBox.InputMode.INT;
			case DOUBLE -> ConfigTextBox.InputMode.DOUBLE;
			default -> ConfigTextBox.InputMode.TEXT;
		};
	}

	private boolean isOpenListSelect(ConfigComponent input) {
		return input instanceof ConfigListSelect select && select.isOpen();
	}

	private void clearFocusExcept(ConfigComponent focusedInput) {
		for (ArgumentField field : argumentFields) {
			if (field.input() != focusedInput) {
				field.input().setFocused(false);
			}
		}
	}

	private int getContentTop(Font font) {
		return y + 46 + getInfoHeight(font) + 8;
	}

	private int getButtonY() {
		return y + height - BUTTON_HEIGHT - 12;
	}

	private int getInfoHeight(Font font) {
		List<String> lines = getInfoLines(font);
		return lines.isEmpty() ? 0 : lines.size() * INFO_LINE_HEIGHT + 4;
	}

	private List<String> getInfoLines(Font font) {
		List<String> lines = new ArrayList<>();

		if (description != null && !description.isBlank()) {
			lines.addAll(wrapLine(description, Math.max(40, width - CONTENT_PADDING * 2), font));
		}

		return lines;
	}

	private List<String> wrapLine(String value, int maxWidth, Font font) {
		List<String> lines = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		for (String word : value.split(" ")) {
			String next = current.isEmpty() ? word : current + " " + word;

			if (!current.isEmpty() && font.width(next) > maxWidth) {
				lines.add(current.toString());
				current.setLength(0);
				current.append(word);
				continue;
			}

			current.setLength(0);
			current.append(next);
		}

		if (!current.isEmpty()) {
			lines.add(current.toString());
		}

		return lines;
	}

	private void clampScroll(Font font) {
		if (font == null) {
			return;
		}

		int contentTop = getContentTop(font);
		int contentBottom = getButtonY() - 10;
		int visible = Math.max(0, contentBottom - contentTop);
		int total = arguments.isEmpty() ? 20 : arguments.size() * FIELD_GAP;
		int maxScroll = Math.max(0, total - visible);
		scroll = Math.max(0, Math.min(scroll, maxScroll));
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	private record ArgumentField(CommandArgument argument, ConfigComponent input, int baseY) {
	}
}
