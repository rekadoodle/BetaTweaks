package net.minecraft.src.betatweaks.references.optifine;

import net.minecraft.src.Config;
import net.minecraft.src.betatweaks.references.HandlerOptifine;

public class ConcreteHandlerOptifine extends HandlerOptifine {

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

}
