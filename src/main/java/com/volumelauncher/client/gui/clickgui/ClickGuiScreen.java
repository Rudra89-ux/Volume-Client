package com.volumelauncher.client.gui.clickgui;

import com.google.gson.JsonObject;
import com.volumelauncher.client.config.ConfigManager;
import com.volumelauncher.client.env.EnvironmentManager;
import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.Category;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final Screen parent;
    private final List<CategoryPanel> panels = new ArrayList<>();
    private String searchQuery = "";
    private boolean searchFocused = false;

    public ClickGuiScreen(Screen parent) {
        super(Component.literal("Volume Client Click GUI"));
        this.parent = parent;
        initializePanels();
    }

    public ClickGuiScreen() {
        this(null);
    }

    private void initializePanels() {
        panels.clear();
        int startX = 30;
        int startY = 40;
        int panelSpacing = 135;

        Category[] categories = Category.values();
        JsonObject savedGui = ConfigManager.getInstance().getGuiState();

        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            int px = startX + i * panelSpacing;
            int py = startY;

            if (savedGui.has(cat.name())) {
                try {
                    JsonObject pObj = savedGui.getAsJsonObject(cat.name());
                    if (pObj.has("x")) px = pObj.get("x").getAsInt();
                    if (pObj.has("y")) py = pObj.get("y").getAsInt();
                } catch (Exception ignored) {}
            }

            CategoryPanel panel = new CategoryPanel(cat, px, py);
            if (savedGui.has(cat.name()) && savedGui.getAsJsonObject(cat.name()).has("collapsed")) {
                panel.setCollapsed(savedGui.getAsJsonObject(cat.name()).get("collapsed").getAsBoolean());
            }
            panels.add(panel);
        }
    }

    public void resetPositions() {
        int startX = 30;
        int startY = 40;
        int panelSpacing = 135;
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setX(startX + i * panelSpacing);
            panels.get(i).setY(startY);
            panels.get(i).setCollapsed(false);
        }
        saveGuiState();
    }

    private void saveGuiState() {
        JsonObject guiState = new JsonObject();
        for (CategoryPanel p : panels) {
            JsonObject pObj = new JsonObject();
            pObj.addProperty("x", p.getX());
            pObj.addProperty("y", p.getY());
            pObj.addProperty("collapsed", p.isCollapsed());
            guiState.add(p.getCategory().name(), pObj);
        }
        ConfigManager.getInstance().setGuiState(guiState);
        ConfigManager.getInstance().save();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Does not pause singleplayer game
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        // Ambient background tint
        extractor.fill(0, 0, this.width, this.height, 0x55000000);

        // --- Top Bar ---
        int topBarH = 26;
        extractor.fill(0, 0, this.width, topBarH, ParchmentTheme.PARCHMENT_HEADER);
        extractor.fill(0, topBarH - 2, this.width, topBarH, ParchmentTheme.GOLD_ACCENT);

        // Branding
        extractor.text(font, "§6VOLUME CLIENT §8| §0Click GUI", 12, 8, ParchmentTheme.TEXT_DARK, false);

        // Environment Mode Notice
        EnvironmentManager.Mode mode = EnvironmentManager.getInstance().getCurrentMode();
        String modeStr = "Mode: " + mode.getDisplayName();
        int modeCol = mode == EnvironmentManager.Mode.EXPERIMENTAL ? 0xFF8A3B30 : ParchmentTheme.STATE_ON;
        extractor.text(font, modeStr, 175, 8, modeCol, false);

        // Search Box
        int searchW = 160;
        int searchX = this.width - searchW - 130;
        int searchY = 4;
        int searchH = 18;
        extractor.fill(searchX, searchY, searchX + searchW, searchY + searchH, searchFocused ? 0xFFFFFFFF : 0xFFE2D6C0);
        extractor.outline(searchX, searchY, searchW, searchH, searchFocused ? ParchmentTheme.GOLD_ACCENT : ParchmentTheme.WOOD_BORDER);

        String searchDisplay = searchQuery.isEmpty() ? (searchFocused ? "" : "§8Search modules...") : searchQuery;
        extractor.text(font, searchDisplay, searchX + 6, searchY + 5, ParchmentTheme.TEXT_DARK, false);

        // Edit HUD Button
        int editHudW = 80;
        int editHudX = this.width - editHudW - 108;
        int editHudY = 4;
        int editHudH = 18;
        boolean editHudHovered = mouseX >= editHudX && mouseX <= editHudX + editHudW && mouseY >= editHudY && mouseY <= editHudY + editHudH;
        ParchmentTheme.drawParchmentButton(extractor, editHudX, editHudY, editHudW, editHudH, "Edit HUD", editHudHovered, font);

        // Reset Layout Button
        int resetW = 90;
        int resetX = this.width - resetW - 12;
        int resetY = 4;
        int resetH = 18;
        boolean resetHovered = mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + resetH;
        ParchmentTheme.drawParchmentButton(extractor, resetX, resetY, resetW, resetH, "Reset Layout", resetHovered, font);

        // --- Render Panels ---
        List<String> tooltip = new ArrayList<>();
        for (CategoryPanel panel : panels) {
            panel.render(extractor, mouseX, mouseY, font, searchQuery, tooltip);
        }

        // --- Render Tooltip ---
        if (!tooltip.isEmpty()) {
            int tipW = 0;
            for (String line : tooltip) {
                tipW = Math.max(tipW, font.width(line));
            }
            tipW += 16;
            int tipH = tooltip.size() * 11 + 10;
            int tipX = Math.min(mouseX + 12, this.width - tipW - 4);
            int tipY = Math.min(mouseY + 12, this.height - tipH - 4);

            ParchmentTheme.drawParchmentPanel(extractor, tipX, tipY, tipW, tipH);
            int lineY = tipY + 6;
            for (String line : tooltip) {
                extractor.text(font, line, tipX + 8, lineY, ParchmentTheme.TEXT_DARK, false);
                lineY += 11;
            }
        }

        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        int mouseX = (int) Math.round(event.x());
        int mouseY = (int) Math.round(event.y());

        // Top bar buttons
        if (event.button() == 1 || event.button() == 0) {
            int editHudW = 80;
            int editHudX = this.width - editHudW - 108;
            if (mouseX >= editHudX && mouseX <= editHudX + editHudW && mouseY >= 4 && mouseY <= 22) {
                this.minecraft.setScreenAndShow(new HudEditorScreen(this));
                return true;
            }

            int resetW = 90;
            int resetX = this.width - resetW - 12;
            if (mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= 4 && mouseY <= 22) {
                resetPositions();
                return true;
            }

            int searchW = 160;
            int searchX = this.width - searchW - 130;
            if (mouseX >= searchX && mouseX <= searchX + searchW && mouseY >= 4 && mouseY <= 22) {
                searchFocused = true;
                return true;
            } else {
                searchFocused = false;
            }
        }

        // Dispatch to panels in reverse order (topmost panel first)
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).mouseClicked(event, isDoubleClick)) {
                return true;
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        for (CategoryPanel p : panels) {
            p.mouseReleased(event);
        }
        saveGuiState();
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // ESC closes GUI
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            if (searchFocused) {
                searchFocused = false;
                searchQuery = "";
                return true;
            }
            saveGuiState();
            this.minecraft.setScreenAndShow(parent);
            return true;
        }

        // Search text editing
        if (searchFocused) {
            if (event.key() == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_ENTER) {
                searchFocused = false;
                return true;
            }
        }

        // Dispatch key to panels for keybinding
        for (CategoryPanel p : panels) {
            if (p.keyPressed(event)) {
                saveGuiState();
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (searchFocused && event.isAllowedChatCharacter()) {
            searchQuery += event.codepointAsString();
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void removed() {
        saveGuiState();
        super.removed();
    }
}
