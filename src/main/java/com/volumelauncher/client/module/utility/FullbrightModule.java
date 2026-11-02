package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullbrightModule extends Module {
    private final NumberSetting gamma = new NumberSetting("gamma", "Brightness", "Ambient lighting level", 12.0, 1.0, 16.0, 1.0);

    public FullbrightModule() {
        super("fullbright", "Fullbright", "Enhances brightness in dark caves and nighttime mining.", Category.UTILITY, 0, false);
        addSetting(gamma);
    }

    @Override
    public void onTick() {
        if (mc.player != null && isEnabled()) {
            Holder<net.minecraft.world.effect.MobEffect> nv = MobEffects.NIGHT_VISION;
            if (!mc.player.hasEffect(nv)) {
                mc.player.addEffect(new MobEffectInstance(nv, 1200, 0, false, false, false));
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
