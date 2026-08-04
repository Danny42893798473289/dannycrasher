package me.dannycrasher.client.gui.theme;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphics;

public final class GlassRenderer {
	public static final int RADIUS_PANEL = 18;
	public static final int RADIUS_CARD = 12;
	public static final int RADIUS_CHIP = 9;
	public static final int RADIUS_FIELD = 7;

	private static final float EDGE_SOFTNESS = 1.35F;

	private GlassRenderer() {
	}

	public static void fillRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		if (width <= 0 || height <= 0) {
			return;
		}

		int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
		if (r == 0) {
			graphics.fill(x, y, x + width, y + height, color);
			return;
		}

		graphics.fill(x + r, y, x + width - r, y + height, color);
		graphics.fill(x, y + r, x + r, y + height - r, color);
		graphics.fill(x + width - r, y + r, x + width, y + height - r, color);

		fillCorner(graphics, x, y, r, color, Corner.TOP_LEFT);
		fillCorner(graphics, x + width - r, y, r, color, Corner.TOP_RIGHT);
		fillCorner(graphics, x, y + height - r, r, color, Corner.BOTTOM_LEFT);
		fillCorner(graphics, x + width - r, y + height - r, r, color, Corner.BOTTOM_RIGHT);
	}

	public static void strokeRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		strokeRounded(graphics, x, y, width, height, radius, color, 1.25F);
	}

	public static void strokeRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color, float thickness) {
		if (width <= 0 || height <= 0) {
			return;
		}

		int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
		drawSoftHLine(graphics, x + r, y, width - r * 2, color, thickness);
		drawSoftHLine(graphics, x + r, y + height - 1, width - r * 2, color, thickness);
		drawSoftVLine(graphics, x, y + r, height - r * 2, color, thickness);
		drawSoftVLine(graphics, x + width - 1, y + r, height - r * 2, color, thickness);

		strokeCorner(graphics, x, y, r, color, thickness, Corner.TOP_LEFT);
		strokeCorner(graphics, x + width - r, y, r, color, thickness, Corner.TOP_RIGHT);
		strokeCorner(graphics, x, y + height - r, r, color, thickness, Corner.BOTTOM_LEFT);
		strokeCorner(graphics, x + width - r, y + height - r, r, color, thickness, Corner.BOTTOM_RIGHT);
	}

	public static void glassPanel(GuiGraphics graphics, int x, int y, int width, int height, int radius, int bodyColor, int borderColor) {
		fillRounded(graphics, x + 3, y + 4, width, height, radius, withAlpha(0x000000, 55));
		fillRounded(graphics, x, y, width, height, radius, bodyColor);

		int sheenHeight = Math.max(10, Math.min(height / 4, 28));
		fillVerticalGradient(
				graphics,
				x + 2,
				y + 2,
				width - 4,
				sheenHeight,
				Math.max(4, radius - 2),
				withAlpha(0xFFFFFF, 42),
				withAlpha(0xFFFFFF, 0)
		);

		int shadeHeight = Math.max(8, height / 7);
		fillVerticalGradient(
				graphics,
				x + 3,
				y + height - shadeHeight - 2,
				width - 6,
				shadeHeight,
				Math.max(4, radius - 3),
				withAlpha(0x000000, 0),
				withAlpha(0x000000, 38)
		);

		strokeRounded(graphics, x, y, width, height, radius, borderColor, 1.4F);
		strokeRounded(graphics, x + 1, y + 1, width - 2, height - 2, Math.max(2, radius - 1), withAlpha(0xFFFFFF, 22), 1.0F);
	}

	public static void glassCard(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean hovered, boolean selected, int accent) {
		int body = selected
				? mixAccentBody(accent, 88)
				: hovered ? new Color(255, 255, 255, 40).getRGB() : new Color(255, 255, 255, 24).getRGB();
		int border = selected ? accent : hovered ? withAlpha(0xFFFFFF, 78) : withAlpha(0xFFFFFF, 42);

		fillRounded(graphics, x + 1, y + 2, width, height, radius, withAlpha(0x000000, 40));
		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x + 2,
				y + 1,
				width - 4,
				Math.max(6, height / 3),
				Math.max(3, radius - 2),
				withAlpha(0xFFFFFF, selected || hovered ? 36 : 20),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, selected ? 1.5F : 1.2F);

		if (selected) {
			strokeRounded(graphics, x + 1, y + 1, width - 2, height - 2, Math.max(2, radius - 1), withAlpha(accent, 70), 1.0F);
		}
	}

	public static void glassChip(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean active, boolean hovered, int accent) {
		int body = active
				? mixAccentBody(accent, 108)
				: hovered ? new Color(255, 255, 255, 42).getRGB() : new Color(255, 255, 255, 26).getRGB();
		int border = active ? accent : withAlpha(0xFFFFFF, hovered ? 70 : 42);

		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x + 2,
				y + 1,
				width - 4,
				Math.max(4, height / 2),
				Math.max(2, radius - 2),
				withAlpha(0xFFFFFF, 28),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, 1.2F);
	}

	public static void glassButton(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean hovered, boolean accented, int accent) {
		int body = accented
				? (hovered ? withAlpha(accent, 185) : withAlpha(accent, 135))
				: (hovered ? new Color(255, 255, 255, 50).getRGB() : new Color(255, 255, 255, 30).getRGB());
		int border = accented ? withAlpha(0xFFFFFF, 95) : withAlpha(0xFFFFFF, hovered ? 72 : 46);

		fillRounded(graphics, x, y, width, height, radius, body);
		fillVerticalGradient(
				graphics,
				x + 2,
				y + 1,
				width - 4,
				Math.max(4, height / 2),
				Math.max(2, radius - 2),
				withAlpha(0xFFFFFF, accented ? 55 : 30),
				withAlpha(0xFFFFFF, 0)
		);
		strokeRounded(graphics, x, y, width, height, radius, border, 1.25F);
	}

	public static void glassField(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean focused, boolean hovered, int accent) {
		int body = focused || hovered ? new Color(255, 255, 255, 38).getRGB() : new Color(255, 255, 255, 24).getRGB();
		int border = focused || hovered ? accent : withAlpha(0xFFFFFF, 48);

		fillRounded(graphics, x, y, width, height, radius, body);
		strokeRounded(graphics, x, y, width, height, radius, border, focused ? 1.4F : 1.15F);
		if (focused) {
			fillRounded(graphics, x + 3, y + height - 3, width - 6, 2, 1, withAlpha(accent, 180));
		}
	}

	public static void glassNav(GuiGraphics graphics, int x, int y, int width, int height, int radius, boolean selected, boolean hovered, int accent) {
		if (selected) {
			fillRounded(graphics, x, y, width, height, radius, mixAccentBody(accent, 72));
			strokeRounded(graphics, x, y, width, height, radius, withAlpha(accent, 150), 1.25F);
			fillRounded(graphics, x + 4, y + 5, 3, height - 10, 2, accent);
		} else if (hovered) {
			fillRounded(graphics, x, y, width, height, radius, new Color(255, 255, 255, 30).getRGB());
			strokeRounded(graphics, x, y, width, height, radius, withAlpha(0xFFFFFF, 38), 1.1F);
		}
	}

	public static int withAlpha(int rgb, int alpha) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new Color(r, g, b, Math.max(0, Math.min(255, alpha))).getRGB();
	}

	private static void fillVerticalGradient(GuiGraphics graphics, int x, int y, int width, int height, int radius, int topColor, int bottomColor) {
		if (width <= 0 || height <= 0) {
			return;
		}

		int steps = Math.max(1, height);
		for (int i = 0; i < steps; i++) {
			float t = steps == 1 ? 0.0F : i / (float) (steps - 1);
			int color = lerpColor(topColor, bottomColor, t);
			int rowY = y + i;
			int inset = horizontalInsetForRoundedRow(i, height, radius);
			int rowWidth = width - inset * 2;
			if (rowWidth > 0) {
				graphics.fill(x + inset, rowY, x + inset + rowWidth, rowY + 1, color);
			}
		}
	}

	private static int horizontalInsetForRoundedRow(int row, int height, int radius) {
		int r = Math.max(0, Math.min(radius, height / 2));
		if (row < r) {
			return cornerPixelInset(row, r);
		}

		if (row >= height - r) {
			return cornerPixelInset(height - 1 - row, r);
		}

		return 0;
	}

	private static int cornerPixelInset(int row, int radius) {
		double dy = radius - (row + 0.5D);
		double dx = Math.sqrt(Math.max(0.0D, (double) radius * radius - dy * dy));
		return (int) Math.max(0, Math.ceil(radius - dx));
	}

	private static void fillCorner(GuiGraphics graphics, int x, int y, int radius, int color, Corner corner) {
		float cx = corner == Corner.TOP_LEFT || corner == Corner.BOTTOM_LEFT ? x + radius : x;
		float cy = corner == Corner.TOP_LEFT || corner == Corner.TOP_RIGHT ? y + radius : y;

		for (int py = 0; py < radius; py++) {
			int runStart = -1;
			int runAlpha = -1;

			for (int px = 0; px < radius; px++) {
				float sampleX = x + px + 0.5F;
				float sampleY = y + py + 0.5F;
				float dist = distance(sampleX, sampleY, cx, cy);
				float coverage = coverageInside(dist, radius);
				int alpha = scaleAlpha(color, coverage);

				if (alpha <= 0) {
					if (runStart >= 0) {
						graphics.fill(x + runStart, y + py, x + px, y + py + 1, withAlpha(color, runAlpha));
						runStart = -1;
						runAlpha = -1;
					}
					continue;
				}

				if (runStart < 0) {
					runStart = px;
					runAlpha = alpha;
				} else if (alpha != runAlpha) {
					graphics.fill(x + runStart, y + py, x + px, y + py + 1, withAlpha(color, runAlpha));
					runStart = px;
					runAlpha = alpha;
				}
			}

			if (runStart >= 0) {
				graphics.fill(x + runStart, y + py, x + radius, y + py + 1, withAlpha(color, runAlpha));
			}
		}
	}

	private static void strokeCorner(GuiGraphics graphics, int x, int y, int radius, int color, float thickness, Corner corner) {
		float cx = corner == Corner.TOP_LEFT || corner == Corner.BOTTOM_LEFT ? x + radius : x;
		float cy = corner == Corner.TOP_LEFT || corner == Corner.TOP_RIGHT ? y + radius : y;
		float half = thickness * 0.5F;

		for (int py = 0; py < radius + 1; py++) {
			for (int px = 0; px < radius + 1; px++) {
				float sampleX = x + px + 0.5F;
				float sampleY = y + py + 0.5F;
				float dist = distance(sampleX, sampleY, cx, cy);
				float coverage = coverageRing(dist, radius, half);
				int alpha = scaleAlpha(color, coverage);
				if (alpha > 0) {
					graphics.fill(x + px, y + py, x + px + 1, y + py + 1, withAlpha(color, alpha));
				}
			}
		}
	}

	private static void drawSoftHLine(GuiGraphics graphics, int x, int y, int width, int color, float thickness) {
		if (width <= 0) {
			return;
		}

		int bands = Math.max(1, Math.round(thickness + EDGE_SOFTNESS));
		float center = bands * 0.5F;
		for (int i = 0; i < bands; i++) {
			float dist = Math.abs(i + 0.5F - center);
			float coverage = 1.0F - smoothstep(thickness * 0.5F - 0.35F, thickness * 0.5F + EDGE_SOFTNESS, dist);
			int alpha = scaleAlpha(color, coverage);
			if (alpha > 0) {
				graphics.fill(x, y + i - bands / 2, x + width, y + i - bands / 2 + 1, withAlpha(color, alpha));
			}
		}
	}

	private static void drawSoftVLine(GuiGraphics graphics, int x, int y, int height, int color, float thickness) {
		if (height <= 0) {
			return;
		}

		int bands = Math.max(1, Math.round(thickness + EDGE_SOFTNESS));
		float center = bands * 0.5F;
		for (int i = 0; i < bands; i++) {
			float dist = Math.abs(i + 0.5F - center);
			float coverage = 1.0F - smoothstep(thickness * 0.5F - 0.35F, thickness * 0.5F + EDGE_SOFTNESS, dist);
			int alpha = scaleAlpha(color, coverage);
			if (alpha > 0) {
				graphics.fill(x + i - bands / 2, y, x + i - bands / 2 + 1, y + height, withAlpha(color, alpha));
			}
		}
	}

	private static float coverageInside(float dist, float radius) {
		return 1.0F - smoothstep(radius - EDGE_SOFTNESS, radius + 0.35F, dist);
	}

	private static float coverageRing(float dist, float radius, float halfThickness) {
		float outer = 1.0F - smoothstep(radius + halfThickness - 0.2F, radius + halfThickness + EDGE_SOFTNESS, dist);
		float inner = smoothstep(radius - halfThickness - EDGE_SOFTNESS, radius - halfThickness + 0.2F, dist);
		return clamp01(outer * inner);
	}

	private static float distance(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return (float) Math.sqrt(dx * dx + dy * dy);
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
		if (coverage <= 0.01F) {
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

	private enum Corner {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
}
