package net.minecraft.server.betatweaks;

import net.minecraft.server.betatweaks.Utils;
import net.minecraft.server.betatweaks.config.Config;
import net.minecraft.server.*;

public class BlockTNTPunchable extends BlockTNT
{

    public BlockTNTPunchable()
    {
    	super(Utils.clearBlockID(Block.TNT), Block.TNT.textureId);
    	c(Block.TNT.j());
    	a(g);
    	a(Block.TNT.l());
    }

    public void postBreak(World world, int i, int j, int k, int l)
    {
        if(world.isStatic)
        {
            return;
        }
        if(!Config.INSTANCE.lightTNTwithFist.isEnabled() && (l & 1) == 0)
        {
            a(world, i, j, k, new ItemStack(Block.TNT.id, 1, 0));
        } else
        {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F);
            world.addEntity(entitytntprimed);
            world.makeSound(entitytntprimed, "random.fuse", 1.0F, 1.0F);
        }
    }

    
}
