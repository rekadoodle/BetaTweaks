package net.minecraft.src.betatweaks;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class HideBlocks {
	
	private static final Block LONG_GRASS_VANILLA = Block.tallGrass;
    private static final Block LONG_GRASS_HIDDEN = new BlockTallGrass(Utils.clearBlockID(LONG_GRASS_VANILLA), 0) {
    	
    	@Override
        public int getRenderType()
        {
            return 0;
        }
    	
    };
    
    static {
    	LONG_GRASS_HIDDEN.setBlockName("tallgrass");
    	LONG_GRASS_HIDDEN.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    	LONG_GRASS_HIDDEN.stepSound = Block.soundGrassFootstep;
    }
    
    private static final Block DEAD_BUSH_VANILLA = Block.deadBush;
    private static final Block DEAD_BUSH_HIDDEN = new BlockDeadBush(Utils.clearBlockID(LONG_GRASS_VANILLA), 0) {
    	
    	@Override
        public int getRenderType()
        {
            return 0;
        }
    	
    };
    
    static {
    	DEAD_BUSH_HIDDEN.setBlockName("deadbush");
    	DEAD_BUSH_HIDDEN.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    	DEAD_BUSH_HIDDEN.stepSound = Block.soundSandFootstep;
    }
    
    private static boolean longGrassVisible = false;
    private static boolean deadBushVisible = false;
    
    public static void setLongGrassVisible(boolean isVisible) {
    	if(longGrassVisible != isVisible) {
    		longGrassVisible = isVisible;
        	Utils.replaceBlock(isVisible ? LONG_GRASS_VANILLA : LONG_GRASS_HIDDEN, Block.tallGrass);
    	}
    }
    
    public static void setDeadBushVisible(boolean isVisible) {
    	if(deadBushVisible != isVisible) {
    		deadBushVisible = isVisible;
        	Utils.replaceBlock(isVisible ? DEAD_BUSH_VANILLA : DEAD_BUSH_HIDDEN, Block.deadBush);
    	}
    }
    
    public static void onTick(Minecraft mc, boolean spWorld, boolean serverModEnabled) {
    	if(mc.thePlayer.getCurrentEquippedItem() != null
				&& mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE
				&& Mouse.isButtonDown(1)) {
    		
    		MovingObjectPosition hoveredObj = mc.objectMouseOver;
    		
    		int x = hoveredObj.blockX;
			int y = hoveredObj.blockY;
			int z = hoveredObj.blockZ;

			if (hoveredObj.sideHit == 0) {
				y--;
			}
			else if (hoveredObj.sideHit == 1) {
				y++;
			}
			else if (hoveredObj.sideHit == 2) {
				z--;
			}
			else if (hoveredObj.sideHit == 3) {
				z++;
			}
			else if (hoveredObj.sideHit == 4) {
				x--;
			}
			else if (hoveredObj.sideHit == 5) {
				x++;
			}
			
			Block hoveredBlock = Block.blocksList[mc.theWorld.getBlockId(x, y, z)];
			if (((hoveredBlock == Block.tallGrass && !longGrassVisible)
					|| (hoveredBlock == Block.deadBush && !deadBushVisible))) {
				if (serverModEnabled) {
					Utils.mpHandler.longGrassDestroyed(x, y, z);
				} 
				else if (!spWorld) {
					mc.playerController.clickBlock(x, y, z, 0);
				} 
				else {
					mc.theWorld.setBlock(x, y, z, 0);
				}
				mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
			}
    	}
    }
}
