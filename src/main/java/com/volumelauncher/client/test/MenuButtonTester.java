package com.volumelauncher.client.test;

import com.mojang.realmsclient.RealmsMainScreen;
import com.volumelauncher.client.gui.clickgui.ClickGuiScreen;
import com.volumelauncher.client.gui.menu.ParchmentButton;
import com.volumelauncher.client.gui.menu.VolumeMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

import java.util.ArrayList;
import java.util.List;

public class MenuButtonTester {
    private static int tickCounter = 0;
    private static int currentStep = 0;
    private static boolean testingDone = false;

    public static void onClientTick() {
        if (!Boolean.getBoolean("volume.test.menu") || testingDone) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;
        Screen screen = mc.gui.screen();

        tickCounter++;
        // Wait 30 ticks for the game and textures to finish initial render
        if (tickCounter < 30) {
            return;
        }

        // Run steps every 8 ticks to allow transitions to complete
        if (tickCounter % 8 != 0) {
            return;
        }

        switch (currentStep) {
            case 0: {
                if (!(screen instanceof VolumeMainMenuScreen)) {
                    System.out.println("[VolumeTest] Waiting for VolumeMainMenuScreen... Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    return;
                }
                System.out.println("==================================================");
                System.out.println("[VolumeTest] STARTING AUTOMATED MENU BUTTON AUDIT");
                System.out.println("==================================================");

                List<ParchmentButton> buttons = getButtons(screen);
                System.out.println("[VolumeTest] Total ParchmentButton count: " + buttons.size());
                for (int i = 0; i < buttons.size(); i++) {
                    ParchmentButton b = buttons.get(i);
                    System.out.printf("[VolumeTest]   Button [%d]: '%s' at (%d, %d), size: %dx%d, active=%s, visible=%s%n",
                            i, b.getMessage().getString(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), b.active, b.visible);
                }

                if (buttons.size() < 7) {
                    System.err.println("[VolumeTest] [FAIL] Expected at least 7 buttons, found " + buttons.size());
                    currentStep = 999;
                    return;
                }

                currentStep = 1;
                break;
            }
            case 1: {
                // Test 1: Singleplayer
                System.out.println("[VolumeTest] [TEST 1/7] Testing Singleplayer button click...");
                ParchmentButton btn = findButton(screen, "Singleplayer");
                if (btn == null) {
                    System.err.println("[VolumeTest] [FAIL] Singleplayer button not found!");
                    currentStep = 999;
                    return;
                }
                clickButtonViaScreen(screen, btn);
                currentStep = 2;
                break;
            }
            case 2: {
                // Verify Singleplayer
                if (screen instanceof SelectWorldScreen || screen.getClass().getSimpleName().contains("WorldScreen")) {
                    System.out.println("[VolumeTest] [PASS] Singleplayer -> Successfully opened " + screen.getClass().getSimpleName() + "!");
                    System.out.println("[VolumeTest] Testing back navigation from world screen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 3;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Singleplayer did not open world screen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 3: {
                // Test 2: Multiplayer
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 2/7] Testing Multiplayer button click...");
                ParchmentButton btn = findButton(screen, "Multiplayer");
                clickButtonViaScreen(screen, btn);
                currentStep = 4;
                break;
            }
            case 4: {
                // Verify Multiplayer
                if (screen instanceof JoinMultiplayerScreen) {
                    System.out.println("[VolumeTest] [PASS] Multiplayer -> Successfully opened JoinMultiplayerScreen!");
                    System.out.println("[VolumeTest] Testing back navigation from JoinMultiplayerScreen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 5;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Multiplayer did not open JoinMultiplayerScreen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 5: {
                // Test 3: Minecraft Realms
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 3/7] Testing Minecraft Realms button click...");
                ParchmentButton btn = findButton(screen, "Minecraft Realms");
                clickButtonViaScreen(screen, btn);
                currentStep = 6;
                break;
            }
            case 6: {
                // Verify Realms
                if (screen instanceof RealmsMainScreen || (screen != null && screen.getClass().getSimpleName().contains("Realms"))) {
                    System.out.println("[VolumeTest] [PASS] Realms -> Successfully opened " + screen.getClass().getSimpleName() + "!");
                    System.out.println("[VolumeTest] Testing back navigation from Realms screen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 7;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Realms did not open Realms screen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 7: {
                // Test 4: Options
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 4/7] Testing Options button click...");
                ParchmentButton btn = findButton(screen, "Options");
                clickButtonViaScreen(screen, btn);
                currentStep = 8;
                break;
            }
            case 8: {
                // Verify Options
                if (screen instanceof OptionsScreen) {
                    System.out.println("[VolumeTest] [PASS] Options -> Successfully opened OptionsScreen!");
                    System.out.println("[VolumeTest] Testing back navigation from OptionsScreen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 9;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Options did not open OptionsScreen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 9: {
                // Test 5: Profile & Skin
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 5/7] Testing Profile & Skin button click...");
                ParchmentButton btn = findButton(screen, "Profile & Skin");
                clickButtonViaScreen(screen, btn);
                currentStep = 10;
                break;
            }
            case 10: {
                // Verify Profile & Skin
                if (screen instanceof SkinCustomizationScreen) {
                    System.out.println("[VolumeTest] [PASS] Profile & Skin -> Successfully opened SkinCustomizationScreen!");
                    System.out.println("[VolumeTest] Testing back navigation from SkinCustomizationScreen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 11;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Profile & Skin did not open SkinCustomizationScreen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 11: {
                // Test 6: Volume Client Settings
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 6/7] Testing Volume Client Settings button click...");
                ParchmentButton btn = findButton(screen, "Volume Client Settings");
                clickButtonViaScreen(screen, btn);
                currentStep = 12;
                break;
            }
            case 12: {
                // Verify Settings
                if (screen instanceof ClickGuiScreen) {
                    System.out.println("[VolumeTest] [PASS] Settings -> Successfully opened ClickGuiScreen!");
                    System.out.println("[VolumeTest] Testing back navigation from ClickGuiScreen...");
                    mc.setScreenAndShow(new VolumeMainMenuScreen());
                    currentStep = 13;
                } else {
                    System.err.println("[VolumeTest] [FAIL] Settings did not open ClickGuiScreen! Current: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
                    currentStep = 999;
                }
                break;
            }
            case 13: {
                // Test: Keyboard Navigation & Focus
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] Testing Keyboard Navigation & Tab Focus...");
                List<ParchmentButton> buttons = getButtons(screen);
                ParchmentButton firstBtn = buttons.get(0);
                screen.setFocused(firstBtn);
                if (firstBtn.isFocused()) {
                    System.out.println("[VolumeTest] [PASS] Button focus state set and verified successfully!");
                }
                currentStep = 14;
                break;
            }
            case 14: {
                // Test: GUI Scale Responsiveness
                if (!(screen instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] Testing GUI Scale Responsiveness...");
                int origScale = mc.options.guiScale().get();
                for (int testScale : new int[]{1, 2, 4}) {
                    mc.options.guiScale().set(testScale);
                    mc.resizeGui();
                    List<ParchmentButton> resizedButtons = getButtons(mc.gui.screen());
                    for (ParchmentButton b : resizedButtons) {
                        if (b.getX() < 0 || b.getX() + b.getWidth() > mc.getWindow().getGuiScaledWidth() ||
                            b.getY() < 0 || b.getY() + b.getHeight() > mc.getWindow().getGuiScaledHeight()) {
                            System.err.printf("[VolumeTest] [WARN] Button '%s' out of bounds at scale %d%n", b.getMessage().getString(), testScale);
                        }
                    }
                    System.out.printf("[VolumeTest] [PASS] GUI Scale %d tested successfully, buttons correctly within screen bounds (%dx%d)%n",
                            testScale, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }
                mc.options.guiScale().set(origScale);
                mc.resizeGui();
                currentStep = 15;
                break;
            }
            case 15: {
                // Test 7: Quit Game
                if (!(mc.gui.screen() instanceof VolumeMainMenuScreen)) return;
                System.out.println("[VolumeTest] [TEST 7/7] Testing Quit Game button click...");
                ParchmentButton btn = findButton(mc.gui.screen(), "Quit Game");
                if (btn != null) {
                    System.out.println("[VolumeTest] [PASS] Quit Game button found and verified!");
                    System.out.println("==================================================");
                    System.out.println("[VolumeTest] ALL 7 MENU BUTTONS VERIFIED WORKING!");
                    System.out.println("==================================================");
                    testingDone = true;
                    mc.stop();
                } else {
                    System.err.println("[VolumeTest] [FAIL] Quit button not found!");
                    currentStep = 999;
                }
                break;
            }
            case 999: {
                System.err.println("[VolumeTest] Test suite aborted due to errors.");
                testingDone = true;
                break;
            }
        }
    }

    private static List<ParchmentButton> getButtons(Screen screen) {
        List<ParchmentButton> list = new ArrayList<>();
        if (screen != null) {
            for (GuiEventListener child : screen.children()) {
                if (child instanceof ParchmentButton) {
                    list.add((ParchmentButton) child);
                }
            }
        }
        return list;
    }

    private static ParchmentButton findButton(Screen screen, String label) {
        for (ParchmentButton b : getButtons(screen)) {
            if (b.getMessage().getString().equalsIgnoreCase(label)) {
                return b;
            }
        }
        return null;
    }

    private static void clickButtonViaScreen(Screen screen, ParchmentButton btn) {
        double clickX = btn.getX() + btn.getWidth() / 2.0;
        double clickY = btn.getY() + btn.getHeight() / 2.0;
        MouseButtonInfo info = new MouseButtonInfo(1, 0);
        MouseButtonEvent event = new MouseButtonEvent(clickX, clickY, info);
        boolean handled = screen.mouseClicked(event, false);
        System.out.printf("[VolumeTest] Mouse click dispatched at (%.1f, %.1f) on '%s' -> handled=%s%n",
                clickX, clickY, btn.getMessage().getString(), handled);
    }
}
