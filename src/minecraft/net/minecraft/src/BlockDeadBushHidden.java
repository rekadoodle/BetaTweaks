package net.minecraft.src;

public class BlockDeadBushHidden extends BlockDeadBush {

	protected BlockDeadBushHidden(Block block)
    {
        super(block.blockID, 0);
        setBlockName("deadbush");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundSandFootstep);
    }
 
    public int getRenderType()
    {
        return 0;
    }
    
}
