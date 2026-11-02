package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.ColorSetting;
import com.volumelauncher.client.setting.ModeSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CustomCrosshairModule extends Module {
    private final ModeSetting style = new ModeSetting("style", "Style", "Crosshair shape", "Parchment Cross", "Parchment Cross", "Dot", "Box");
    private final NumberSetting size = new NumberSetting("size", "Size", "Crosshair radius", 4.0, 2.0, 10.0, 1.0);
    private final ColorSetting color = new ColorSetting("color", "Color", "Crosshair tint color", ParchmentTheme.GOLD_ACCENT);

    public CustomCrosshairModule() {
        super("customcrosshair", "Custom Crosshair", "Replaces vanilla crosshair with a stylized parchment crosshair.", Category.UTILITY, 0, false);
        addSetting(style);
        addSetting(size);
        addSetting(color);
    }

    @Override
    public void onRenderHud(GuiGraphicsExtractor extractor, float partialTicks) {
        if (!isEnabled() || mc.getWindow() == null) return;

        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        int s = size.getValueInt();
        int col = color.getValue();

        if (style.is("Parchment Cross")) {
            // Horizontal bar
            extractor.fill(cx - s, cy, cx + s + 1, cy + 1, col);
            // Vertical bar
            extractor.fill(cx, cy - s, cx + 1, cy + s + 1, col);
            // Outline border
            extractor.outline(cx - s - 1, cy - 1, s * 2 + 3, 3, ParchmentTheme.WOOD_DARK);
            extractor.outline(cx - 1, cy - s - 1, 3, s * 2 + 3, ParchmentTheme.WOOD_DARK);

        } else if (style.is("Dot")) {
            extractor.fill(cx - 1, cy - 1, cx + 2, cy + 2, col);
            extractor.outline(cx - 2, cy - 2, 5, 5, ParchmentTheme.WOOD_DARK);

        } else if (style.is("Box")) {
            extractor.outline(cx - s, cy - s, s * 2 + 1, s * 2 + 1, col);
            extractor.outline(cx - s - 1, cy - s - 1, s * 2 + 3, s * 2 + 3, ParchmentTheme.WOOD_DARK);
        }
    }
}
