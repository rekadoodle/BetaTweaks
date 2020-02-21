package net.minecraft.src.betatweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.mod_BetaTweaks;

public class LadderGaps {

	public static void onTick(Minecraft mc) {
		for (int z = 0; z < mc.theWorld.loadedEntityList.size() + mc.theWorld.playerEntities.size(); z++) {
			Entity entity;
			if (z < mc.theWorld.loadedEntityList.size()) {
				entity = (Entity) mc.theWorld.loadedEntityList.get(z);
			} else {
				entity = (Entity) mc.theWorld.playerEntities.get(z - mc.theWorld.loadedEntityList.size());
			}
			if (entity instanceof EntityLiving) {
				int i = MathHelper.floor_double(entity.posX);
				int j = MathHelper.floor_double(entity.boundingBox.minY);
				int k = MathHelper.floor_double(entity.posZ);
				int l = mc.theWorld.getBlockId(i, j + 1, k);
				if (!((EntityLiving) entity).isOnLadder() && l == Block.ladder.blockID) {
					float f4 = 0.15F;
					if (entity.motionX < (double) (-f4)) {
						entity.motionX = (double) -f4;
					}
					if (entity.motionX > (double) f4) {
						entity.motionX = (double) f4;
					}
					if (entity.motionX < (double) (-f4)) {
						entity.motionX = (double) -f4;
					}
					if (entity.motionX > (double) f4) {
						entity.motionX = (double) f4;
					}
					mod_BetaTweaks.setFallDistance(entity, 0.0F);
					if (entity.motionY < -0.14999999999999999D) {
						entity.motionY = -0.14999999999999999D;
					}
					if (entity.isSneaking() && entity.motionY < 0.0D) {
						entity.motionY = 0.0D;
					}
					if (!((EntityLiving) entity).isOnLadder() && entity.isCollidedHorizontally) {
						entity.motionY = 0.20000000000000001D;
						entity.motionY -= 0.080000000000000002D;
						entity.motionY *= 0.98000001907348633D;
					}
				}
			}

		}
	}
}
