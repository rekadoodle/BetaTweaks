package net.minecraft.src.betatweaks.dummy;

import java.util.List;

import org.lwjgl.opengl.DisplayMode;

import net.minecraft.src.*;

public abstract class HandlerGuiAPI {
	
	public abstract void init(List<DisplayMode> resolutions);
	
	public abstract void handleTooltip(GuiScreen guiscreen, int posX, int posY);
	
	public abstract boolean isGuiModScreen(GuiScreen guiscreen);
	
	public abstract boolean isGuiModSelectScreen(GuiScreen guiscreen);
	
	public abstract boolean settingsChanged(GuiScreen guiscreen);

	public abstract void loadSettings();
	
	public abstract void updateSettings();
	
}
