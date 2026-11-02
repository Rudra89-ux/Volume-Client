package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FpsHudModule extends HudModule {
    public FpsHudModule() {
        super("fps", "FPS Display", "Displays real-time frames per second.", 10, 10);
    }

    @Override
    public int getWidth(Font font) {
        return font.width("FPS: " + mc.getFps()) + 14;
    }

    @Override
    public int getHeight(Font font) {
        return 16;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        String text = "§6FPS: §0" + mc.getFps();
        extractor.text(font, text, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);
    }
}
