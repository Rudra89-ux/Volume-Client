package com.volumelauncher.client.mixin;

import com.volumelauncher.client.module.ModuleManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void volume$onRenderHud(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        ModuleManager.getInstance().onRenderHud(extractor, partialTicks);
    }
}
