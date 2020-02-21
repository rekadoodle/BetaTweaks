package net.minecraft.src.betatweaks.dummy;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityRenderer;

public abstract class HandlerShaders {

	public abstract void init(EntityRenderer renderer);
	
	public boolean second_renderpass;

	public abstract void updateFBOSize(int i, int j);
	
	public abstract int getWaterRenderMode();
	
	public abstract void bind(int i);
	public abstract void unbind();
	public abstract void bindBlack();
	public abstract void unbindBlack();
	public abstract void bindWhite(EntityLiving entityliving);
	public abstract void unbindWhite();
	public abstract void bindTransparency();
	public abstract void unbindTransparency();
	
	public abstract boolean getWaterReflectiveItems();
	public abstract boolean getWaterReflectivePlayer();
	public abstract boolean getWaterReflectiveClouds();
	
	public abstract void confusingShaderStuff();
	
}
