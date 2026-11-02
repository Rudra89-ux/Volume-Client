package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CpsHudModule extends HudModule {
    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();
    private boolean leftWasDown = false;
    private boolean rightWasDown = false;

    public CpsHudModule() {
        super("cps", "CPS Display", "Displays Left and Right clicks per second.", 10, 30);
    }

    @Override
    public void onTick() {
        long window = mc.getWindow().handle();
        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        long now = System.currentTimeMillis();

        if (leftDown && !leftWasDown) {
            leftClicks.add(now);
        }
        if (rightDown && !rightWasDown) {
            rightClicks.add(now);
        }

        leftWasDown = leftDown;
        rightWasDown = rightDown;

        // Purge clicks older than 1000ms
        leftClicks.removeIf(t -> now - t > 1000);
        rightClicks.removeIf(t -> now - t > 1000);
    }

    public int getLeftCps() {
        return leftClicks.size();
    }

    public int getRightCps() {
        return rightClicks.size();
    }

    @Override
    public int getWidth(Font font) {
        String str = "CPS: " + getLeftCps() + " | " + getRightCps();
        return font.width(str) + 14;
    }

    @Override
    public int getHeight(Font font) {
        return 16;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        String text = "§6CPS: §0" + getLeftCps() + " §8| §0" + getRightCps();
        extractor.text(font, text, getX() + 6, getY() + 4, ParchmentTheme.TEXT_DARK, false);
    }
}
