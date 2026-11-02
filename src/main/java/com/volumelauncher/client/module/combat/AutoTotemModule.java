package com.volumelauncher.client.module.combat;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoTotemModule extends Module {
    private final NumberSetting healthThreshold = new NumberSetting("threshold", "Health Threshold", "Health to automatically swap totem", 8.0, 2.0, 16.0, 1.0);

    public AutoTotemModule() {
        super("autototem", "Auto Totem", "Automatically equips a Totem of Undying to the offhand when health is low.", Category.COMBAT, 0, true);
        addSetting(healthThreshold);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) return;

        if (mc.player.getHealth() <= healthThreshold.getValueFloat()) {
            ItemStack offhand = mc.player.getOffhandItem();
            if (offhand.is(Items.TOTEM_OF_UNDYING)) {
                return; // Already holding totem
            }

            // Search inventory for totem (slots 9 to 44)
            for (int slot = 9; slot <= 44; slot++) {
                ItemStack stack = mc.player.inventoryMenu.getSlot(slot).getItem();
                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    // Pick up totem and swap to offhand (offhand slot is 45)
                    int containerId = mc.player.inventoryMenu.containerId;
                    mc.gameMode.handleContainerInput(containerId, slot, 0, ContainerInput.PICKUP, mc.player);
                    mc.gameMode.handleContainerInput(containerId, 45, 0, ContainerInput.PICKUP, mc.player);
                    if (!mc.player.inventoryMenu.getCarried().isEmpty()) {
                        mc.gameMode.handleContainerInput(containerId, slot, 0, ContainerInput.PICKUP, mc.player);
                    }
                    break;
                }
            }
        }
    }
}
