package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class ZoomModule extends Module {
    private final NumberSetting zoomFactor = new NumberSetting("factor", "Zoom Level", "Magnification multiplier", 3.5, 2.0, 6.0, 0.5);
    private final BooleanSetting smooth = new BooleanSetting("smooth", "Smooth Zoom", "Animate zoom transition smoothly", true);

    private boolean isZooming = false;
    private double currentFovModifier = 1.0;

    public ZoomModule() {
        super("zoom", "Zoom", "Magnifies camera view smoothly when holding keybind.", Category.UTILITY, GLFW.GLFW_KEY_C, false);
        addSetting(zoomFactor);
        addSetting(smooth);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        long window = mc.getWindow().handle();
        isZooming = isEnabled() && GLFW.glfwGetKey(window, getKeybind()) == GLFW.GLFW_PRESS;

        double target = isZooming ? (1.0 / zoomFactor.getValue()) : 1.0;
        if (smooth.getValue()) {
            currentFovModifier += (target - currentFovModifier) * 0.35;
        } else {
            currentFovModifier = target;
        }
    }

    public double getFovModifier() {
        return currentFovModifier;
    }

    public boolean isZooming() {
        return isZooming;
    }
}
