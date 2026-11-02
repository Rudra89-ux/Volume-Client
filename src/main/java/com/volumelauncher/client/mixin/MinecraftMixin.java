package com.volumelauncher.client.mixin;

import com.volumelauncher.client.module.ModuleManager;
import com.volumelauncher.client.test.MenuButtonTester;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void volume$onClientTick(CallbackInfo ci) {
        ModuleManager.getInstance().onTick();
        MenuButtonTester.onClientTick();
    }
}
