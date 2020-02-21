package net.minecraft.src.betatweaks.block;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockTallGrass;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.Config;

public class BlockTallGrassHidden extends BlockTallGrass {

	public BlockTallGrassHidden()
    {
        super(Utils.clearBlockID(Block.tallGrass), 0);
        setBlockName("tallgrass");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundGrassFootstep);
    }
 
	@Override
    public int getRenderType()
    {
        return 0;
    }
    
    private static Block hiddenGrass;
    private static final Block vanillaGrass = Block.tallGrass;
    
    public static void setVisible(boolean isVisible) {
    	if(hiddenGrass == null && !isVisible) hiddenGrass = new BlockTallGrassHidden();
    	Utils.replaceBlock(isVisible ? vanillaGrass : hiddenGrass, "tallGrass", "Y");
    }
    
    public static void onTick(Minecraft mc, boolean spWorld, boolean serverModEnabled) {
    	if(mc.thePlayer.getCurrentEquippedItem() != null
				&& mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE
				&& Mouse.isButtonDown(1)) {
    		int x = mc.objectMouseOver.blockX;
			int y = mc.objectMouseOver.blockY;
			int z = mc.objectMouseOver.blockZ;

			if (mc.objectMouseOver.sideHit == 0) {
				y--;
			}
			if (mc.objectMouseOver.sideHit == 1) {
				y++;
			}
			if (mc.objectMouseOver.sideHit == 2) {
				z--;
			}
			if (mc.objectMouseOver.sideHit == 3) {
				z++;
			}
			if (mc.objectMouseOver.sideHit == 4) {
				x--;
			}
			if (mc.objectMouseOver.sideHit == 5) {
				x++;
			}
			int b = mc.theWorld.getBlockId(x, y, z);
			if (((Block.blocksList[b] == Block.tallGrass && Config.getInstance().hideLongGrass.isEnabled())
					|| (Block.blocksList[b] == Block.deadBush && Config.getInstance().hideDeadBush.isEnabled()))) {
				if (serverModEnabled) {
					Utils.mpHandler.longGrassDestroyed(x, y, z);
				} else if (!spWorld) {
					mc.playerController.clickBlock(x, y, z, 0);
				} else {
					mc.theWorld.setBlock(x, y, z, 0);
				}
				mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
			}
    	}
    }
    
}
