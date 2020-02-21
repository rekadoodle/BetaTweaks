package net.minecraft.src.betatweaks;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.MathHelper;

public class MinecartBoosters {

	public static void onTick(Minecraft mc) {
		for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
			Entity entity = (Entity) mc.theWorld.loadedEntityList.get(i);
			if (entity instanceof EntityMinecart) {
				@SuppressWarnings("unchecked")
				List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(entity,
						entity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				if (list != null && list.size() > 0) {
					for (int j1 = 0; j1 < list.size(); j1++) {
						Entity entity2 = (Entity) list.get(j1);
						if (entity2 != entity2.riddenByEntity && entity2.canBePushed()
								&& (entity2 instanceof EntityMinecart)) {
							double d = entity2.posX - entity.posX;
							double d1 = entity2.posZ - entity.posZ;
							double d2 = d * d + d1 * d1;
							if (d2 >= 9.9999997473787516E-005D) {
								d2 = MathHelper.sqrt_double(d2);
								d /= d2;
								d1 /= d2;
								double d3 = 1.0D / d2;
								if (d3 > 1.0D) {
									d3 = 1.0D;
								}
								d *= d3;
								d1 *= d3;
								d *= 0.10000000149011612D;
								d1 *= 0.10000000149011612D;
								d *= 1.0F - entity.entityCollisionReduction;
								d1 *= 1.0F - entity.entityCollisionReduction;
								d *= 0.5D;
								d1 *= 0.5D;
								entity.motionX *= 0.20000000298023224D;
								entity.motionZ *= 0.20000000298023224D;
								entity.addVelocity(entity2.motionX - d, 0.0D, entity2.motionZ - d1);
								entity2.motionX *= 0.69999998807907104D;
								entity2.motionZ *= 0.69999998807907104D;
							}
						}
					}
				}
			}
		}
	}
}
