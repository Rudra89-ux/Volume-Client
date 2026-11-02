package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.hud.HudModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeDisplayModule extends HudModule {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final long sessionStart = System.currentTimeMillis();

    public TimeDisplayModule() {
        super("timedisplay", "Time & Playtime", "Displays local system clock and launcher session playtime.", 180, 10);
    }

    @Override
    public int getWidth(Font font) {
        return 95;
    }

    @Override
    public int getHeight(Font font) {
        return 26;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        String clock = "Time: §6" + LocalTime.now().format(TIME_FMT);
        extractor.text(font, clock, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);

        long sessionSecs = (System.currentTimeMillis() - sessionStart) / 1000;
        long mins = sessionSecs / 60;
        long secs = sessionSecs % 60;
        String play = "Play: §8" + String.format("%02d:%02d", mins, secs);
        extractor.text(font, play, getX() + 6, getY() + 14, ParchmentTheme.TEXT_DARK, false);
    }
}
