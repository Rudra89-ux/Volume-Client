package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class AutoClickerModule extends Module {
    private final NumberSetting minCps = new NumberSetting("minCps", "Min CPS", "Minimum clicks per second", 9.0, 4.0, 20.0, 1.0);
    private final NumberSetting maxCps = new NumberSetting("maxCps", "Max CPS", "Maximum clicks per second", 13.0, 4.0, 20.0, 1.0);
    private final BooleanSetting jitter = new BooleanSetting("jitter", "Jitter", "Add subtle crosshair shake", false);

    private final Random random = new Random();
    private long nextClickDelay = 100;
    private long lastClickTime = 0;

    public AutoClickerModule() {
        super("autoclicker", "Auto Clicker", "Automatically clicks attack when holding the left mouse button.", Category.COMBAT, 0, true);
        addSetting(minCps);
        addSetting(maxCps);
        addSetting(jitter);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gui == null || mc.gui.screen() != null) return;

        long window = mc.getWindow().handle();
        boolean leftMouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (leftMouseDown) {
            long now = System.currentTimeMillis();
            if (now - lastClickTime >= nextClickDelay) {
                // Perform attack on crosshair entity or swing
                if (mc.crosshairPickEntity != null && mc.gameMode != null) {
                    mc.gameMode.attack(mc.player, mc.crosshairPickEntity);
                }
                mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
                lastClickTime = now;

                // Randomize next interval between min and max CPS
                double targetCps = minCps.getValue() + random.nextDouble() * Math.max(0.1, maxCps.getValue() - minCps.getValue());
                nextClickDelay = (long) (1000.0 / Math.max(1.0, targetCps));

                // Jitter effect
                if (jitter.getValue()) {
                    mc.player.setYRot(mc.player.getYRot() + (random.nextFloat() - 0.5f) * 0.8f);
                    mc.player.setXRot(mc.player.getXRot() + (random.nextFloat() - 0.5f) * 0.8f);
                }
            }
        }
    }
}
