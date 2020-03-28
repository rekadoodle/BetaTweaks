package net.minecraft.src;

import java.nio.FloatBuffer;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.betatweaks.*;

public class mod_BetaTweaks extends BaseMod {

	public mod_BetaTweaks() {
		BetaTweaks.INSTANCE.init(this);
	}
	
	@Override
	public String Version() {
		return BetaTweaks.INSTANCE.version();
	}
	
	@Override
	public void KeyboardEvent(KeyBinding keybinding)
    {
		BetaTweaks.INSTANCE.keyboardEvent(keybinding);
    }

	@Override
	public void ModsLoaded() {
		BetaTweaks.INSTANCE.modsLoaded();
	}
	
	@Override
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		BetaTweaks.INSTANCE.onTickInGUI(mc, guiscreen);
		return true;
	}

    @Override
	public boolean OnTickInGame(Minecraft mc) {
		BetaTweaks.INSTANCE.onTickInGame(mc);
		return true;
	}
	
	//These methods are used to avoid reflection in classes from the betatweaks package.
	public static void drawRect(int i, int j, int k, int l, int colour) {
		Graphics.gui.drawRect(i, j, k, l, colour);
	}
	
	public static FloatBuffer fogColorBuffer(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBuffer;
	}
	
	public static float fogColorRed(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorRed;
	}
	
	public static float fogColorGreen(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorGreen;
	}
	
	public static float fogColorBlue(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBlue;
	}
	
	public static void setFallDistance(Entity entity, float fallDistance) {
		entity.fallDistance = fallDistance;
	}
	
	public static int xSize(GuiContainer container) {
		return container.xSize;
	}
	
	public static int ySize(GuiContainer container) {
		return container.ySize;
	}
	
	public static List<?> controlList(GuiScreen guiscreen) {
		return guiscreen.controlList;
	}
	
	//Info for mine_diver's mod menu
	public String Description() {
		return BetaTweaks.INSTANCE.modMenuDescription();
	}
	
	public String Name() {
		return BetaTweaks.INSTANCE.modMenuName();
	}
	
	public String Icon() {
		return BetaTweaks.INSTANCE.modMenuIcon();
	}
}
