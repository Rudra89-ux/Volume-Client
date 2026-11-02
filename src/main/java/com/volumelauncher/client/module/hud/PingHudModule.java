package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHudModule extends HudModule {
    public PingHudModule() {
        super("ping", "Ping Display", "Displays network latency in milliseconds.", 10, 50);
    }

    public int getPing() {
        if (mc.player == null || mc.getConnection() == null) return 0;
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return info != null ? info.getLatency() : 0;
    }

    @Override
    public int getWidth(Font font) {
        String str = "Ping: " + getPing() + "ms";
        return font.width(str) + 14;
    }

    @Override
    public int getHeight(Font font) {
        return 16;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        String text = "§6Ping: §0" + getPing() + "ms";
        extractor.text(font, text, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);
    }
}
