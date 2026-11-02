package com.volumelauncher.client.setting;

import java.util.function.Consumer;

public abstract class Setting<T> {
    private final String id;
    private final String name;
    private final String description;
    protected T value;
    protected final T defaultValue;
    private Consumer<T> changeListener;

    public Setting(String id, String name, String description, T defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        if (changeListener != null) {
            changeListener.accept(value);
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        setValue(defaultValue);
    }

    public void setChangeListener(Consumer<T> listener) {
        this.changeListener = listener;
    }

    public abstract String getSerializedValue();
    public abstract void deserializeValue(String serialized);
}
