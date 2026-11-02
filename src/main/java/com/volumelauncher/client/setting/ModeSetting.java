package com.volumelauncher.client.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String id, String name, String description, String defaultMode, String... modes) {
        super(id, name, description, defaultMode);
        this.modes = Arrays.asList(modes);
        if (!this.modes.contains(defaultMode) && !this.modes.isEmpty()) {
            setValue(this.modes.get(0));
        }
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        int index = modes.indexOf(getValue());
        if (index == -1 || index >= modes.size() - 1) {
            setValue(modes.get(0));
        } else {
            setValue(modes.get(index + 1));
        }
    }

    public boolean is(String mode) {
        return getValue() != null && getValue().equalsIgnoreCase(mode);
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }

    @Override
    public void deserializeValue(String serialized) {
        if (serialized != null && modes.contains(serialized)) {
            setValue(serialized);
        }
    }
}
