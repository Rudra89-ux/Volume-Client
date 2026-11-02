package com.volumelauncher.client.mixin;

import com.volumelauncher.client.gui.clickgui.ClickGuiScreen;
import com.volumelauncher.client.keybind.KeybindManager;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void volume$onKeyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        // Dispatch to client keybind manager
        KeybindManager.getInstance().handleKeyPress(event.key(), 0, action);

        // Open Click GUI on RIGHT SHIFT
        if (action == 1 && event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gui != null && mc.gui.screen() == null) {
                mc.setScreenAndShow(new ClickGuiScreen(null));
            }
        }
    }
}
