package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;

public class TargetHudModule extends HudModule {
    private LivingEntity lastTarget = null;
    private long lastTargetTime = 0;

    public TargetHudModule() {
        super("targethud", "Target HUD", "Displays opponent player health, distance, and combat information.", 180, 150);
    }

    @Override
    public void onTick() {
        if (mc.crosshairPickEntity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) mc.crosshairPickEntity;
            if (target.isAlive() && target != mc.player) {
                lastTarget = target;
                lastTargetTime = System.currentTimeMillis();
            }
        }
        if (System.currentTimeMillis() - lastTargetTime > 3000) {
            lastTarget = null;
        }
    }

    @Override
    public int getWidth(Font font) {
        return 130;
    }

    @Override
    public int getHeight(Font font) {
        return 38;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        if (lastTarget == null || !lastTarget.isAlive()) {
            extractor.text(font, "§8No target locked", getX() + 8, getY() + 14, ParchmentTheme.TEXT_MUTED, false);
            return;
        }

        // Target Name & Distance
        String name = lastTarget.getName().getString();
        double dist = mc.player != null ? mc.player.distanceTo(lastTarget) : 0.0;
        String nameLine = name + " §8" + String.format("%.1fm", dist);
        extractor.text(font, nameLine, getX() + 8, getY() + 6, ParchmentTheme.TEXT_DARK, false);

        // Health Bar
        float hp = lastTarget.getHealth();
        float maxHp = lastTarget.getMaxHealth();
        float ratio = Math.max(0.0f, Math.min(1.0f, hp / maxHp));

        int barX = getX() + 8;
        int barY = getY() + 20;
        int barW = 114;
        int barH = 8;

        extractor.fill(barX, barY, barX + barW, barY + barH, 0x403A281B);
        int fillW = (int) (barW * ratio);
        int barColor = ratio > 0.5f ? ParchmentTheme.STATE_ON : (ratio > 0.25f ? ParchmentTheme.GOLD_ACCENT : ParchmentTheme.STATE_OFF);
        extractor.fill(barX, barY, barX + fillW, barY + barH, barColor);
        extractor.outline(barX, barY, barW, barH, ParchmentTheme.WOOD_DARK);

        String hpText = String.format("%.1f / %.1f HP", hp, maxHp);
        int hpW = font.width(hpText);
        extractor.text(font, hpText, barX + (barW - hpW) / 2, barY, 0xFFFFFFFF, false);
    }
}
