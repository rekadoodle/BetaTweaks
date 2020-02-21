package betatweaks.block;

import net.minecraft.src.Block;
import net.minecraft.src.BlockDeadBush;

public class BlockDeadBushHidden extends BlockDeadBush {

	public BlockDeadBushHidden()
    {
        super(Block.deadBush.blockID, 0);
        setBlockName("deadbush");
        setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        setStepSound(soundSandFootstep);
    }
 
    public int getRenderType()
    {
        return 0;
    }
    
}
