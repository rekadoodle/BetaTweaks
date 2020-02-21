package net.minecraft.src.betatweaks.references.hmi;

import hmi.GuiRecipeViewer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.betatweaks.references.HandlerHMI;

public class ConcreteHandlerHMI extends HandlerHMI {

	@Override
	public boolean isGuiRecipeViewer(GuiScreen guiscreen) {
		return guiscreen instanceof GuiRecipeViewer;
	}

}
