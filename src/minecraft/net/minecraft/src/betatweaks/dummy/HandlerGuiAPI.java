package net.minecraft.src.betatweaks.dummy;

import java.util.List;

import org.lwjgl.opengl.DisplayMode;

import net.minecraft.src.*;

public abstract class HandlerGuiAPI {
	
	public abstract void init(List<DisplayMode> resolutions);
	
	public abstract void handleTooltip(GuiScreen guiscreen);
	
	public abstract void onGuiScreenChanged(GuiScreen guiscreen);

	public abstract void loadSettingsToGUI();
	
	public abstract void loadSettingsFromGUI();
	
}
