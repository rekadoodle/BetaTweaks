package betatweaks;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.forge.ForgeHooksClient;

public class BetaTweaksForgeHandler {

	public static boolean ForgeHooksClient_onBlockHighlight(RenderGlobal renderglobal, EntityPlayer entityplayer,
			MovingObjectPosition objectMouseOver, int i, ItemStack currentItem, float f) {
		return ForgeHooksClient.onBlockHighlight(renderglobal, entityplayer, objectMouseOver, i, currentItem, f);
		
	}
}
