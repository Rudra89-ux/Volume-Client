package com.volumelauncher.client.keybind;

import com.volumelauncher.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeybindManager {
    private static final KeybindManager INSTANCE = new KeybindManager();
    private static final Map<Integer, String> KEY_NAMES = new HashMap<>();

    static {
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_SHIFT, "R-Shift");
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_SHIFT, "L-Shift");
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_CONTROL, "R-Ctrl");
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_CONTROL, "L-Ctrl");
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_ALT, "R-Alt");
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_ALT, "L-Alt");
        KEY_NAMES.put(GLFW.GLFW_KEY_SPACE, "Space");
        KEY_NAMES.put(GLFW.GLFW_KEY_TAB, "Tab");
        KEY_NAMES.put(GLFW.GLFW_KEY_ESCAPE, "Esc");
        KEY_NAMES.put(GLFW.GLFW_KEY_ENTER, "Enter");
        KEY_NAMES.put(GLFW.GLFW_KEY_BACKSPACE, "Backspace");
        KEY_NAMES.put(GLFW.GLFW_KEY_CAPS_LOCK, "Caps");
    }

    private KeybindManager() {}

    public static KeybindManager getInstance() {
        return INSTANCE;
    }

    public static String getKeyName(int keyCode) {
        if (keyCode <= 0) {
            return "None";
        }
        if (KEY_NAMES.containsKey(keyCode)) {
            return KEY_NAMES.get(keyCode);
        }
        String glfwName = GLFW.glfwGetKeyName(keyCode, 0);
        if (glfwName != null && !glfwName.trim().isEmpty()) {
            return glfwName.toUpperCase();
        }
        return "Key " + keyCode;
    }

    public void handleKeyPress(int keyCode, int scanCode, int action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        // Dispatch to ModuleManager
        ModuleManager.getInstance().onKey(keyCode, scanCode, action);
    }
}
