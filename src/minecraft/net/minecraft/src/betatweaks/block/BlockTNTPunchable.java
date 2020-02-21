package net.minecraft.src.betatweaks.block;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.Config;

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

    @Override
    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l)
    {
        if(world.multiplayerWorld)
        {
            return;
        }
        if(!Config.getInstance().lightTNTwithFist.isEnabled() && (l & 1) == 0)
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
