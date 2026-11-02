package com.volumelauncher.client.env;

import net.minecraft.client.Minecraft;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages environment separation between Fair-Play Mode and Experimental Mode.
 * Strictly restricts combat automation to singleplayer or explicitly allowed private servers.
 */
public class EnvironmentManager {
    public enum Mode {
        FAIR_PLAY("Fair-Play"),
        EXPERIMENTAL("Experimental");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final EnvironmentManager INSTANCE = new EnvironmentManager();
    private final Set<String> allowedPrivateServers = new HashSet<>();
    private boolean forceFairPlay = false;

    private EnvironmentManager() {
        // Default allowed localhost test targets
        allowedPrivateServers.add("localhost");
        allowedPrivateServers.add("127.0.0.1");
    }

    public static EnvironmentManager getInstance() {
        return INSTANCE;
    }

    public boolean isForceFairPlay() {
        return forceFairPlay;
    }

    public void setForceFairPlay(boolean forceFairPlay) {
        this.forceFairPlay = forceFairPlay;
    }

    public void addAllowedPrivateServer(String serverAddress) {
        if (serverAddress != null && !serverAddress.trim().isEmpty()) {
            allowedPrivateServers.add(serverAddress.trim().toLowerCase());
        }
    }

    public boolean isExperimentalAllowed() {
        if (forceFairPlay) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }

        // 1. Singleplayer local integrated server is always permitted
        if (mc.hasSingleplayerServer() || mc.isLocalServer()) {
            return true;
        }

        // 2. Private test server verification
        if (mc.getCurrentServer() != null) {
            String ip = mc.getCurrentServer().ip.toLowerCase();
            if (allowedPrivateServers.contains(ip)) {
                return true;
            }
            for (String allowed : allowedPrivateServers) {
                if (ip.startsWith(allowed + ":")) {
                    return true;
                }
            }
        }

        // Public multiplayer servers are strictly disallowed for experimental modules
        return false;
    }

    public Mode getCurrentMode() {
        return isExperimentalAllowed() ? Mode.EXPERIMENTAL : Mode.FAIR_PLAY;
    }
}
