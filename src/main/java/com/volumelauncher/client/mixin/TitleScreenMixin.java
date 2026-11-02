package com.volumelauncher.client.mixin;

import com.volumelauncher.client.gui.menu.VolumeMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void volume$redirectToCustomMainMenu(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui != null && mc.gui.screen() instanceof TitleScreen && !(mc.gui.screen() instanceof VolumeMainMenuScreen)) {
            mc.setScreenAndShow(new VolumeMainMenuScreen());
            ci.cancel();
        }
    }
}
