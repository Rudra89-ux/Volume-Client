package com.volumelauncher.client.gui.menu;

import com.mojang.realmsclient.RealmsMainScreen;
import com.volumelauncher.client.VolumeClient;
import com.volumelauncher.client.env.EnvironmentManager;
import com.volumelauncher.client.gui.clickgui.ClickGuiScreen;
import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public class VolumeMainMenuScreen extends Screen {

    public VolumeMainMenuScreen() {
        super(Component.literal("Volume Client Main Menu"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4 + 48;
        int spacing = 24;

        // 1. Singleplayer -> Open the vanilla world selection screen
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY, 180, 20,
            Component.literal("Singleplayer"),
            btn -> {
                System.out.println("[VolumeButton] Singleplayer clicked!");
                try {
                    this.minecraft.setScreenAndShow(new SelectWorldScreen(this));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening SelectWorldScreen:");
                    t.printStackTrace();
                }
            }
        ));

        // 2. Multiplayer -> Open the vanilla multiplayer server screen
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY + spacing, 180, 20,
            Component.literal("Multiplayer"),
            btn -> {
                System.out.println("[VolumeButton] Multiplayer clicked!");
                try {
                    this.minecraft.setScreenAndShow(new JoinMultiplayerScreen(this));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening JoinMultiplayerScreen:");
                    t.printStackTrace();
                }
            }
        ));

        // 3. Realms -> Open the Realms screen
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY + spacing * 2, 180, 20,
            Component.literal("Minecraft Realms"),
            btn -> {
                System.out.println("[VolumeButton] Realms clicked!");
                try {
                    this.minecraft.setScreenAndShow(new RealmsMainScreen(this));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening RealmsMainScreen:");
                    t.printStackTrace();
                }
            }
        ));

        // 4. Split row: Options & Profile/Accounts
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY + spacing * 3, 88, 20,
            Component.literal("Options"),
            btn -> {
                System.out.println("[VolumeButton] Options clicked!");
                try {
                    this.minecraft.setScreenAndShow(new OptionsScreen(this, this.minecraft.options));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening OptionsScreen:");
                    t.printStackTrace();
                }
            }
        ));

        this.addRenderableWidget(new ParchmentButton(
            centerX + 2, startY + spacing * 3, 88, 20,
            Component.literal("Profile & Skin"),
            btn -> {
                System.out.println("[VolumeButton] Profile & Skin clicked!");
                try {
                    this.minecraft.setScreenAndShow(new SkinCustomizationScreen(this, this.minecraft.options));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening SkinCustomizationScreen:");
                    t.printStackTrace();
                }
            }
        ));

        // 5. Volume Client Settings -> Open Volume Client settings
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY + spacing * 4, 180, 20,
            Component.literal("Volume Client Settings"),
            btn -> {
                System.out.println("[VolumeButton] Settings clicked!");
                try {
                    this.minecraft.setScreenAndShow(new ClickGuiScreen(this));
                    System.out.println("[VolumeButton] Screen is now: " + (this.minecraft.gui.screen() != null ? this.minecraft.gui.screen().getClass().getSimpleName() : "null"));
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error opening ClickGuiScreen:");
                    t.printStackTrace();
                }
            }
        ));

        // 6. Quit Game -> Exit Minecraft correctly
        this.addRenderableWidget(new ParchmentButton(
            centerX - 90, startY + spacing * 5, 180, 20,
            Component.literal("Quit Game"),
            btn -> {
                System.out.println("[VolumeButton] Quit Game clicked!");
                try {
                    this.minecraft.stop();
                } catch (Throwable t) {
                    System.err.println("[VolumeButton] Error calling minecraft.stop():");
                    t.printStackTrace();
                }
            }
        ));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        // Render rotating Minecraft panorama
        this.extractPanorama(extractor, partialTicks);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        // Draw dark ambient overlay over panorama
        extractor.fill(0, 0, this.width, this.height, 0x4D000000);

        int centerX = this.width / 2;

        // --- Parchment Header Plaque ---
        int plaqueW = 240;
        int plaqueH = 68;
        int plaqueX = centerX - plaqueW / 2;
        int plaqueY = this.height / 4 - 32;

        ParchmentTheme.drawParchmentPanel(extractor, plaqueX, plaqueY, plaqueW, plaqueH);

        // Gold title banner
        String title = "VOLUME CLIENT";
        int titleW = font.width(title);
        extractor.text(font, title, centerX - titleW / 2, plaqueY + 12, ParchmentTheme.GOLD_MUTED, false);

        String subtitle = "Minecraft 26.3 • Fabric Edition";
        int subW = font.width(subtitle);
        extractor.text(font, subtitle, centerX - subW / 2, plaqueY + 28, ParchmentTheme.TEXT_MUTED, false);

        // Environment status badge
        EnvironmentManager.Mode mode = EnvironmentManager.getInstance().getCurrentMode();
        String modeText = "Environment: " + mode.getDisplayName();
        int modeColor = mode == EnvironmentManager.Mode.EXPERIMENTAL ? 0xFF8A3B30 : ParchmentTheme.STATE_ON;
        int modeW = font.width(modeText);
        extractor.text(font, modeText, centerX - modeW / 2, plaqueY + 44, modeColor, false);

        // Render buttons and any other registered widgets
        super.extractRenderState(extractor, mouseX, mouseY, partialTicks);

        // --- Footer Info ---
        String versionStr = "Volume Client v" + VolumeClient.VERSION + " | Earthy Parchment Theme";
        extractor.text(font, versionStr, 8, this.height - 16, 0xCCFFFFFF, false);

        String brandingStr = "Designed for Volume Launcher";
        int brandW = font.width(brandingStr);
        extractor.text(font, brandingStr, this.width - brandW - 8, this.height - 16, 0xCCFFFFFF, false);
    }
}
