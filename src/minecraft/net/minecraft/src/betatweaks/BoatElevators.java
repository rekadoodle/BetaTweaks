package net.minecraft.src.betatweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.Material;

public class BoatElevators {

	public static void onTick(Minecraft mc) {
		for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
			Entity entity = (Entity) mc.theWorld.loadedEntityList.get(i);
			if (entity instanceof EntityBoat) {
				int q = 5;
				double d = 0.0D;
				for (int j = 0; j < q; j++) {
					double d5 = (entity.boundingBox.minY
							+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 0)) / (double) i)
							- 0.125D;
					double d9 = (entity.boundingBox.minY
							+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 1)) / (double) i)
							- 0.125D;
					AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(entity.boundingBox.minX, d5,
							entity.boundingBox.minZ, entity.boundingBox.maxX, d9, entity.boundingBox.maxZ);
					if (mc.theWorld.isAABBInMaterial(axisalignedbb, Material.water)) {
						d += 1.0D / (double) q;
					}
				}

				if (d >= 1.0D) {
					double d3 = d * 2D - 1.0D;
					entity.moveEntity(0, (0.039999999105930328D * d3) / 2.5D - 0.0070000002160668373D, 0);
				}
			}
		}
	}
}
