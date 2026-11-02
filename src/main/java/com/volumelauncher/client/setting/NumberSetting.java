package com.volumelauncher.client.setting;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String id, String name, String description, double defaultValue, double min, double max, double step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        setValue(clamp(defaultValue));
    }

    private double clamp(double val) {
        double clamped = Math.max(min, Math.min(max, val));
        if (step > 0) {
            clamped = Math.round(clamped / step) * step;
        }
        return clamped;
    }

    @Override
    public void setValue(Double value) {
        if (value != null) {
            super.setValue(clamp(value));
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public int getValueInt() {
        return (int) Math.round(getValue());
    }

    public float getValueFloat() {
        return getValue().floatValue();
    }

    @Override
    public String getSerializedValue() {
        return String.valueOf(getValue());
    }

    @Override
    public void deserializeValue(String serialized) {
        if (serialized != null) {
            try {
                setValue(Double.parseDouble(serialized));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
