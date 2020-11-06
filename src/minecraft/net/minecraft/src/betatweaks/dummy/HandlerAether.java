package net.minecraft.src.betatweaks.dummy;

import java.util.HashMap;

import net.minecraft.src.Entity;
import net.minecraft.src.GuiScreen;

public abstract class HandlerAether {

	public abstract void shearSheepuff(Entity entity);
	
	public abstract boolean simulatedWorldMenu();
	
	public abstract void registerGuiOverrides(HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>> map);
	
	public abstract void preGuiScreenOverride(GuiScreen guiscreen);
	
	public abstract void postGuiScreenOverride(GuiScreen guiscreen);
	
	public abstract void onServerListClosed(int musicId);
	
	public abstract void displayAetherMultiplayer(GuiScreen guiscreen, int musicId);
}
