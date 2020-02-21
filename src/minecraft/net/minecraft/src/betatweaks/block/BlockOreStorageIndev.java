package net.minecraft.src.betatweaks.block;

import java.lang.reflect.Field;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.Config;

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
    
	@Override
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
    
    public static void init() {
    	String resources = mod_BetaTweaks.resources;
		new BlockOreStorageIndev(Block.blockSteel, new String[] {"blockSteel", "aj"}, resources + "/steelSide.png", resources + "/steelBottom.png");
		new BlockOreStorageIndev(Block.blockGold, new String[] {"blockGold", "ai"}, resources + "/goldSide.png", resources + "/goldBottom.png");
		new BlockOreStorageIndev(Block.blockDiamond, new String[] {"blockDiamond", "ay"}, resources + "/diamondSide.png", resources + "/diamondBottom.png");
		
		ModLoader.RegisterAllTextureOverrides(Utils.mc.renderEngine);
		
		final Field blocksEffectiveAgainst = Utils.getField(ItemTool.class, "blocksEffectiveAgainst", "bk");
		if (blocksEffectiveAgainst != null) {
			for(Item item : Item.itemsList) {
				if(item instanceof ItemPickaxe) {
					try {
						Block blocks[] = (Block[])blocksEffectiveAgainst.get(item);
						for(int i = 0; i < blocks.length; i++) {
							if(blocks[i].blockID == Block.blockSteel.blockID) {
								blocks[i] = Block.blockSteel;
							}
							else if(blocks[i].blockID == Block.blockGold.blockID) {
								blocks[i] = Block.blockGold;
							}
							else if(blocks[i].blockID == Block.blockDiamond.blockID) {
								blocks[i] = Block.blockDiamond;
							}
						}
					} 
					catch (Exception e) { e.printStackTrace(); }
				}
			}
		}
		
	}
    
}
