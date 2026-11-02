package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;

public class TriggerBotModule extends Module {
    private final NumberSetting delay = new NumberSetting("delay", "Delay (ms)", "Attack pause between swings", 80.0, 0.0, 300.0, 10.0);
    private final BooleanSetting checkCooldown = new BooleanSetting("checkCooldown", "Check Cooldown", "Only attack when attack meter is full", true);

    private long lastAttack = 0;

    public TriggerBotModule() {
        super("triggerbot", "Trigger Bot", "Attacks automatically when your crosshair is hovering over a target.", Category.COMBAT, 0, true);
        addSetting(delay);
        addSetting(checkCooldown);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.crosshairPickEntity == null || mc.gameMode == null) return;

        if (mc.crosshairPickEntity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) mc.crosshairPickEntity;
            if (!target.isAlive() || target == mc.player) return;

            if (checkCooldown.getValue() && mc.player.getAttackStrengthScale(0.0f) < 0.95f) {
                return;
            }

            long now = System.currentTimeMillis();
            if (now - lastAttack >= delay.getValue()) {
                mc.gameMode.attack(mc.player, target);
                mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
                mc.player.resetAttackStrengthTicker();
                lastAttack = now;
            }
        }
    }
}
