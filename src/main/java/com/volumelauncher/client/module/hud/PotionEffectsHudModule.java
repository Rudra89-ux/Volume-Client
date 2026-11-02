package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;

public class PotionEffectsHudModule extends HudModule {
    public PotionEffectsHudModule() {
        super("potioneffects", "Potion Effects", "Displays active potion status effects and remaining durations.", 10, 290);
    }

    @Override
    public int getWidth(Font font) {
        if (mc.player == null) return 80;
        int maxW = 70;
        for (MobEffectInstance effect : mc.player.getActiveEffects()) {
            String str = formatEffect(effect);
            maxW = Math.max(maxW, font.width(str) + 14);
        }
        return maxW;
    }

    @Override
    public int getHeight(Font font) {
        if (mc.player == null) return 18;
        int count = mc.player.getActiveEffects().size();
        return Math.max(18, count * 14 + 8);
    }

    private String formatEffect(MobEffectInstance effect) {
        String name = effect.getEffect().value().getDisplayName().getString();
        if (effect.getAmplifier() > 0) {
            name += " " + (effect.getAmplifier() + 1);
        }
        int totalSecs = effect.getDuration() / 20;
        int mins = totalSecs / 60;
        int secs = totalSecs % 60;
        return name + " §8" + String.format("%02d:%02d", mins, secs);
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        if (mc.player == null) return;

        Collection<MobEffectInstance> effects = mc.player.getActiveEffects();
        if (effects.isEmpty()) {
            extractor.text(font, "§8No active effects", getX() + 6, getY() + 5, ParchmentTheme.TEXT_MUTED, false);
            return;
        }

        int ey = getY() + 4;
        for (MobEffectInstance effect : effects) {
            String str = formatEffect(effect);
            extractor.text(font, str, getX() + 6, ey, ParchmentTheme.TEXT_DARK, false);
            ey += 14;
        }
    }
}
