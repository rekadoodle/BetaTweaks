package betatweaks.block;

import betatweaks.Config;
import betatweaks.Utils;
import net.minecraft.src.*;

public class BlockTNTPunchable extends BlockTNT
{

    public BlockTNTPunchable()
    {
    	super(Utils.clearBlockID(Block.tnt), Block.tnt.blockIndexInTexture);
    	setHardness(Block.tnt.getHardness());
    	setStepSound(soundGrassFootstep);
    	setBlockName(Block.tnt.getBlockName().replaceFirst("tile.", ""));
    	Utils.replaceBlock(this, "tnt", "an");
    }

    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l)
    {
        if(world.multiplayerWorld)
        {
            return;
        }
        if(!Config.gameplayLightTNTwithFist && (l & 1) == 0)
        {
            dropBlockAsItem_do(world, i, j, k, new ItemStack(Block.tnt.blockID, 1, 0));
        } else
        {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F);
            world.entityJoinedWorld(entitytntprimed);
            world.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
        }
    }

    
}
