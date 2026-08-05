package me.dannycrasher.client.gui.theme;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphics;

public final class GlassRenderer {
	public static final int RADIUS_PANEL = 16;
	public static final int RADIUS_CARD = 10;
	public static final int RADIUS_CHIP = 8;
	public static final int RADIUS_FIELD = 6;

	private static final float AA = 1.0F;

	private GlassRenderer() {
	}

	public static void fillRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		if (width <= 0 || height <= 0) {
			return;
		}

		int r = clampRadius(radius, width, height);
		if (r == 0) {
			graphics.fill(x, y, x + width, y + height, color);
			return;
		}

		float cx = x + width * 0.5F;
		float cy = y + height * 0.5F;
		float halfW = width * 0.5F;
		float halfH = height * 0.5F;

		// Solid middle — no corner involvement, avoids seams.
		if (height > r * 2) {
			graphics.fill(x, y + r, x + width, y + height - r, color);
		}

		drawSdfBand(graphics, x, y, width, r, cx, cy, halfW, halfH, r, color, true, 0.0F);
		drawSdfBand(graphics, x, y + height - r, width, r, cx, cy, halfW, halfH, r, color, true, 0.0F);
	}

	public static void strokeRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		strokeRounded(graphics, x, y, width, height, radius, color, 1.15F);
	}

	public static void strokeRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color, float thickness) {
		if (width <= 0 || height <= 0) {
			return;
		}

		int r = clampRadius(radius, width, height);
		float cx = x + width * 0.5F;
		float cy = y + height * 0.5F;
		float halfW = width * 0.5F;
		float halfH = height * 0.5F;
		float halfThick = Math.max(0.55F, thickness * 0.5F);

		int pad = (int) Math.ceil(halfThick + AA);
		// Top/bottom and side bands meet at y+r / y+h-r with no overlap so joins don't darken.
		drawSdfBand(graphics, x - pad, y - pad, width + pad * 2, r + pad, cx, cy, halfW, halfH, r, color, false, halfThick);
		drawSdfBand(graphics, x - pad, y + height - r, width + pad * 2, r + pad, cx, cy, halfW, halfH, r, color, false, halfThick);

		if (height > r * 2) {
			drawSdfBand(graphics, x - pad, y + r, pad * 2 + 2, height - r * 2, cx, cy, halfW, halfH, r, color, false, halfThick);
			drawSdfBand(graphics, x + width - 2 - pad, y + r, pad * 2 + 2, height - r * 2, cx, cy, halfW, halfH, r, color, false, halfThick);
		}
	}

	public static void glassPanel(GuiGraphics graphics, int x, int y, int width, int height, int radius, int bodyColor, int borderColor) {
		fillRounded(graphics, x + 2, y + 3, width, height, radius, withAlpha(0x000000, 50));
		fillRounded(graphics, x, y, width, height, radius, bodyColor);

		int sheenHeight = Math.max(10, Math.min(height / 4, 26));
		fillVerticalGradient(
				graphics,
				x,
				y,
				width,
				height,
				y,
				sheenHeight,
				radius,
				withAlpha(0xFFFFFF, 38),
				withAlpha(0xFFFFFF, 0)
		);

		int shadeHeight = Math.max(8, height / 7);
		fillVerticalGradient(
				graphics,
				x,
				y,
				width,
				height,
				y + height - shadeHeight,
				shadeHeight,
				radius,
				withAlpha(0x000000, 0),
				withAlpha(0x000000, 34)
		);

		strokeRounded(graphics, x, y, width, height, radius, borderColor, 1.2F);
	}

	public static void glassCard(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean hovered, boolean selected, int accent) {
		int body = selected
				? mixAccentBody(accent, 88)
				: hovered ? new Color(255, 255, 255, 40).getRGB() : new Color(255, 255, 255, 24).getRGB();
		int border = selected ? accent : hovered ? withAlpha(0xFFFFFF, 78) : withAlpha(0xFFFFFF, 42);

		fillRounded(graphics, x + 1, y + 2, width, height, radius, withAlpha(0x000000, 36));
		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x,
				y,
				width,
				height,
				y,
				Math.max(6, height / 3),
				radius,
				withAlpha(0xFFFFFF, selected || hovered ? 32 : 18),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, selected ? 1.35F : 1.1F);
	}

	public static void glassChip(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean active, boolean hovered, int accent) {
		int body = active
				? mixAccentBody(accent, 108)
				: hovered ? new Color(255, 255, 255, 42).getRGB() : new Color(255, 255, 255, 26).getRGB();
		int border = active ? accent : withAlpha(0xFFFFFF, hovered ? 70 : 42);

		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x,
				y,
				width,
				height,
				y,
				Math.max(4, height / 2),
				radius,
				withAlpha(0xFFFFFF, 26),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, 1.1F);
	}

	public static void glassButton(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean hovered, boolean accented, int accent) {
		int body = accented
				? (hovered ? withAlpha(accent, 185) : withAlpha(accent, 135))
				: (hovered ? new Color(255, 255, 255, 50).getRGB() : new Color(255, 255, 255, 30).getRGB());
		int border = accented ? withAlpha(0xFFFFFF, 95) : withAlpha(0xFFFFFF, hovered ? 72 : 46);

		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x,
				y,
				width,
				height,
				y,
				Math.max(4, height / 2),
				radius,
				withAlpha(0xFFFFFF, accented ? 50 : 28),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, 1.15F);
	}

	public static void glassField(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean focused, boolean hovered, int accent) {
		int body = focused || hovered ? new Color(255, 255, 255, 38).getRGB() : new Color(255, 255, 255, 24).getRGB();
		int border = focused || hovered ? accent : withAlpha(0xFFFFFF, 48);

		fillRounded(graphics, x, y, width, height, radius, body);
		strokeRounded(graphics, x, y, width, height, radius, border, focused ? 1.3F : 1.05F);
		if (focused) {
			fillRounded(graphics, x + 3, y + height - 3, width - 6, 2, 1, withAlpha(accent, 180));
		}
	}

	public static void glassNav(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean selected, boolean hovered, int accent) {
		if (selected) {
			fillRounded(graphics, x, y, width, height, radius, mixAccentBody(accent, 72));
			strokeRounded(graphics, x, y, width, height, radius, withAlpha(accent, 150), 1.15F);
			fillRounded(graphics, x + 4, y + 5, 3, height - 10, 2, accent);
		} else if (hovered) {
			fillRounded(graphics, x, y, width, height, radius, new Color(255, 255, 255, 30).getRGB());
			strokeRounded(graphics, x, y, width, height, radius, withAlpha(0xFFFFFF, 38), 1.05F);
		}
	}

	public static int withAlpha(int rgb, int alpha) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new Color(r, g, b, Math.max(0, Math.min(255, alpha))).getRGB();
	}

	private static void fillVerticalGradient(
			GuiGraphics graphics,
			int panelX,
			int panelY,
			int panelW,
			int panelH,
			int drawY,
			int drawH,
			int radius,
			int topColor,
			int bottomColor
	) {
		if (panelW <= 0 || drawH <= 0) {
			return;
		}

		int r = clampRadius(radius, panelW, panelH);
		float cx = panelX + panelW * 0.5F;
		float cy = panelY + panelH * 0.5F;
		float halfW = panelW * 0.5F;
		float halfH = panelH * 0.5F;

		for (int row = 0; row < drawH; row++) {
			float t = drawH == 1 ? 0.0F : row / (float) (drawH - 1);
			int rowColor = lerpColor(topColor, bottomColor, t);
			drawSdfRow(graphics, panelX, drawY + row, panelW, cx, cy, halfW, halfH, r, rowColor, true, 0.0F);
		}
	}

	/**
	 * Draws a horizontal band of rows using a shared rounded-rect SDF so edges and corners match.
	 *
	 * @param fill when true, fills the interior; when false, draws a stroke ring of halfThickness
	 */
	private static void drawSdfBand(
			GuiGraphics graphics,
			int x,
			int y,
			int width,
			int height,
			float cx,
			float cy,
			float halfW,
			float halfH,
			float radius,
			int color,
			boolean fill,
			float halfThickness
	) {
		if (width <= 0 || height <= 0) {
			return;
		}

		for (int row = 0; row < height; row++) {
			drawSdfRow(graphics, x, y + row, width, cx, cy, halfW, halfH, radius, color, fill, halfThickness);
		}
	}

	private static void drawSdfRow(
			GuiGraphics graphics,
			int x,
			int rowY,
			int width,
			float cx,
			float cy,
			float halfW,
			float halfH,
			float radius,
			int color,
			boolean fill,
			float halfThickness
	) {
		int runStart = -1;
		int runAlpha = -1;

		for (int col = 0; col < width; col++) {
			float sd = sdRoundedRect(x + col + 0.5F, rowY + 0.5F, cx, cy, halfW, halfH, radius);
			float coverage = fill ? coverageFill(sd) : coverageStroke(sd, halfThickness);
			int alpha = scaleAlpha(color, coverage);

			if (alpha <= 0) {
				if (runStart >= 0) {
					graphics.fill(x + runStart, rowY, x + col, rowY + 1, withAlpha(color, runAlpha));
					runStart = -1;
					runAlpha = -1;
				}
				continue;
			}

			if (runStart < 0) {
				runStart = col;
				runAlpha = alpha;
			} else if (alpha != runAlpha) {
				graphics.fill(x + runStart, rowY, x + col, rowY + 1, withAlpha(color, runAlpha));
				runStart = col;
				runAlpha = alpha;
			}
		}

		if (runStart >= 0) {
			graphics.fill(x + runStart, rowY, x + width, rowY + 1, withAlpha(color, runAlpha));
		}
	}

	private static float sdRoundedRect(float px, float py, float cx, float cy, float halfW, float halfH, float radius) {
		float dx = Math.abs(px - cx) - (halfW - radius);
		float dy = Math.abs(py - cy) - (halfH - radius);
		float ox = Math.max(dx, 0.0F);
		float oy = Math.max(dy, 0.0F);
		return (float) Math.sqrt(ox * ox + oy * oy) + Math.min(Math.max(dx, dy), 0.0F) - radius;
	}

	private static float coverageFill(float sd) {
		return 1.0F - smoothstep(-AA, AA, sd);
	}

	private static float coverageStroke(float sd, float halfThickness) {
		float outer = 1.0F - smoothstep(halfThickness - AA * 0.5F, halfThickness + AA, sd);
		float inner = smoothstep(-halfThickness - AA, -halfThickness + AA * 0.5F, sd);
		return clamp01(outer * inner);
	}

	private static float smoothstep(float edge0, float edge1, float value) {
		if (edge0 >= edge1) {
			return value < edge0 ? 0.0F : 1.0F;
		}

		float t = clamp01((value - edge0) / (edge1 - edge0));
		return t * t * (3.0F - 2.0F * t);
	}

	private static float clamp01(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}

	private static int scaleAlpha(int color, float coverage) {
		if (coverage <= 0.02F) {
			return 0;
		}

		int baseAlpha = (color >>> 24) & 0xFF;
		return Math.max(0, Math.min(255, Math.round(baseAlpha * coverage)));
	}

	private static int lerpColor(int from, int to, float t) {
		t = clamp01(t);
		int a1 = (from >>> 24) & 0xFF;
		int r1 = (from >>> 16) & 0xFF;
		int g1 = (from >>> 8) & 0xFF;
		int b1 = from & 0xFF;
		int a2 = (to >>> 24) & 0xFF;
		int r2 = (to >>> 16) & 0xFF;
		int g2 = (to >>> 8) & 0xFF;
		int b2 = to & 0xFF;
		return new Color(
				Math.round(r1 + (r2 - r1) * t),
				Math.round(g1 + (g2 - g1) * t),
				Math.round(b1 + (b2 - b1) * t),
				Math.round(a1 + (a2 - a1) * t)
		).getRGB();
	}

	private static int mixAccentBody(int accent, int alpha) {
		int r = (accent >> 16) & 0xFF;
		int g = (accent >> 8) & 0xFF;
		int b = accent & 0xFF;
		int mixedR = (r * 2 + 255) / 3;
		int mixedG = (g * 2 + 255) / 3;
		int mixedB = (b * 2 + 255) / 3;
		return new Color(mixedR, mixedG, mixedB, Math.max(0, Math.min(255, alpha))).getRGB();
	}

	private static int clampRadius(int radius, int width, int height) {
		return Math.max(0, Math.min(radius, Math.min(width, height) / 2));
	}
}
