package betatweaks.block;

import net.minecraft.src.Block;
import net.minecraft.src.BlockTallGrass;

public class BlockTallGrassHidden extends BlockTallGrass {

	public BlockTallGrassHidden()
    {
        super(Block.tallGrass.blockID, 0);
        setBlockName("tallgrass");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundGrassFootstep);
    }
 
    public int getRenderType()
    {
        return 0;
    }
    
}
