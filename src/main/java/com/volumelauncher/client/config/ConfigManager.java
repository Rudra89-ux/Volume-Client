package com.volumelauncher.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.module.ModuleManager;
import com.volumelauncher.client.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configDir;
    private final Path profilesDir;
    private String activeProfile = "default";
    private JsonObject guiState = new JsonObject();
    private JsonObject hudState = new JsonObject();

    private ConfigManager() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        configDir = gameDir.resolve(".volume").resolve("client");
        profilesDir = configDir.resolve("profiles");
        try {
            new File(configDir.toUri()).mkdirs();
            new File(profilesDir.toUri()).mkdirs();
        } catch (Exception ignored) {}
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public JsonObject getGuiState() {
        return guiState;
    }

    public void setGuiState(JsonObject guiState) {
        this.guiState = guiState;
    }

    public JsonObject getHudState() {
        return hudState;
    }

    public void setHudState(JsonObject hudState) {
        this.hudState = hudState;
    }

    public synchronized void save() {
        saveProfile(activeProfile);
    }

    public synchronized void load() {
        loadProfile(activeProfile);
    }

    public synchronized boolean saveProfile(String profileName) {
        if (profileName == null || profileName.trim().isEmpty()) {
            profileName = "default";
        }
        File profileFile = profilesDir.resolve(profileName + ".json").toFile();
        File activeFile = configDir.resolve("config.json").toFile();

        try {
            JsonObject root = new JsonObject();
            root.addProperty("version", "1.0.0");
            root.addProperty("profileName", profileName);

            // 1. Modules and Settings
            JsonObject modulesJson = new JsonObject();
            for (Module m : ModuleManager.getInstance().getModules()) {
                JsonObject mObj = new JsonObject();
                mObj.addProperty("enabled", m.isEnabled());
                mObj.addProperty("keybind", m.getKeybind());

                JsonObject settingsObj = new JsonObject();
                for (Setting<?> s : m.getSettings()) {
                    settingsObj.addProperty(s.getId(), s.getSerializedValue());
                }
                mObj.add("settings", settingsObj);
                modulesJson.add(m.getId(), mObj);
            }
            root.add("modules", modulesJson);

            // 2. GUI and HUD Layouts
            root.add("gui", guiState);
            root.add("hud", hudState);

            String jsonString = GSON.toJson(root);

            try (FileWriter writer = new FileWriter(profileFile)) {
                writer.write(jsonString);
            }
            try (FileWriter writer = new FileWriter(activeFile)) {
                writer.write(jsonString);
            }

            this.activeProfile = profileName;
            return true;
        } catch (Exception e) {
            System.err.println("[Volume Client] Failed to save profile " + profileName + ": " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean loadProfile(String profileName) {
        if (profileName == null || profileName.trim().isEmpty()) {
            profileName = "default";
        }
        File profileFile = profilesDir.resolve(profileName + ".json").toFile();
        if (!profileFile.exists()) {
            profileFile = configDir.resolve("config.json").toFile();
        }

        if (!profileFile.exists()) {
            return false;
        }

        try (FileReader reader = new FileReader(profileFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (root.has("modules")) {
                JsonObject modulesJson = root.getAsJsonObject("modules");
                for (Module m : ModuleManager.getInstance().getModules()) {
                    if (modulesJson.has(m.getId())) {
                        JsonObject mObj = modulesJson.getAsJsonObject(m.getId());
                        if (mObj.has("keybind")) {
                            m.setKeybind(mObj.get("keybind").getAsInt());
                        }

                        if (mObj.has("settings")) {
                            JsonObject sObj = mObj.getAsJsonObject("settings");
                            for (Setting<?> s : m.getSettings()) {
                                if (sObj.has(s.getId())) {
                                    s.deserializeValue(sObj.get(s.getId()).getAsString());
                                }
                            }
                        }

                        if (mObj.has("enabled")) {
                            boolean shouldEnable = mObj.get("enabled").getAsBoolean();
                            if (shouldEnable != m.isEnabled()) {
                                m.setEnabled(shouldEnable);
                            }
                        }
                    }
                }
            }

            if (root.has("gui")) {
                this.guiState = root.getAsJsonObject("gui");
            }
            if (root.has("hud")) {
                this.hudState = root.getAsJsonObject("hud");
            }

            this.activeProfile = profileName;
            return true;
        } catch (Exception e) {
            System.err.println("[Volume Client] Corrupted or invalid profile " + profileName + ", using defaults: " + e.getMessage());
            return false;
        }
    }

    public List<String> listProfiles() {
        List<String> list = new ArrayList<>();
        File[] files = profilesDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                list.add(name.substring(0, name.length() - 5));
            }
        }
        if (!list.contains("default")) {
            list.add("default");
        }
        return list;
    }

    public synchronized boolean deleteProfile(String profileName) {
        if ("default".equalsIgnoreCase(profileName)) {
            return false; // Cannot delete default profile
        }
        File f = profilesDir.resolve(profileName + ".json").toFile();
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }

    public synchronized boolean exportProfile(String profileName, File destination) {
        if (destination == null) return false;
        File source = profilesDir.resolve(profileName + ".json").toFile();
        if (!source.exists()) {
            source = configDir.resolve("config.json").toFile();
        }
        if (!source.exists()) return false;

        try (FileReader fr = new FileReader(source); FileWriter fw = new FileWriter(destination)) {
            char[] buf = new char[4096];
            int read;
            while ((read = fr.read(buf)) != -1) {
                fw.write(buf, 0, read);
            }
            return true;
        } catch (Exception e) {
            System.err.println("[Volume Client] Failed to export profile: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean importProfile(File source, String asProfileName) {
        if (source == null || !source.exists() || asProfileName == null || asProfileName.trim().isEmpty()) {
            return false;
        }

        // Validate JSON structure before saving
        try (FileReader fr = new FileReader(source)) {
            JsonObject root = JsonParser.parseReader(fr).getAsJsonObject();
            if (!root.has("modules")) {
                System.err.println("[Volume Client] Invalid import file: missing modules block");
                return false;
            }

            File target = profilesDir.resolve(asProfileName.trim() + ".json").toFile();
            try (FileWriter fw = new FileWriter(target)) {
                fw.write(GSON.toJson(root));
            }
            loadProfile(asProfileName.trim());
            return true;
        } catch (Exception e) {
            System.err.println("[Volume Client] Failed to import profile: " + e.getMessage());
            return false;
        }
    }
}
