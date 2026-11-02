package com.volumelauncher.client.module.utility;

import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import net.minecraft.client.CameraType;
import org.lwjgl.glfw.GLFW;

public class FreelookModule extends Module {
    private CameraType previousCameraType = CameraType.FIRST_PERSON;

    public FreelookModule() {
        super("freelook", "Freelook", "Allows 360 degree 3rd-person camera rotation while maintaining motion direction.", Category.UTILITY, GLFW.GLFW_KEY_V, false);
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            previousCameraType = mc.options.getCameraType();
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.setCameraType(previousCameraType);
        }
    }
}
