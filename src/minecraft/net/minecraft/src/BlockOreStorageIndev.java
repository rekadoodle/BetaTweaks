package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BlockOreStorageIndev extends BlockOreStorage {

	private int sideTexture;
	private int bottomTexture;
	
	public BlockOreStorageIndev(Block block, String[] fields, String sideTexture, String bottomTexture) {
		super(block.blockID, block.blockIndexInTexture);
		setHardness(block.getHardness());
        setBlockName(block.getBlockName().replaceFirst("tile.", ""));
		setResistance(10F);
		setStepSound(soundMetalFootstep);
        this.sideTexture = ModLoader.addOverride("/terrain.png", sideTexture);
        this.bottomTexture = ModLoader.addOverride("/terrain.png", bottomTexture);
        try {
        	Field field = mod_BetaTweaks.getObfuscatedPrivateField(Block.class, fields);
            if(field != null) {
            	field.setAccessible(true);
                
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
               
                field.set(null, this);
            }
        } 
        catch (Exception e) {e.printStackTrace();}
    }
    
    public int getBlockTextureFromSide(int i)
    {
    	 if(!mod_BetaTweaks.optionsClientIndevStorageBlocks || i == 1)
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
