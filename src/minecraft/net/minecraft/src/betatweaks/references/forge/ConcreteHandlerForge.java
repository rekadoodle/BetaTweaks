package net.minecraft.src.betatweaks.references.forge;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.references.HandlerForge;
import net.minecraft.src.forge.ForgeHooksClient;

public class ConcreteHandlerForge extends HandlerForge {

	@Override
	public boolean onBlockHighlight(RenderGlobal renderglobal, EntityPlayer entityplayer,
			MovingObjectPosition objectMouseOver, int i, ItemStack currentItem, float f) {
		return ForgeHooksClient.onBlockHighlight(renderglobal, entityplayer, objectMouseOver, i, currentItem, f);
	}

}
