package com.volumelauncher.client.setting;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String id, String name, String description, int defaultColorRgba) {
        super(id, name, description, defaultColorRgba);
    }

    public int getRed() {
        return (getValue() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (getValue() >> 8) & 0xFF;
    }

    public int getBlue() {
        return getValue() & 0xFF;
    }

    public int getAlpha() {
        return (getValue() >> 24) & 0xFF;
    }

    @Override
    public String getSerializedValue() {
        return String.format("#%08X", getValue());
    }

    @Override
    public void deserializeValue(String serialized) {
        if (serialized != null) {
            try {
                if (serialized.startsWith("#")) {
                    serialized = serialized.substring(1);
                }
                long parsed = Long.parseLong(serialized, 16);
                setValue((int) parsed);
            } catch (Exception ignored) {
            }
        }
    }
}
