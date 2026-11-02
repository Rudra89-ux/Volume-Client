package com.volumelauncher.client.module.render;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class EspModule extends Module {
    private final BooleanSetting players = new BooleanSetting("players", "Players", "Highlight player entities", true);
    private final BooleanSetting monsters = new BooleanSetting("monsters", "Monsters", "Highlight hostile monsters", true);
    private final BooleanSetting items = new BooleanSetting("items", "Dropped Items", "Highlight item entities", false);

    public EspModule() {
        super("esp", "ESP", "Highlights players and entities through world obstacles.", Category.RENDER, GLFW.GLFW_KEY_V, false);
        addSetting(players);
        addSetting(monsters);
        addSetting(items);
    }

    public boolean shouldGlow(Entity entity) {
        if (!isEnabled() || entity == null || mc.player == null) return false;
        if (entity == mc.player) return false;

        if (entity instanceof Player && players.getValue()) return true;
        if (entity instanceof Monster && monsters.getValue()) return true;
        if (entity instanceof ItemEntity && items.getValue()) return true;

        return false;
    }
}
