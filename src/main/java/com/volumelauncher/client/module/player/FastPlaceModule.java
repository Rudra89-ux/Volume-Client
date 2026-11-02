package com.volumelauncher.client.module.player;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.NumberSetting;

public class FastPlaceModule extends Module {
    private final NumberSetting delay = new NumberSetting("delay", "Delay Ticks", "Ticks between consecutive placements", 0.0, 0.0, 4.0, 1.0);

    public FastPlaceModule() {
        super("fastplace", "Fast Place", "Removes or reduces the block placement delay interval.", Category.PLAYER, 0, false);
        addSetting(delay);
    }

    public int getDelay() {
        return isEnabled() ? delay.getValueInt() : 4;
    }
}
