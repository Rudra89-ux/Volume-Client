package com.volumelauncher.client.module;

import com.volumelauncher.client.env.EnvironmentManager;
import com.volumelauncher.client.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {
    protected final Minecraft mc = Minecraft.getInstance();
    private final String id;
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keybind; // GLFW key code, 0 = unbound
    private final boolean experimental;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String id, String name, String description, Category category, int defaultKeybind, boolean experimental) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = defaultKeybind;
        this.experimental = experimental;
        this.enabled = false;
    }

    public Module(String id, String name, String description, Category category, int defaultKeybind) {
        this(id, name, description, category, defaultKeybind, false);
    }

    public Module(String id, String name, String description, Category category) {
        this(id, name, description, category, 0, false);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    protected void addSetting(Setting<?> setting) {
        if (setting != null) {
            settings.add(setting);
        }
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        // Restrict experimental modules
        if (enabled && experimental && !EnvironmentManager.getInstance().isExperimentalAllowed()) {
            if (mc.gui != null && mc.gui.hud != null && mc.gui.hud.getChat() != null) {
                // Send safe client-side chat notice
                mc.gui.hud.getChat().addClientSystemMessage(
                    net.minecraft.network.chat.Component.literal("§c[Volume Client] " + name + " is restricted to Singleplayer or private test environments.")
                );
            }
            return;
        }

        this.enabled = enabled;
        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRenderHud(GuiGraphicsExtractor extractor, float partialTicks) {}
    public void onKey(int keyCode, int scanCode, int action) {}
}
