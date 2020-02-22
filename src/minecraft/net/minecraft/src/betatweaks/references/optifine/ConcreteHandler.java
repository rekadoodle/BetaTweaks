package net.minecraft.src.betatweaks.references.optifine;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Config;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.dummy.HandlerOptifine;

public class ConcreteHandler extends HandlerOptifine {

	@Override
	public boolean isFarView() {
		return Config.isFarView();
	}

	@Override
	public boolean isWaterFancy() {
		return Config.isWaterFancy();
	}

	@Override
	public float getAlphaFuncLevel() {
		return Config.getAlphaFuncLevel();
	}

	@Override
	public boolean isUseAlphaFunc() {
		return Config.isUseAlphaFunc();
	}
	
	@Override
	public boolean zoomKeyHeld() {
		return Keyboard.isKeyDown(Utils.mc.gameSettings.ofKeyBindZoom.keyCode);
	}

	@Override
	public int renderAllSortedRenders(float f) {
		return Utils.mc.renderGlobal.renderAllSortedRenderers(1, f);
	}
}
