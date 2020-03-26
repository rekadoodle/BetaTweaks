package net.minecraft.src.betatweaks.references.guiapi;

import java.util.List;
import org.lwjgl.opengl.DisplayMode;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.dummy.HandlerGuiAPI;

public class ConcreteHandler extends HandlerGuiAPI {
	
	final HandlerGuiAPI HANDLER = (HandlerGuiAPI) Utils.getHandler("guiapi" + (Utils.classExists("GuiApiHelper") ? ".v11" : ".v10"));
	
	@Override
	public void init(final List<DisplayMode> resolutions) {
		HANDLER.init(resolutions);
	}
	
	@Override
	public void handleTooltip(GuiScreen guiscreen, int posX, int posY) {
		HANDLER.handleTooltip(guiscreen, posX, posY);
	}
	
	@Override
	public boolean isGuiModScreen(GuiScreen guiscreen) {
		return HANDLER.isGuiModScreen(guiscreen);
	}

	@Override
	public boolean isGuiModSelectScreen(GuiScreen guiscreen) {
		return HANDLER.isGuiModSelectScreen(guiscreen);
	}

	@Override
	public boolean settingsChanged(GuiScreen guiscreen) {
		return HANDLER.settingsChanged(guiscreen);
	}

	@Override
	public void loadSettingsToGUI() {
		HANDLER.loadSettingsToGUI();
	}
	
	
	@Override
	public void loadSettingsFromGUI() {
		HANDLER.loadSettingsFromGUI();
	}

}
