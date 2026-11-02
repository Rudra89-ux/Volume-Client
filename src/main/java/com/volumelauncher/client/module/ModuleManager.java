package com.volumelauncher.client.module;

import com.volumelauncher.client.env.EnvironmentManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    private final Map<String, Module> moduleMap = new HashMap<>();

    private ModuleManager() {}

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public synchronized void registerModule(Module module) {
        if (module != null && !moduleMap.containsKey(module.getId().toLowerCase())) {
            modules.add(module);
            moduleMap.put(module.getId().toLowerCase(), module);
        }
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getModulesByCategory(Category category) {
        List<Module> list = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == category) {
                list.add(m);
            }
        }
        return list;
    }

    public Module getModuleById(String id) {
        if (id == null) return null;
        return moduleMap.get(id.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) {
                return (T) m;
            }
        }
        return null;
    }

    public boolean isModuleEnabled(String id) {
        Module m = getModuleById(id);
        return m != null && m.isEnabled();
    }

    public void onTick() {
        boolean experimentalAllowed = EnvironmentManager.getInstance().isExperimentalAllowed();

        for (Module m : modules) {
            if (m.isEnabled()) {
                // Enforce environment safety on tick
                if (m.isExperimental() && !experimentalAllowed) {
                    m.setEnabled(false);
                    continue;
                }

                try {
                    m.onTick();
                } catch (Throwable t) {
                    System.err.println("[Volume Client] Error ticking module " + m.getId() + ": " + t.getMessage());
                }
            }
        }
    }

    public void onRenderHud(GuiGraphicsExtractor extractor, float partialTicks) {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try {
                    m.onRenderHud(extractor, partialTicks);
                } catch (Throwable t) {
                    System.err.println("[Volume Client] Error rendering HUD for module " + m.getId() + ": " + t.getMessage());
                }
            }
        }
    }

    public void onKey(int keyCode, int scanCode, int action) {
        // action 1 = press
        if (action == 1) {
            for (Module m : modules) {
                if (m.getKeybind() > 0 && m.getKeybind() == keyCode) {
                    m.toggle();
                }
                if (m.isEnabled()) {
                    m.onKey(keyCode, scanCode, action);
                }
            }
        }
    }
}
