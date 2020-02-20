// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import org.lwjgl.Sys;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, GuiSmallButton, TexturePackList, 
//            GuiTexturePackSlot, GuiButton, RenderEngine, FontRenderer

public class GuiTexturePacksIngame extends GuiTexturePacks
{

    public GuiTexturePacksIngame(GuiScreen guiscreen)
    {
    	super(guiscreen);
        fileLocation = "";
        guiScreen = guiscreen;
        initialTexturePack = ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack;
        
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 5)
        {
            Sys.openURL((new StringBuilder()).append("file://").append(fileLocation).toString());
        } else
        if(guibutton.id == 6)
        {
        	
			if (ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack != initialTexturePack)
        	{
        		mc.renderEngine.refreshTextures();
        		mc.renderGlobal.loadRenderers();
        	}
            mc.displayGuiScreen(guiScreen);
            
        } else
        {
            guiTexturePackSlot.actionPerformed(guibutton);
            
        }
    }

    protected void keyTyped(char c, int i)
    {
        if(i == 1)
        {
        	if (ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack != initialTexturePack)
        	{
        		mc.renderEngine.refreshTextures();
        		mc.renderGlobal.loadRenderers();
        	}
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }
   
    
    protected GuiScreen guiScreen;
    private String fileLocation;
    private GuiTexturePacks guiTexturePackSlot;
    private static TexturePackBase initialTexturePack;
}
