package com.volumelauncher.client;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class VolumeClientPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        System.out.println("[VolumeClient] Pre-launch initialized for Minecraft 26.3");
    }
}
