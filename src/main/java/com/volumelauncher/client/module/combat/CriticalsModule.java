package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.ModeSetting;
import net.minecraft.world.phys.Vec3;

public class CriticalsModule extends Module {
    private final ModeSetting mode = new ModeSetting("mode", "Mode", "Critical mechanics style", "Mini-Jump", "Mini-Jump", "Visual");

    public CriticalsModule() {
        super("criticals", "Criticals", "Times micro-falls or mini-jumps to land critical strikes in test environments.", Category.COMBAT, 0, true);
        addSetting(mode);
    }

    public void triggerCritical() {
        if (!isEnabled() || mc.player == null) return;

        if (mode.is("Mini-Jump") && mc.player.onGround()) {
            Vec3 vel = mc.player.getDeltaMovement();
            mc.player.setDeltaMovement(vel.x, 0.22, vel.z);
        }
    }
}
