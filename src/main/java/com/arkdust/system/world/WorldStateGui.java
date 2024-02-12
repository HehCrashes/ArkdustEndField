package com.arkdust.system.world;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

public class WorldStateGui extends Screen {
    public WorldStateGui() {
        super(Component.translatable("gui.arkdust.level_state"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
