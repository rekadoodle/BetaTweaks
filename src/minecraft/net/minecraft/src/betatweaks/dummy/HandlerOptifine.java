package net.minecraft.src.betatweaks.dummy;

public abstract class HandlerOptifine {

	public abstract boolean isFarView();

	public abstract boolean isWaterFancy();

	public abstract float getAlphaFuncLevel();

	public abstract boolean isUseAlphaFunc();
	
	public abstract boolean zoomKeyHeld();
	
	public abstract int renderAllSortedRenders(float f);
}
