package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class KillAuraModule extends Module {
    private final NumberSetting range = new NumberSetting("range", "Range", "Maximum attack reach", 4.2, 3.0, 6.0, 0.1);
    private final NumberSetting cps = new NumberSetting("cps", "Target CPS", "Attacks per second if cooldown check disabled", 10.0, 4.0, 20.0, 1.0);
    private final BooleanSetting playersOnly = new BooleanSetting("playersOnly", "Players Only", "Only attack other player entities", true);
    private final BooleanSetting cooldownCheck = new BooleanSetting("cooldown", "Attack Cooldown", "Wait for 1.9+ weapon recovery strength", true);

    private long lastAttackTime = 0;

    public KillAuraModule() {
        super("killaura", "KillAura", "Automatically strikes nearby valid entities in test environments.", Category.COMBAT, GLFW.GLFW_KEY_R, true);
        addSetting(range);
        addSetting(cps);
        addSetting(playersOnly);
        addSetting(cooldownCheck);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        // Check weapon cooldown or delay
        if (cooldownCheck.getValue()) {
            if (mc.player.getAttackStrengthScale(0.0f) < 0.92f) {
                return;
            }
        } else {
            long delay = (long) (1000.0 / cps.getValue());
            if (System.currentTimeMillis() - lastAttackTime < delay) {
                return;
            }
        }

        double reachDist = range.getValue();
        double reachSq = reachDist * reachDist;

        // Find closest valid living entity
        Entity target = StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false)
            .filter(e -> e instanceof LivingEntity)
            .filter(e -> e != mc.player)
            .filter(e -> ((LivingEntity) e).isAlive())
            .filter(e -> !playersOnly.getValue() || e instanceof Player)
            .filter(e -> mc.player.distanceToSqr(e) <= reachSq)
            .min(Comparator.comparingDouble(e -> mc.player.distanceToSqr(e)))
            .orElse(null);

        if (target != null) {
            // Execute attack
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            mc.player.resetAttackStrengthTicker();
            lastAttackTime = System.currentTimeMillis();
        }
    }
}
