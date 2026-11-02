package com.volumelauncher.client.gui.clickgui;

import com.volumelauncher.client.config.ConfigManager;
import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.module.ModuleManager;
import com.volumelauncher.client.module.hud.HudModule;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class HudEditorScreen extends Screen {
    private final Screen parent;
    private HudModule draggingModule = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("HUD Customizer"));
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        // Draw dark translucent blueprint grid
        extractor.fill(0, 0, this.width, this.height, 0x66000000);

        // Grid lines every 40px
        for (int gx = 0; gx < this.width; gx += 40) {
            extractor.fill(gx, 0, gx + 1, this.height, 0x1AFFFFFF);
        }
        for (int gy = 0; gy < this.height; gy += 40) {
            extractor.fill(0, gy, this.width, gy + 1, 0x1AFFFFFF);
        }

        // Top info bar
        int bannerH = 26;
        extractor.fill(0, 0, this.width, bannerH, ParchmentTheme.PARCHMENT_HEADER);
        extractor.fill(0, bannerH - 2, this.width, bannerH, ParchmentTheme.GOLD_ACCENT);

        String info = "§6HUD CUSTOMIZER §8| §0Drag elements to reposition • Right-Click to toggle on/off • ESC to save";
        extractor.text(font, info, 14, 8, ParchmentTheme.TEXT_DARK, false);

        int exitW = 90;
        int exitX = this.width - exitW - 12;
        boolean exitHovered = mouseX >= exitX && mouseX <= exitX + exitW && mouseY >= 4 && mouseY <= 22;
        ParchmentTheme.drawParchmentButton(extractor, exitX, 4, exitW, 18, "Exit & Save", exitHovered, font);

        // Handle dragging
        if (draggingModule != null) {
            int newX = Math.max(0, Math.min(this.width - draggingModule.getWidth(font), mouseX - dragOffsetX));
            int newY = Math.max(bannerH + 4, Math.min(this.height - draggingModule.getHeight(font), mouseY - dragOffsetY));
            draggingModule.setX(newX);
            draggingModule.setY(newY);
        }

        // Render all HUD modules with editor bounds
        List<Module> hudModules = ModuleManager.getInstance().getModulesByCategory(Category.HUD);
        for (Module m : hudModules) {
            if (m instanceof HudModule) {
                HudModule hm = (HudModule) m;
                int hx = hm.getX();
                int hy = hm.getY();
                int hw = hm.getWidth(font);
                int hh = hm.getHeight(font);

                boolean hovered = mouseX >= hx && mouseX <= hx + hw && mouseY >= hy && mouseY <= hy + hh;

                // Render element preview
                hm.onRenderHud(extractor, partialTicks);

                // Render editor outline
                int borderCol = hm.isEnabled() ? (hovered ? ParchmentTheme.GOLD_HOVER : ParchmentTheme.STATE_ON) : 0x808B3A3A;
                extractor.outline(hx - 2, hy - 2, hw + 4, hh + 4, borderCol);

                // Title label pill
                String status = hm.isEnabled() ? " §a[ON]" : " §c[OFF]";
                String label = hm.getName() + status;
                extractor.fill(hx, hy - 10, hx + font.width(label) + 6, hy, 0xCC2A1B12);
                extractor.text(font, label, hx + 3, hy - 9, 0xFFFFFFFF, false);
            }
        }

        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        int mouseX = (int) Math.round(event.x());
        int mouseY = (int) Math.round(event.y());

        boolean isLeft = event.button() == 1 || event.button() == 0;
        boolean isRight = event.button() == 3;

        // Exit button
        int exitW = 90;
        int exitX = this.width - exitW - 12;
        if (isLeft && mouseX >= exitX && mouseX <= exitX + exitW && mouseY >= 4 && mouseY <= 22) {
            closeEditor();
            return true;
        }

        // Check click on HUD modules
        List<Module> hudModules = ModuleManager.getInstance().getModulesByCategory(Category.HUD);
        for (int i = hudModules.size() - 1; i >= 0; i--) {
            Module m = hudModules.get(i);
            if (m instanceof HudModule) {
                HudModule hm = (HudModule) m;
                int hx = hm.getX();
                int hy = hm.getY();
                int hw = hm.getWidth(font);
                int hh = hm.getHeight(font);

                if (mouseX >= hx && mouseX <= hx + hw && mouseY >= hy && mouseY <= hy + hh) {
                    if (isLeft) { // Left-click drag
                        draggingModule = hm;
                        dragOffsetX = mouseX - hx;
                        dragOffsetY = mouseY - hy;
                        return true;
                    } else if (isRight) { // Right-click toggle
                        hm.toggle();
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingModule = null;
        ConfigManager.getInstance().save();
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            closeEditor();
            return true;
        }
        return super.keyPressed(event);
    }

    private void closeEditor() {
        ConfigManager.getInstance().save();
        this.minecraft.setScreenAndShow(parent);
    }
}
