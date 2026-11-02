package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.NumberSetting;

public class HitboxModule extends Module {
    private final NumberSetting expansion = new NumberSetting("expansion", "Expansion", "Hitbox expansion width in blocks", 0.4, 0.1, 1.2, 0.05);

    public HitboxModule() {
        super("hitbox", "Hitbox", "Expands entity target detection bounds for combat testing.", Category.COMBAT, 0, true);
        addSetting(expansion);
    }

    public float getExpansion() {
        return isEnabled() ? expansion.getValueFloat() : 0.0f;
    }
}
