package com.volumelauncher.client;

import com.volumelauncher.client.config.ConfigManager;
import com.volumelauncher.client.env.EnvironmentManager;
import com.volumelauncher.client.keybind.KeybindManager;
import com.volumelauncher.client.module.ModuleManager;
import com.volumelauncher.client.module.combat.*;
import com.volumelauncher.client.module.hud.*;
import com.volumelauncher.client.module.player.FastPlaceModule;
import com.volumelauncher.client.module.render.EspModule;
import com.volumelauncher.client.module.utility.*;
import net.fabricmc.api.ClientModInitializer;

public class VolumeClient implements ClientModInitializer {
    public static final String MOD_ID = "volume-client";
    public static final String CLIENT_NAME = "Volume Client";
    public static final String VERSION = "1.0.0";

    private static VolumeClient instance;

    public static VolumeClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        System.out.println("==================================================");
        System.out.println("  VOLUME CLIENT v" + VERSION + " (Minecraft 26.3 / Fabric)");
        System.out.println("  Earthy Parchment Visual Theme Active");
        System.out.println("==================================================");

        // 1. Initialize environment detection
        EnvironmentManager env = EnvironmentManager.getInstance();
        System.out.println("[Volume Client] Initial environment mode: " + env.getCurrentMode().getDisplayName());

        // 2. Initialize Keybind manager
        KeybindManager keybinds = KeybindManager.getInstance();

        // 3. Register All Modules
        ModuleManager mm = ModuleManager.getInstance();

        // Combat
        mm.registerModule(new KillAuraModule());
        mm.registerModule(new AimAssistModule());
        mm.registerModule(new AutoClickerModule());
        mm.registerModule(new CriticalsModule());
        mm.registerModule(new ReachModule());
        mm.registerModule(new TriggerBotModule());
        mm.registerModule(new HitboxModule());
        mm.registerModule(new AutoTotemModule());

        // Render
        mm.registerModule(new EspModule());

        // Player / World
        mm.registerModule(new FastPlaceModule());

        // HUD Elements
        mm.registerModule(new FpsHudModule());
        mm.registerModule(new CpsHudModule());
        mm.registerModule(new PingHudModule());
        mm.registerModule(new CoordinatesHudModule());
        mm.registerModule(new KeystrokesHudModule());
        mm.registerModule(new ArmorStatusHudModule());
        mm.registerModule(new PotionEffectsHudModule());
        mm.registerModule(new ActiveModulesHudModule());
        mm.registerModule(new TargetHudModule());

        // Feather-Inspired Utilities
        mm.registerModule(new ZoomModule());
        mm.registerModule(new ToggleSprintModule());
        mm.registerModule(new FreelookModule());
        mm.registerModule(new CustomCrosshairModule());
        mm.registerModule(new ItemCounterModule());
        mm.registerModule(new TimeDisplayModule());
        mm.registerModule(new DirectionDisplayModule());
        mm.registerModule(new FullbrightModule());

        System.out.println("[Volume Client] Registered " + mm.getModules().size() + " modules across 5 categories.");

        // 4. Load persistent configuration
        ConfigManager.getInstance().load();

        System.out.println("[Volume Client] Core client successfully initialized.");
    }
}
