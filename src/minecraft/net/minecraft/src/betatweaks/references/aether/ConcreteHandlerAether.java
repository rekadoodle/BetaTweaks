package net.minecraft.src.betatweaks.references.aether;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntitySheepuff;
import net.minecraft.src.ItemStack;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.references.HandlerAether;

public class ConcreteHandlerAether extends HandlerAether {

	@Override
	public void shearSheepuff(Entity entity) {
		if(entity instanceof EntitySheepuff) {
			EntitySheepuff sheep = (EntitySheepuff) entity;
			
			if (!Utils.mc.theWorld.multiplayerWorld && sheep.beenAttacked) {
				if (!sheep.getSheared()) {

					sheep.setSheared(true);
					Random rand = new Random();
					int i = 2 + rand.nextInt(3);
					for (int j = 0; j < i; j++) {
						EntityItem wool = sheep.entityDropItem(
								new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
						wool.motionY += rand.nextFloat() * 0.05F;
						wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}
				}
			}
		}
		
		
	}

}
