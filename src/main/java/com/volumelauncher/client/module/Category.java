package com.volumelauncher.client.module;

public enum Category {
    COMBAT("Combat", "Experimental combat mechanics for test environments"),
    RENDER("Render", "Visual indicators and world overlays"),
    PLAYER("Player", "Client-side player utilities and conveniences"),
    HUD("HUD", "Heads-up display informational widgets"),
    UTILITY("Utilities", "Feather-inspired client quality-of-life enhancements");

    private final String displayName;
    private final String description;

    Category(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
