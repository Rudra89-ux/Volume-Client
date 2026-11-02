package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.hud.HudModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DirectionDisplayModule extends HudModule {
    private static final String[] CARDINALS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public DirectionDisplayModule() {
        super("direction", "Direction Display", "Displays your current cardinal heading and degree angle.", 180, 42);
    }

    private String getDirectionString() {
        if (mc.player == null) return "N 0°";
        float yaw = (mc.player.getYRot() % 360 + 360) % 360;
        int idx = (int) Math.floor((yaw + 22.5) / 45.0) % 8;
        return CARDINALS[idx] + " (" + String.format("%.0f°", yaw) + ")";
    }

    @Override
    public int getWidth(Font font) {
        return font.width("Heading: " + getDirectionString()) + 14;
    }

    @Override
    public int getHeight(Font font) {
        return 16;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        String str = "Heading: §6" + getDirectionString();
        extractor.text(font, str, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);
    }
}
