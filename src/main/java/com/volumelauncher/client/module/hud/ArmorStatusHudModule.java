package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.setting.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorStatusHudModule extends HudModule {
    private final BooleanSetting showDamagePercent = new BooleanSetting("damage", "Show Durability %", "Display durability percentage remaining", true);

    public ArmorStatusHudModule() {
        super("armorstatus", "Armor Status", "Displays durability and icons of currently equipped armor and hand items.", 10, 180);
        addSetting(showDamagePercent);
    }

    private List<ItemStack> getEquippedItems() {
        List<ItemStack> list = new ArrayList<>();
        if (mc.player != null) {
            list.add(mc.player.getItemBySlot(EquipmentSlot.HEAD));
            list.add(mc.player.getItemBySlot(EquipmentSlot.CHEST));
            list.add(mc.player.getItemBySlot(EquipmentSlot.LEGS));
            list.add(mc.player.getItemBySlot(EquipmentSlot.FEET));
            list.add(mc.player.getMainHandItem());
            list.add(mc.player.getOffhandItem());
        }
        return list;
    }

    @Override
    public int getWidth(Font font) {
        return 75;
    }

    @Override
    public int getHeight(Font font) {
        return 16 * 6 + 8;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        if (mc.player == null) return;

        int iy = getY() + 4;
        for (ItemStack stack : getEquippedItems()) {
            if (!stack.isEmpty()) {
                extractor.item(stack, getX() + 4, iy);
                extractor.itemDecorations(font, stack, getX() + 4, iy);

                if (showDamagePercent.getValue() && stack.isDamageableItem()) {
                    int max = stack.getMaxDamage();
                    int cur = max - stack.getDamageValue();
                    int pct = (int) Math.round((double) cur / max * 100.0);
                    int col = pct > 60 ? ParchmentTheme.STATE_ON : (pct > 25 ? ParchmentTheme.GOLD_ACCENT : ParchmentTheme.STATE_OFF);
                    extractor.text(font, pct + "%", getX() + 24, iy + 4, col, false);
                }
            }
            iy += 16;
        }
    }
}
