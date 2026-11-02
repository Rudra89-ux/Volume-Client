package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class AimAssistModule extends Module {
    private final NumberSetting speed = new NumberSetting("speed", "Smooth Speed", "Aim interpolation rate", 4.0, 1.0, 10.0, 0.5);
    private final NumberSetting fov = new NumberSetting("fov", "FOV Limit", "Angle limit around crosshair", 75.0, 20.0, 180.0, 5.0);
    private final BooleanSetting playersOnly = new BooleanSetting("playersOnly", "Players Only", "Only track players", true);

    public AimAssistModule() {
        super("aimassist", "Aim Assist", "Smoothly assists aiming crosshairs toward opponents in test games.", Category.COMBAT, 0, true);
        addSetting(speed);
        addSetting(fov);
        addSetting(playersOnly);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        double maxDistSq = 6.0 * 6.0;
        Entity target = StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false)
            .filter(e -> e instanceof LivingEntity && e != mc.player && ((LivingEntity) e).isAlive())
            .filter(e -> !playersOnly.getValue() || e instanceof Player)
            .filter(e -> mc.player.distanceToSqr(e) <= maxDistSq)
            .min(Comparator.comparingDouble(e -> mc.player.distanceToSqr(e)))
            .orElse(null);

        if (target != null) {
            double dx = target.getX() - mc.player.getX();
            double dy = (target.getY() + target.getEyeHeight() * 0.75) - (mc.player.getY() + mc.player.getEyeHeight());
            double dz = target.getZ() - mc.player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

            float yawDiff = wrapDegrees(targetYaw - mc.player.getYRot());
            float pitchDiff = targetPitch - mc.player.getXRot();

            if (Math.abs(yawDiff) <= fov.getValue()) {
                float step = (float) (speed.getValue() * 0.6);
                mc.player.setYRot(mc.player.getYRot() + clamp(yawDiff, -step, step));
                mc.player.setXRot(mc.player.getXRot() + clamp(pitchDiff, -step, step));
            }
        }
    }

    private float wrapDegrees(float value) {
        float wrapped = value % 360.0F;
        if (wrapped >= 180.0F) wrapped -= 360.0F;
        if (wrapped < -180.0F) wrapped += 360.0F;
        return wrapped;
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
