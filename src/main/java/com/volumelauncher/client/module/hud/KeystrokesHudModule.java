package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.ModuleManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

public class KeystrokesHudModule extends HudModule {
    public KeystrokesHudModule() {
        super("keystrokes", "Keystrokes", "Displays on-screen WASD and mouse button presses.", 10, 105);
    }

    @Override
    public int getWidth(Font font) {
        return 72;
    }

    @Override
    public int getHeight(Font font) {
        return 66;
    }

    @Override
    public void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks) {
        int baseX = getX() + 4;
        int baseY = getY() + 4;

        boolean wDown = mc.options.keyUp.isDown();
        boolean aDown = mc.options.keyLeft.isDown();
        boolean sDown = mc.options.keyDown.isDown();
        boolean dDown = mc.options.keyRight.isDown();

        long window = mc.getWindow().handle();
        boolean lmbDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmbDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        // W key (center top)
        drawKey(extractor, font, "W", baseX + 22, baseY, 20, 18, wDown);

        // A, S, D keys
        drawKey(extractor, font, "A", baseX, baseY + 20, 20, 18, aDown);
        drawKey(extractor, font, "S", baseX + 22, baseY + 20, 20, 18, sDown);
        drawKey(extractor, font, "D", baseX + 44, baseY + 20, 20, 18, dDown);

        // LMB, RMB
        CpsHudModule cpsMod = ModuleManager.getInstance().getModule(CpsHudModule.class);
        String lmbText = cpsMod != null ? cpsMod.getLeftCps() + " CPS" : "LMB";
        String rmbText = cpsMod != null ? cpsMod.getRightCps() + " CPS" : "RMB";

        drawKey(extractor, font, lmbText, baseX, baseY + 40, 31, 18, lmbDown);
        drawKey(extractor, font, rmbText, baseX + 33, baseY + 40, 31, 18, rmbDown);
    }

    private void drawKey(GuiGraphicsExtractor extractor, Font font, String label, int kx, int ky, int kw, int kh, boolean pressed) {
        int bg = pressed ? ParchmentTheme.GOLD_ACCENT : 0x403A281B;
        extractor.fill(kx, ky, kx + kw, ky + kh, bg);
        extractor.outline(kx, ky, kw, kh, ParchmentTheme.WOOD_DARK);

        int textW = font.width(label);
        int tx = kx + (kw - textW) / 2;
        int ty = ky + (kh - 8) / 2;
        int col = pressed ? ParchmentTheme.TEXT_DARK : ParchmentTheme.TEXT_MUTED;
        extractor.text(font, label, tx, ty, col, false);
    }
}
