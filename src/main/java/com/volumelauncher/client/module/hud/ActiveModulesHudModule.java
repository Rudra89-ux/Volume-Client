package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.module.ModuleManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveModulesHudModule extends HudModule {
    public ActiveModulesHudModule() {
        super("activemodules", "Active Modules", "Displays a stylized parchment list of enabled modules.", 300, 10);
    }

    private List<Module> getActiveList(Font font) {
        return ModuleManager.getInstance().getModules().stream()
            .filter(Module::isEnabled)
            .sorted(Comparator.comparingInt((Module m) -> font.width(m.getName())).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public int getWidth(Font font) {
        int maxW = 60;
        for (Module m : getActiveList(font)) {
            maxW = Math.max(maxW, font.width(m.getName()) + 16);
        }
        return maxW;
    }

    @Override
    public int getHeight(Font font) {
        int count = (int) ModuleManager.getInstance().getModules().stream().filter(Module::isEnabled).count();
        return Math.max(18, count * 13 + 6);
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        List<Module> active = getActiveList(font);
        if (active.isEmpty()) {
            extractor.text(font, "§8No modules active", getX() + 6, getY() + 5, ParchmentTheme.TEXT_MUTED, false);
            return;
        }

        int my = getY() + 4;
        for (Module m : active) {
            String name = m.getName();
            int nameW = font.width(name);

            // Left gold pill
            int color = m.isExperimental() ? 0xFF9E4738 : ParchmentTheme.GOLD_ACCENT;
            extractor.fill(getX() + 3, my + 1, getX() + 5, my + 11, color);

            extractor.text(font, name, getX() + 8, my + 2, ParchmentTheme.TEXT_DARK, false);
            my += 13;
        }
    }
}
