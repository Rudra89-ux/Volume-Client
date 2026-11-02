package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;

public class ToggleSprintModule extends Module {
    public ToggleSprintModule() {
        super("togglesprint", "Toggle Sprint", "Maintains sprinting automatically when moving forward.", Category.UTILITY, 0, false);
    }

    @Override
    public void onTick() {
        if (mc.player != null && isEnabled()) {
            if (mc.player.input != null && mc.player.input.hasForwardImpulse() && !mc.player.horizontalCollision) {
                if (!mc.player.isSprinting() && !mc.player.isShiftKeyDown()) {
                    mc.player.setSprinting(true);
                }
            }
        }
    }
}
