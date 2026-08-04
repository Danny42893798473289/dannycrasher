package me.dannycrasher.client.gui.theme;

import java.awt.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class ClickGuiTheme {
	public enum Accent {
		PURPLE(new Color(188, 0, 255), new Color(120, 0, 180)),
		BLUE(new Color(74, 163, 255), new Color(25, 92, 210)),
		GREEN(new Color(87, 255, 173), new Color(24, 140, 90)),
		RED(new Color(255, 94, 110), new Color(180, 40, 55));

		private final Color bright;
		private final Color dark;

		Accent(Color bright, Color dark) {
			this.bright = bright;
			this.dark = dark;
		}

		public int brightRgb() {
			return bright.getRGB();
		}

		public int darkRgb() {
			return dark.getRGB();
		}

		public static Accent fromId(String id) {
			if (id == null || id.isBlank()) {
				return PURPLE;
			}

			try {
				return Accent.valueOf(id.trim().toUpperCase());
			} catch (IllegalArgumentException ignored) {
				return PURPLE;
			}
		}
	}

	public static final int TEXT = new Color(248, 248, 255).getRGB();
	public static final int MUTED = new Color(170, 170, 185).getRGB();
	public static final int SOFT = new Color(210, 210, 224).getRGB();
	public static final int OVERLAY = new Color(8, 10, 16, 55).getRGB();
	public static final int PANEL = new Color(26, 26, 32).getRGB();
	public static final int PANEL_INNER = new Color(255, 255, 255, 22).getRGB();
	public static final int PANEL_SOFT = new Color(255, 255, 255, 36).getRGB();
	public static final int BORDER = new Color(255, 255, 255, 55).getRGB();
	public static final int DANGER = new Color(255, 94, 110).getRGB();
	public static final int SUCCESS = new Color(87, 255, 173).getRGB();

	private static Accent accent = Accent.PURPLE;
	private static int panelOpacity = 170;

	private ClickGuiTheme() {
	}

	public static void setAccent(Accent value) {
		accent = value == null ? Accent.PURPLE : value;
	}

	public static Accent getAccent() {
		return accent;
	}

	public static void setPanelOpacity(int opacity) {
		panelOpacity = Math.max(100, Math.min(230, opacity));
	}

	public static int getPanelOpacity() {
		return panelOpacity;
	}

	public static int accent() {
		return accent.brightRgb();
	}

	public static int accentDark() {
		return accent.darkRgb();
	}

	public static int panelColor() {
		return new Color(22, 24, 32, panelOpacity).getRGB();
	}

	public static int panelInnerColor() {
		return new Color(255, 255, 255, Math.max(18, panelOpacity / 6)).getRGB();
	}

	public static void renderOverlay(GuiGraphics graphics, int width, int height) {
		graphics.fill(0, 0, width, height, OVERLAY);
	}

	public static void renderPanel(GuiGraphics graphics, int x, int y, int width, int height) {
		GlassRenderer.glassPanel(
				graphics,
				x,
				y,
				width,
				height,
				GlassRenderer.RADIUS_PANEL,
				panelColor(),
				GlassRenderer.withAlpha(accent(), 110)
		);
	}

	public static void renderCard(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered, boolean selected) {
		GlassRenderer.glassCard(graphics, x, y, width, height, GlassRenderer.RADIUS_CARD, hovered, selected, accent());
	}

	public static void renderNavItem(GuiGraphics graphics, Font font, String label, int x, int y, int width, int height, boolean selected, boolean hovered) {
		GlassRenderer.glassNav(graphics, x, y, width, height, GlassRenderer.RADIUS_CHIP, selected, hovered, accent());
		graphics.drawString(font, label, x + 16, y + (height - 8) / 2, selected ? TEXT : SOFT, false);
	}

	public static void renderChip(GuiGraphics graphics, Font font, String label, int x, int y, int width, int height, boolean active, boolean hovered) {
		GlassRenderer.glassChip(graphics, x, y, width, height, GlassRenderer.RADIUS_CHIP, active, hovered, accent());
		int textWidth = font.width(label);
		graphics.drawString(font, label, x + (width - textWidth) / 2, y + (height - 8) / 2, TEXT, false);
	}

	public static void renderAccentButton(GuiGraphics graphics, Font font, String label, int x, int y, int width, int height, boolean hovered) {
		GlassRenderer.glassButton(graphics, x, y, width, height, GlassRenderer.RADIUS_CHIP, hovered, true, accent());
		int textWidth = font.width(label);
		graphics.drawString(font, label, x + (width - textWidth) / 2, y + (height - 8) / 2, TEXT, true);
	}

	public static void renderButton(GuiGraphics graphics, Font font, String label, int x, int y, int width, int height, boolean hovered) {
		GlassRenderer.glassButton(graphics, x, y, width, height, GlassRenderer.RADIUS_CHIP, hovered, false, accent());
		int textWidth = font.width(label);
		graphics.drawString(font, label, x + (width - textWidth) / 2, y + (height - 8) / 2, TEXT, false);
	}

	public static void renderField(GuiGraphics graphics, int x, int y, int width, int height, boolean focused, boolean hovered) {
		GlassRenderer.glassField(graphics, x, y, width, height, GlassRenderer.RADIUS_FIELD, focused, hovered, accent());
	}

	public static void label(GuiGraphics graphics, Font font, String text, int x, int y, int color) {
		graphics.drawString(font, text, x, y, color, false);
	}
}
