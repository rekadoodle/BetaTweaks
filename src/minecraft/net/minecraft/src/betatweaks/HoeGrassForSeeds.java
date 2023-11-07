package net.minecraft.src.betatweaks;

import java.util.Random;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemHoe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;

public class HoeGrassForSeeds {

	private static int lastTickHoeDamage = -1;
	private static int lastTickHoeX;
	private static int lastTickHoeY;
	private static int lastTickHoeZ;
	
	public static void onTick(Minecraft mc, boolean serverModEnabled) {
		ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();
		MovingObjectPosition hoveredObj = mc.objectMouseOver;
		if(heldItem != null && heldItem.getItem() instanceof ItemHoe) {
			if(lastTickHoeDamage >= 0 && lastTickHoeDamage == heldItem.getItemDamage() - 1) {
				lastTickHoeDamage = -1;
				if (serverModEnabled) {
					References.mpHandler.grassHoed(lastTickHoeX, lastTickHoeY, lastTickHoeZ);
				} else {
					Random rand = new Random();
					int i;
					if ((i = Block.tallGrass.idDropped(0, rand)) != -1) {
						float f = 0.7F;
						float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						float f2 = 1.2F;
						float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						EntityItem seeds = new EntityItem(mc.theWorld, (double) lastTickHoeX + f1,
								(double) lastTickHoeY + f2, (double) lastTickHoeZ + f3, new ItemStack(i, 1, 0));
						seeds.delayBeforeCanPickup = 10;
						mc.theWorld.entityJoinedWorld(seeds);
					}
				}
			}
			else if(hoveredObj != null && hoveredObj.typeOfHit == EnumMovingObjectType.TILE && Mouse.isButtonDown(1)) {
				int x = hoveredObj.blockX;
				int y = hoveredObj.blockY;
				int z = hoveredObj.blockZ;
				if (Block.blocksList[mc.theWorld.getBlockId(x, y, z)] == Block.grass) {
					lastTickHoeDamage = heldItem.getItemDamage();
					lastTickHoeX = x;
					lastTickHoeY = y;
					lastTickHoeZ = z;
				}
				
			}
		}
	}
}
