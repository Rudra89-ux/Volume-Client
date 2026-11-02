package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.hud.HudModule;
import com.volumelauncher.client.setting.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemCounterModule extends HudModule {
    private final BooleanSetting countArrows = new BooleanSetting("arrows", "Arrows", "Count arrows", true);
    private final BooleanSetting countTotems = new BooleanSetting("totems", "Totems", "Count totems of undying", true);
    private final BooleanSetting countPearls = new BooleanSetting("pearls", "Ender Pearls", "Count ender pearls", true);

    public ItemCounterModule() {
        super("itemcounter", "Item Counter", "Tracks counts of essential combat items in your inventory.", 10, 360);
        addSetting(countArrows);
        addSetting(countTotems);
        addSetting(countPearls);
    }

    private int countItem(net.minecraft.world.item.Item item) {
        if (mc.player == null) return 0;
        int count = 0;
        for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public int getWidth(Font font) {
        return 90;
    }

    @Override
    public int getHeight(Font font) {
        return 38;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        if (mc.player == null) return;

        int iy = getY() + 4;
        if (countArrows.getValue()) {
            extractor.text(font, "Arrows: §6" + countItem(Items.ARROW), getX() + 6, iy, ParchmentTheme.TEXT_DARK, false);
            iy += 11;
        }
        if (countTotems.getValue()) {
            extractor.text(font, "Totems: §6" + countItem(Items.TOTEM_OF_UNDYING), getX() + 6, iy, ParchmentTheme.TEXT_DARK, false);
            iy += 11;
        }
        if (countPearls.getValue()) {
            extractor.text(font, "Pearls: §6" + countItem(Items.ENDER_PEARL), getX() + 6, iy, ParchmentTheme.TEXT_DARK, false);
        }
    }
}
