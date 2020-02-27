package net.minecraft.src.betatweaks.references.aether;

import java.util.Random;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.dummy.HandlerAether;

public class ConcreteHandler extends HandlerAether {
	
	private final boolean sheepuffExists;
	
	public ConcreteHandler() {
		this.sheepuffExists = Utils.classExists("EntitySheepuff");
	}

	@Override
	public void shearSheepuff(Entity entity) {
		if(!this.sheepuffExists) {
			return;
		}
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

	@Override
	public boolean simulatedWorldMenu() {
		return GuiMainMenu.mmactive;
	}

}
