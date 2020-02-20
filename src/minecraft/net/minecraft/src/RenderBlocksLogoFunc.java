// func_1238_a reimplemented for use in mod_oldCustomLogo
// Has been tweaked slightly to allow metadata

package net.minecraft.src;

import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            RenderBlocks, Block, Tessellator

public class RenderBlocksLogoFunc extends RenderBlocks
{
	private Boolean custom;
	
    public RenderBlocksLogoFunc(Boolean x)
    {
    	custom = x;
    }

    public void func_1238_a(Block block, int i, float f)
    {
        int j = block.getRenderType();
       //GL11.glScalef(1F, 1F, 1.5F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        Tessellator tessellator = Tessellator.instance;
        float f1 = 1.0F;
        float f2 = 0.5F;
        float f3 = 0.8F;
        float f4 = 0.6F;
        
        if (custom) {
        	f2 = 1.0F;
        	f1 = 0.5F;
        	
        	f1 *= mod_BetaTweaks.logoLightMultiplier;
        	f2 *= mod_BetaTweaks.logoLightMultiplier;
        	f3 *= mod_BetaTweaks.logoLightMultiplier;
        	f4 *= mod_BetaTweaks.logoLightMultiplier;
        }
         
        tessellator.startDrawingQuads();
        
        tessellator.setColorRGBA_F(f1, f1, f1, f);
        renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, i));
        
        tessellator.setColorRGBA_F(f2, f2, f2, f);
        
        renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, i));
        tessellator.setColorRGBA_F(f3, f3, f3, f);
        renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, i));
        renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, i));
        
        tessellator.setColorRGBA_F(f4, f4, f4, f);
        renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, i));
        renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, i));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
