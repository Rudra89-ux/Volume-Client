package com.volumelauncher.client.gui.menu;

import com.volumelauncher.client.gui.theme.ParchmentTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ParchmentButton extends Button {
    public ParchmentButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        boolean hovered = this.isHoveredOrFocused();
        Font font = Minecraft.getInstance().font;
        ParchmentTheme.drawParchmentButton(
            extractor,
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            this.getMessage().getString(),
            hovered,
            font
        );
    }
}
