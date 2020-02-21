package betatweaks.block;

import betatweaks.Utils;
import betatweaks.config.Config;
import net.minecraft.src.*;

public class BlockOreStorageIndev extends BlockOreStorage {

	private int sideTexture;
	private int bottomTexture;
	
	public BlockOreStorageIndev(Block block, String[] fields, String sideTexture, String bottomTexture) {
		super(Utils.clearBlockID(block), block.blockIndexInTexture);
		setHardness(block.getHardness());
        setBlockName(block.getBlockName().replaceFirst("tile.", ""));
		setResistance(10F);
		setStepSound(soundMetalFootstep);
        this.sideTexture = ModLoader.addOverride("/terrain.png", sideTexture);
        this.bottomTexture = ModLoader.addOverride("/terrain.png", bottomTexture);
        Utils.replaceBlock(this, fields);
    }
    
    public int getBlockTextureFromSide(int i)
    {
    	 if(!Config.getInstance().indevStorageBlocks.isEnabled() || i == 1)
         {
             return blockIndexInTexture;
         }
         if(i == 0)
         {
        	 return bottomTexture;
         } 
         else
         {
        	 return sideTexture;
         }
    }
    
}
