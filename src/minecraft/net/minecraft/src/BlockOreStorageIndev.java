package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BlockOreStorageIndev extends BlockOreStorage {

	private Block thisBlock;

	public BlockOreStorageIndev(Block block, int field) {
		super(block.blockID, block.blockIndexInTexture);
		setHardness(block.getHardness());
        setBlockName(block.getBlockName().replaceFirst("tile.", ""));
		setResistance(10F);
		setStepSound(soundMetalFootstep);
		thisBlock = block;
        try {
            Field x = Block.class.getDeclaredFields()[field];
            x.setAccessible(true);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
           
            x.set(null, this);
        } catch (Exception e) {e.printStackTrace();}
        setTickOnLoad(true);
    }
    
    @Override
    public int getBlockTextureFromSide(int i)
    {
    	 if(!mod_BetaTweaks.optionsClientIndevStorageBlocks || i == 1)
         {
             return blockIndexInTexture;
         }
         if(i == 0)
         {
        	 return mod_BetaTweaks.getTexture(thisBlock, i);
         } else
         {
        	 return mod_BetaTweaks.getTexture(thisBlock, i);
         }
    }
    
}
