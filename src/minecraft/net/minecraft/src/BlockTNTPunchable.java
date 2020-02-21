// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

// Referenced classes of package net.minecraft.src:
//            Block, Material, World, EntityTNTPrimed, 
//            ItemStack, EntityPlayer, Item

public class BlockTNTPunchable extends BlockTNT
{

    public BlockTNTPunchable()
    {
    	super(Block.tnt.blockID, Block.tnt.blockIndexInTexture);
    	setHardness(Block.tnt.getHardness());
    	setStepSound(soundGrassFootstep);
    	setBlockName(Block.tnt.getBlockName());
    	
    	try {
    		Field x = mod_BetaTweaks.getObfuscatedPrivateField(Block.class, new String[] {"tnt", "q"});
    		if (x != null) {
    			x.setAccessible(true);
                
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
               
                x.set(null, this);
    		}
        } catch (Exception e) {e.printStackTrace();}
        setTickOnLoad(true);
    }

    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l)
    {
        if(world.multiplayerWorld)
        {
            return;
        }
        if(!mod_BetaTweaks.optionsGameplayLightTNTwithFist && (l & 1) == 0)
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
