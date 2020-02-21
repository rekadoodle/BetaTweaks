package net.minecraft.src.betatweaks.block;

import net.minecraft.src.Block;
import net.minecraft.src.BlockDeadBush;
import net.minecraft.src.betatweaks.Utils;

public class BlockDeadBushHidden extends BlockDeadBush {

	public BlockDeadBushHidden()
    {
        super(Utils.clearBlockID(Block.deadBush), 0);
        setBlockName("deadbush");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundSandFootstep);
    }
 
	@Override
    public int getRenderType()
    {
        return 0;
    }
    
	private static Block hiddenBush;
    private static final Block vanillaBush = Block.deadBush;
    
    public static void setVisible(boolean isVisible) {
    	if(hiddenBush == null && !isVisible) hiddenBush = new BlockDeadBushHidden(); 
    	Utils.replaceBlock(isVisible ? vanillaBush : hiddenBush, "deadBush", "Z");
    }
    
}
