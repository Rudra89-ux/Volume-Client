package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.setting.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Direction;

public class CoordinatesHudModule extends HudModule {
    private final BooleanSetting showBiome = new BooleanSetting("biome", "Show Biome", "Include current biome name", true);
    private final BooleanSetting showDirection = new BooleanSetting("direction", "Show Facing", "Include cardinal facing direction", true);

    public CoordinatesHudModule() {
        super("coords", "Coordinates", "Displays your current player location and facing direction.", 10, 70);
        addSetting(showBiome);
        addSetting(showDirection);
    }

    private String getCoordString() {
        if (mc.player == null) return "XYZ: 0.0, 0.0, 0.0";
        return String.format("XYZ: %.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
    }

    @Override
    public int getWidth(Font font) {
        int w = font.width(getCoordString());
        if (showDirection.getValue() && mc.player != null) {
            Direction dir = mc.player.getDirection();
            w += font.width(" (" + dir.getName().toUpperCase() + ")");
        }
        return w + 16;
    }

    @Override
    public int getHeight(Font font) {
        return (showBiome.getValue() && mc.player != null && mc.level != null) ? 26 : 16;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        if (mc.player == null) return;

        String coords = "§6XYZ: §0" + String.format("%.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (showDirection.getValue()) {
            coords += " §8(" + mc.player.getDirection().getName().toUpperCase() + ")";
        }
        extractor.text(font, coords, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);

        if (showBiome.getValue() && mc.level != null) {
            String biome = "§8Biome: §0" + mc.level.getBiome(mc.player.blockPosition()).unwrapKey()
                .map(k -> k.identifier().getPath()).orElse("unknown");
            extractor.text(font, biome, getX() + 6, getY() + 15, ParchmentTheme.TEXT_DARK, false);
        }
    }
}
