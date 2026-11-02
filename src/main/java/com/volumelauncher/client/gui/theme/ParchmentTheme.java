package com.volumelauncher.client.gui.theme;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ParchmentTheme {
    public static final int PARCHMENT_BG = 0xEEEDE0C8;
    public static final int PARCHMENT_LIGHT = 0xFFF4ECD8;
    public static final int PARCHMENT_HEADER = 0xFFDAC3A2;
    public static final int PARCHMENT_DARK = 0xFFC2AB89;
    
    public static final int WOOD_DARK = 0xFF2A1B12;
    public static final int WOOD_BORDER = 0xFF4B3423;
    public static final int WOOD_HIGHLIGHT = 0xFF6B4D36;

    public static final int GOLD_ACCENT = 0xFFD4AF37;
    public static final int GOLD_HOVER = 0xFFECC450;
    public static final int GOLD_MUTED = 0xFFA88928;

    public static final int TEXT_DARK = 0xFF23170F;
    public static final int TEXT_MUTED = 0xFF5C4736;
    public static final int TEXT_LIGHT = 0xFFF0E5D0;

    public static final int STATE_ON = 0xFF4A7C3A;
    public static final int STATE_OFF = 0xFF8A3B30;

    public static void drawCarvedBox(GuiGraphicsExtractor extractor, int x, int y, int width, int height, int bgColor) {
        // Outer dark shadow
        extractor.fill(x - 2, y - 2, x + width + 2, y + height + 2, WOOD_DARK);
        // Wood border
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, WOOD_BORDER);
        // Inner highlight edge
        extractor.fill(x, y, x + width, y + 1, WOOD_HIGHLIGHT);
        extractor.fill(x, y, x + 1, y + height, WOOD_HIGHLIGHT);
        // Main panel fill
        extractor.fill(x, y, x + width, y + height, bgColor);
    }

    public static void drawParchmentPanel(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        drawCarvedBox(extractor, x, y, width, height, PARCHMENT_BG);
        // Subtle parchment texture simulated via inner frame lines
        extractor.fill(x + 2, y + 2, x + width - 2, y + 3, PARCHMENT_LIGHT);
        extractor.fill(x + 2, y + height - 3, x + width - 2, y + height - 2, PARCHMENT_DARK);
    }

    public static void drawHeader(GuiGraphicsExtractor extractor, int x, int y, int width, int height, String title, Font font) {
        // Carved header bar
        extractor.fill(x - 1, y - 1, x + width + 1, y + height, WOOD_BORDER);
        extractor.fill(x, y, x + width, y + height, PARCHMENT_HEADER);
        extractor.fill(x, y + height - 1, x + width, y + height, GOLD_ACCENT);

        // Header Title
        extractor.text(font, title, x + 8, y + (height - 8) / 2, TEXT_DARK, false);
    }

    public static void drawParchmentButton(GuiGraphicsExtractor extractor, int x, int y, int width, int height, String label, boolean hovered, Font font) {
        int bg = hovered ? PARCHMENT_LIGHT : PARCHMENT_HEADER;
        int border = hovered ? GOLD_HOVER : WOOD_BORDER;

        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, border);
        extractor.fill(x, y, x + width, y + height, bg);

        if (hovered) {
            extractor.fill(x, y, x + width, y + 1, GOLD_HOVER);
        }

        int textWidth = font.width(label);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;
        extractor.text(font, label, textX, textY, hovered ? TEXT_DARK : TEXT_MUTED, false);
    }
}
