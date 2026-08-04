package me.dannycrasher.client.gui.component;

import me.dannycrasher.client.gui.theme.ClickGuiTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ConfigCheckbox implements ConfigComponent {
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private boolean checked;
	private boolean focused;

	public ConfigCheckbox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(GuiGraphics graphics, Font font, int mouseX, int mouseY, float partialTick) {
		boolean hovered = isInside(mouseX, mouseY);
		int boxSize = Math.min(height, 16);
		int boxY = y + (height - boxSize) / 2;

		ClickGuiTheme.renderField(graphics, x, boxY, boxSize, boxSize, focused, hovered);

		if (checked) {
			int mark = ClickGuiTheme.accent();
			graphics.fill(x + 4, boxY + 8, x + 6, boxY + 10, mark);
			graphics.fill(x + 6, boxY + 10, x + 8, boxY + 12, mark);
			graphics.fill(x + 8, boxY + 6, x + 10, boxY + 10, mark);
			graphics.fill(x + 10, boxY + 4, x + 12, boxY + 8, mark);
		}

		graphics.drawString(font, checked ? "true" : "false", x + boxSize + 7, y + 5, ClickGuiTheme.TEXT, false);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0 || !isInside(mouseX, mouseY)) {
			return false;
		}

		checked = !checked;
		focused = true;
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
		if (!focused || keyCode != 32) {
			return false;
		}

		checked = !checked;
		return true;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return false;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public String getValue() {
		return Boolean.toString(checked);
	}

	@Override
	public void setValue(String value) {
		checked = "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
	}

	@Override
	public int getY() {
		return y;
	}

	private boolean isInside(double mouseX, double mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
