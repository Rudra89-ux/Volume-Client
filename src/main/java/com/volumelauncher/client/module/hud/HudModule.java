package com.volumelauncher.client.module.hud;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import com.volumelauncher.client.module.Category;
import com.volumelauncher.client.module.Module;
import com.volumelauncher.client.setting.BooleanSetting;
import com.volumelauncher.client.setting.NumberSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudModule extends Module {
    protected final NumberSetting posX;
    protected final NumberSetting posY;
    protected final BooleanSetting background = new BooleanSetting("bg", "Parchment Box", "Render earthy parchment panel background", true);

    public HudModule(String id, String name, String description, int defaultX, int defaultY) {
        super(id, name, description, Category.HUD, 0, false);
        this.posX = new NumberSetting("posX", "Position X", "Horizontal screen coordinate", defaultX, 0, 3840, 1);
        this.posY = new NumberSetting("posY", "Position Y", "Vertical screen coordinate", defaultY, 0, 2160, 1);
        addSetting(posX);
        addSetting(posY);
        addSetting(background);
    }

    public int getX() {
        return posX.getValueInt();
    }

    public void setX(int x) {
        posX.setValue((double) x);
    }

    public int getY() {
        return posY.getValueInt();
    }

    public void setY(int y) {
        posY.setValue((double) y);
    }

    public abstract int getWidth(Font font);
    public abstract int getHeight(Font font);
    public abstract void renderElement(GuiGraphicsExtractor extractor, Font font, float partialTicks);

    @Override
    public void onRenderHud(GuiGraphicsExtractor extractor, float partialTicks) {
        if (!isEnabled() || mc.font == null) return;

        Font font = mc.font;
        int w = getWidth(font);
        int h = getHeight(font);
        int x = getX();
        int y = getY();

        if (background.getValue()) {
            ParchmentTheme.drawParchmentPanel(extractor, x, y, w, h);
        }

        renderElement(extractor, font, partialTicks);
    }
}
