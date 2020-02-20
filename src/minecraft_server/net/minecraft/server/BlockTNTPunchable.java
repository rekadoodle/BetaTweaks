// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.server;

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
    	super(Block.TNT.id, Block.TNT.textureId);
    	c(Block.TNT.j());
    	a(g);
    	a(Block.TNT.l());
    	
    	try {
    		Field x = Block.class.getDeclaredFields()[64];
            x.setAccessible(true);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
           
            x.set(null, this);
        } catch (Exception e) {e.printStackTrace();}
        a(true);
    }

    public void postBreak(World world, int i, int j, int k, int l)
    {
        if(world.isStatic)
        {
            return;
        }
        if(!BetaTweaksMP.optionsGameplayLightTNTwithFist && (l & 1) == 0)
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
