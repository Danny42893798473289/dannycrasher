package me.dannycrasher.client.gui.component;

import java.util.List;
import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import me.dannycrasher.client.gui.theme.GlassRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ConfigListSelect implements ConfigComponent {
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final List<String> options;
	private String value;
	private boolean focused;
	private boolean open;

	public ConfigListSelect(int x, int y, int width, int height, List<String> options) {
		if (options == null || options.isEmpty()) {
			throw new IllegalArgumentException("List select requires at least one option");
		}

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.options = List.copyOf(options);
		this.value = this.options.getFirst();
	}

	@Override
	public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY, float partialTick) {
		boolean hovered = isInside(mouseX, mouseY, x, y, width, height);
		ClickGuiTheme.renderField(graphics, x, y, width, height, focused || open, hovered);
		graphics.drawString(font, value, x + 5, y + 5, ClickGuiTheme.TEXT, false);
		graphics.drawString(font, open ? "-" : "+", x + width - 12, y + 5, ClickGuiTheme.SOFT, false);

		if (!open) {
			return;
		}

		int dropdownTop = y + height + 2;
		int dropdownHeight = options.size() * height;
		GlassRenderer.glassPanel(
				graphics,
				x,
				dropdownTop,
				width,
				dropdownHeight,
				GlassRenderer.RADIUS_FIELD,
				GlassRenderer.withAlpha(0x161820, 220),
				GlassRenderer.withAlpha(ClickGuiTheme.accent(), 120)
		);

		for (int index = 0; index < options.size(); index++) {
			int optionY = dropdownTop + index * height;
			String option = options.get(index);
			boolean optionHovered = isInside(mouseX, mouseY, x, optionY, width, height);
			boolean selected = option.equals(value);

			if (selected || optionHovered) {
				GlassRenderer.fillRounded(
						graphics,
						x + 2,
						optionY + 1,
						width - 4,
						height - 2,
						4,
						selected ? GlassRenderer.withAlpha(ClickGuiTheme.accent(), 90) : GlassRenderer.withAlpha(0xFFFFFF, 28)
				);
			}

			graphics.drawString(font, option, x + 5, optionY + 5, ClickGuiTheme.TEXT, false);
		}
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0) {
			return false;
		}

		if (open) {
			int dropdownTop = y + height + 2;
			for (int index = 0; index < options.size(); index++) {
				int optionY = dropdownTop + index * height;

				if (isInside(mouseX, mouseY, x, optionY, width, height)) {
					value = options.get(index);
					focused = true;
					open = false;
					return true;
				}
			}
		}

		if (!isInside(mouseX, mouseY, x, y, width, height)) {
			return false;
		}

		focused = true;
		open = !open;
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		return false;
	}

	@Override
	public void mouseReleased() {
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!focused) {
			return false;
		}

		if (keyCode == 256) {
			open = false;
			return true;
		}

		return false;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return false;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;

		if (!focused) {
			open = false;
		}
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		if (value != null && options.contains(value)) {
			this.value = value;
			return;
		}

		this.value = options.getFirst();
	}

	@Override
	public int getY() {
		return y;
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
