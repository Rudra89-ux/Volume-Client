package com.volumelauncher.client.gui.clickgui;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.keybind.KeybindManager;
import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.module.ModuleManager;
import com.volumelauncher.client.setting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel {
    private final Category category;
    private int x;
    private int y;
    private int width = 120;
    private int headerHeight = 18;
    private boolean collapsed = false;

    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private Module listeningModule = null;
    private final List<String> expandedModules = new ArrayList<>();
    private Setting<?> activeSlider = null;

    public CategoryPanel(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
    }

    public Category getCategory() {
        return category;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public void render(GuiGraphicsExtractor extractor, int mouseX, int mouseY, Font font, String filter, List<String> tooltipSink) {
        // Handle dragging
        if (dragging) {
            this.x = mouseX - dragOffsetX;
            this.y = mouseY - dragOffsetY;
        }

        // Draw header bar
        ParchmentTheme.drawHeader(extractor, x, y, width, headerHeight, category.getDisplayName(), font);

        // Collapse indicator icon [+]/[-]
        String collapseIcon = collapsed ? "+" : "-";
        int iconW = font.width(collapseIcon);
        extractor.text(font, collapseIcon, x + width - iconW - 6, y + 5, ParchmentTheme.TEXT_MUTED, false);

        if (collapsed) {
            return;
        }

        int currentY = y + headerHeight;
        List<Module> modules = ModuleManager.getInstance().getModulesByCategory(category);

        for (Module m : modules) {
            // Filter match
            if (filter != null && !filter.isEmpty()) {
                if (!m.getName().toLowerCase().contains(filter.toLowerCase()) &&
                    !m.getDescription().toLowerCase().contains(filter.toLowerCase())) {
                    continue;
                }
            }

            int moduleHeight = 18;
            boolean isHovered = mouseX >= x && mouseX <= x + width && mouseY >= currentY && mouseY < currentY + moduleHeight;

            // Background fill
            int bgCol = m.isEnabled() ? 0xFFDFD1B8 : (isHovered ? 0xFFE8DCC4 : ParchmentTheme.PARCHMENT_BG);
            extractor.fill(x - 1, currentY, x + width + 1, currentY + moduleHeight, ParchmentTheme.WOOD_BORDER);
            extractor.fill(x, currentY, x + width, currentY + moduleHeight, bgCol);

            // Active indicator pill on left edge
            int indicatorCol = m.isEnabled() ? (m.isExperimental() ? 0xFF9E4738 : ParchmentTheme.STATE_ON) : 0x405C4736;
            extractor.fill(x + 2, currentY + 3, x + 5, currentY + moduleHeight - 3, indicatorCol);

            // Module Name
            int textCol = m.isEnabled() ? ParchmentTheme.TEXT_DARK : ParchmentTheme.TEXT_MUTED;
            String displayName = m.getName();
            if (m == listeningModule) {
                displayName = "[Press Key...]";
                textCol = ParchmentTheme.GOLD_HOVER;
            }
            extractor.text(font, displayName, x + 8, currentY + 5, textCol, false);

            // Keybind pill on right edge if bound
            if (m.getKeybind() > 0 && m != listeningModule) {
                String keyStr = KeybindManager.getKeyName(m.getKeybind());
                int kw = font.width(keyStr);
                extractor.fill(x + width - kw - 14, currentY + 3, x + width - 10, currentY + moduleHeight - 3, 0x30000000);
                extractor.text(font, keyStr, x + width - kw - 12, currentY + 5, ParchmentTheme.TEXT_MUTED, false);
            }

            // Expand settings arrow indicator if module has settings
            if (!m.getSettings().isEmpty()) {
                boolean isExpanded = expandedModules.contains(m.getId());
                String expandArrow = isExpanded ? "▼" : "▶";
                int arrowW = font.width(expandArrow);
                extractor.text(font, expandArrow, x + width - arrowW - 3, currentY + 5, ParchmentTheme.TEXT_MUTED, false);
            }

            // Collect hover tooltip
            if (isHovered && tooltipSink != null) {
                String expNotice = m.isExperimental() ? " §c[Experimental - Singleplayer Only]" : "";
                tooltipSink.add("§6" + m.getName() + expNotice);
                tooltipSink.add("§7" + m.getDescription());
                tooltipSink.add("§8Left-Click: Toggle | Right-Click: Settings | Middle-Click: Bind");
            }

            currentY += moduleHeight;

            // Render expanded settings controls
            if (expandedModules.contains(m.getId())) {
                for (Setting<?> s : m.getSettings()) {
                    int settingH = 16;
                    renderSetting(extractor, s, x, currentY, width, settingH, mouseX, mouseY, font);
                    currentY += settingH;
                }
            }
        }

        // Bottom border line
        extractor.fill(x - 1, currentY, x + width + 1, currentY + 1, ParchmentTheme.WOOD_BORDER);
    }

    private void renderSetting(GuiGraphicsExtractor extractor, Setting<?> s, int sx, int sy, int sw, int sh, int mouseX, int mouseY, Font font) {
        extractor.fill(sx - 1, sy, sx + sw + 1, sy + sh, ParchmentTheme.WOOD_BORDER);
        extractor.fill(sx, sy, sx + sw, sy + sh, 0xFFD2C0A2); // Darker parchment inset

        if (s instanceof BooleanSetting) {
            BooleanSetting bs = (BooleanSetting) s;
            extractor.text(font, bs.getName(), sx + 12, sy + 4, ParchmentTheme.TEXT_DARK, false);

            int switchW = 12;
            int switchH = 8;
            int switchX = sx + sw - switchW - 6;
            int switchY = sy + 4;
            int switchCol = bs.getValue() ? ParchmentTheme.STATE_ON : 0x805C4736;
            extractor.fill(switchX, switchY, switchX + switchW, switchY + switchH, switchCol);
            extractor.outline(switchX, switchY, switchW, switchH, ParchmentTheme.WOOD_DARK);

        } else if (s instanceof NumberSetting) {
            NumberSetting ns = (NumberSetting) s;
            double progress = (ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
            int barW = sw - 20;
            int barFill = (int) Math.round(barW * progress);

            // Slider track
            extractor.fill(sx + 10, sy + 11, sx + 10 + barW, sy + 14, 0x40000000);
            extractor.fill(sx + 10, sy + 11, sx + 10 + barFill, sy + 14, ParchmentTheme.GOLD_ACCENT);

            String label = ns.getName() + ": " + String.format("%.1f", ns.getValue());
            extractor.text(font, label, sx + 10, sy + 2, ParchmentTheme.TEXT_DARK, false);

            if (activeSlider == ns && mouseX >= sx + 10 && mouseX <= sx + 10 + barW) {
                double pct = Math.max(0.0, Math.min(1.0, (double) (mouseX - (sx + 10)) / barW));
                double newVal = ns.getMin() + pct * (ns.getMax() - ns.getMin());
                ns.setValue(newVal);
            }

        } else if (s instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting) s;
            String text = ms.getName() + ": §6" + ms.getValue();
            extractor.text(font, text, sx + 10, sy + 4, ParchmentTheme.TEXT_DARK, false);

        } else if (s instanceof ColorSetting) {
            ColorSetting cs = (ColorSetting) s;
            extractor.text(font, cs.getName(), sx + 10, sy + 4, ParchmentTheme.TEXT_DARK, false);
            int colorX = sx + sw - 20;
            int colorY = sy + 3;
            extractor.fill(colorX, colorY, colorX + 14, colorY + 10, cs.getValue());
            extractor.outline(colorX, colorY, 14, 10, ParchmentTheme.WOOD_DARK);
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        int mouseX = (int) Math.round(event.x());
        int mouseY = (int) Math.round(event.y());

        boolean isLeft = event.button() == 1 || event.button() == 0;
        boolean isRight = event.button() == 3;
        boolean isMiddle = event.button() == 2;

        // Header click: drag or collapse
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight) {
            if (isLeft) { // Left click header
                if (mouseX >= x + width - 16) {
                    // Clicked collapse toggle
                    collapsed = !collapsed;
                    playClick();
                } else {
                    // Start dragging panel
                    dragging = true;
                    dragOffsetX = mouseX - x;
                    dragOffsetY = mouseY - y;
                }
                return true;
            } else if (isRight) { // Right click header collapses
                collapsed = !collapsed;
                playClick();
                return true;
            }
        }

        if (collapsed) {
            return false;
        }

        int currentY = y + headerHeight;
        List<Module> modules = ModuleManager.getInstance().getModulesByCategory(category);

        for (Module m : modules) {
            int moduleHeight = 18;
            if (mouseX >= x && mouseX <= x + width && mouseY >= currentY && mouseY < currentY + moduleHeight) {
                if (isLeft) { // Left click: Toggle module
                    m.toggle();
                    playClick();
                    return true;
                } else if (isRight) { // Right click: Expand settings
                    if (!m.getSettings().isEmpty()) {
                        if (expandedModules.contains(m.getId())) {
                            expandedModules.remove(m.getId());
                        } else {
                            expandedModules.add(m.getId());
                        }
                        playClick();
                    }
                    return true;
                } else if (isMiddle) { // Middle click: Begin keybind listening
                    listeningModule = m;
                    playClick();
                    return true;
                }
            }

            currentY += moduleHeight;

            // Handle setting interactions
            if (expandedModules.contains(m.getId())) {
                for (Setting<?> s : m.getSettings()) {
                    int settingH = 16;
                    if (isLeft && mouseX >= x && mouseX <= x + width && mouseY >= currentY && mouseY < currentY + settingH) {
                        if (s instanceof BooleanSetting) {
                            ((BooleanSetting) s).toggle();
                            playClick();
                            return true;
                        } else if (s instanceof ModeSetting) {
                            ((ModeSetting) s).cycle();
                            playClick();
                            return true;
                        } else if (s instanceof NumberSetting) {
                            activeSlider = s;
                            NumberSetting ns = (NumberSetting) s;
                            int barW = width - 20;
                            double pct = Math.max(0.0, Math.min(1.0, (double) (mouseX - (x + 10)) / barW));
                            ns.setValue(ns.getMin() + pct * (ns.getMax() - ns.getMin()));
                            return true;
                        }
                    }
                    currentY += settingH;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        activeSlider = null;
        return false;
    }

    public boolean keyPressed(KeyEvent event) {
        if (listeningModule != null) {
            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                listeningModule.setKeybind(0); // Unbind
            } else {
                listeningModule.setKeybind(event.key());
            }
            listeningModule = null;
            playClick();
            return true;
        }
        return false;
    }

    private void playClick() {
        Minecraft.getInstance().getSoundManager().play(
            SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }
}
