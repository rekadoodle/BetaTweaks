package net.minecraft.src;

public class BlockTallGrassHidden extends BlockTallGrass {

	protected BlockTallGrassHidden(Block block)
    {
        super(block.blockID, 0);
        setBlockName("tallgrass");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundGrassFootstep);
    }
 
    public int getRenderType()
    {
        return 0;
    }
    
}
