package net.minecraft.src.betatweaks;

import java.util.Random;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;

public class PunchSheepForWool {

	public static void onTick(Minecraft mc, boolean serverModEnabled) {
		if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY) {
			Entity entity = mc.objectMouseOver.entityHit;
			if (entity instanceof EntitySheep) {
				EntitySheep sheep = (EntitySheep) entity;
				if (serverModEnabled) {
					References.mpHandler.sheepPunched(sheep.entityId);
				} else if (sheep.beenAttacked && !sheep.getSheared()) {
					sheep.setSheared(true);
					Random rand = new Random();
					int i = 2 + rand.nextInt(3);
					for (int j = 0; j < i; j++) {
						EntityItem wool = sheep
								.entityDropItem(new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
						wool.motionY += rand.nextFloat() * 0.05F;
						wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}
				}
			} else if (References.isInstalled(References.aetherHandler)) {
				References.aetherHandler.shearSheepuff(entity);
			}
		}
	}
}
