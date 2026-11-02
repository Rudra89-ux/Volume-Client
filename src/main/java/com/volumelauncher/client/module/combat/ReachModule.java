package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.NumberSetting;

public class ReachModule extends Module {
    private final NumberSetting distance = new NumberSetting("distance", "Reach Distance", "Maximum attack reach in blocks", 4.2, 3.0, 5.5, 0.1);

    public ReachModule() {
        super("reach", "Reach", "Configures test reach distance for client attack calculations.", Category.COMBAT, 0, true);
        addSetting(distance);
    }

    public double getReachDistance() {
        return isEnabled() ? distance.getValue() : 3.0;
    }
}
