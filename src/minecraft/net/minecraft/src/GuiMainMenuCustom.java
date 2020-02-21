// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.src.mod_BetaTweaks.LogoState;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, mod_Test1a, LogoEffectRandomizer, StringTranslate, 
//            GuiButton, GuiOptions, GuiSelectWorld, GuiMultiplayer, 
//            GuiTexturePacks, Tessellator, RenderEngine, MathHelper, 
//            FontRenderer, ScaledResolution, RenderBlocksLogoFunc

public class GuiMainMenuCustom extends GuiMainMenu
{

    private int viewportTexture;
	private boolean undrawn2 = true;
    private static final String[] field_73978_o = new String[] {"/title/bg/panorama/panorama0.png", "/title/bg/panorama/panorama1.png", "/title/bg/panorama/panorama2.png", "/title/bg/panorama/panorama3.png", "/title/bg/panorama/panorama4.png", "/title/bg/panorama/panorama5.png"};
    
	public GuiMainMenuCustom()
    {
		updateCounter = 0;
		minecraftLogo = getCustomLogo();
        splashText = "missingno";
        try
        {
            ArrayList arraylist = new ArrayList();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((net.minecraft.src.GuiMainMenuCustom.class).getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8")));
            String s = "";
            do
            {
                String s1;
                if((s1 = bufferedreader.readLine()) == null)
                {
                    break;
                }
                s1 = s1.trim();
                if(s1.length() > 0)
                {
                    arraylist.add(s1);
                }
            } while(true);
            splashText = (String)arraylist.get(rand.nextInt(arraylist.size()));
        }
        catch(Exception exception) { }
    }
	
	 
	
	
    public void updateScreen()
    {
        updateCounter++;
        if(logoEffects != null)
        {
            for(int i = 0; i < logoEffects.length; i++)
            {
                for(int j = 0; j < logoEffects[i].length; j++)
                {
                    logoEffects[i][j].func_875_a();
                }

            }

        }
    }

    protected void keyTyped(char c, int i)
    {
    	if(i == 1)
        {
    		logoEffects = null;
            //mc.displayGuiScreen(new GuiMainMenuCustom(updateCounter));
    	}
    	else {
    		super.keyTyped(c, i);
    	}
    }

    protected void actionPerformed(GuiButton guibutton) {
    	undrawn = true;
    	super.actionPerformed(guibutton);
    }

  

    
    public void drawScreen(int i, int j, float f)
    {
    	
    	if (mod_BetaTweaks.optionsClientPanoramaEnabled) {
    		if (undrawn2) {
    			undrawn2 = false;
        		this.viewportTexture = this.mc.renderEngine.allocateAndSetupTexture(new BufferedImage(256, 256, 2));
    		}
    		renderSkybox(i, j, f);
    	} 
    	else {
    		drawDefaultBackground();
    	}
    	
    	
        Tessellator tessellator = Tessellator.instance;
        if (mod_BetaTweaks.optionsClientLogo != LogoState.STANDARD)  {
        	if (undrawn && mod_BetaTweaks.optionsClientLogo == LogoState.CUSTOM && updateCounter > 1) {
        		mc.renderEngine.updateDynamicTextures();
        		undrawn = false;
        	}
        	
        	
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        if (mod_BetaTweaks.optionsClientLogo == LogoState.CUSTOM) {
        	drawLogo(f);
        }
        else {
        	drawVanillaLogo(f);
        }
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/logo.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else {
        	char c = '\u0112';
            int k = width / 2 - c / 2;
            byte byte0 = 30;
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/title/mclogo.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(k + 0, byte0 + 0, 0, 0, 155, 44);
            drawTexturedModalRect(k + 155, byte0 + 0, 0, 45, 155, 44);
        }
        tessellator.setColorOpaque_I(0xffffff);
        GL11.glPushMatrix();
        if (mod_BetaTweaks.optionsClientLogo != LogoState.CUSTOM) {
        	GL11.glTranslatef(width / 2 + 90, (float)(70), 0.0F);
        }
        else {
        	GL11.glTranslatef(width / 2 + 90 + mod_BetaTweaks.logoSplashTextOffsetX, (float)(70) - mod_BetaTweaks.logoSplashTextOffsetY, 0.0F);
        }
        GL11.glRotatef(-20F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - MathHelper.abs(MathHelper.sin(((float)(System.currentTimeMillis() % 1000L) / 1000F) * 3.141593F * 2.0F) * 0.1F);
        f1 = (f1 * 100F) / (float)(fontRenderer.getStringWidth(splashText) + 32);
        GL11.glScalef(f1, f1, f1);
        if (mod_BetaTweaks.optionsClientLogo != LogoState.CUSTOM || mod_BetaTweaks.logoSplashTextEnabled) {
        	drawCenteredString(fontRenderer, splashText, 0, -8, 0xffff00);
        }
        GL11.glPopMatrix();
        if (mod_BetaTweaks.optionsClientPanoramaEnabled) {
        	drawString(this.fontRenderer, "Minecraft Beta 1.7.3", 2, this.height - 10, 16777215);
        }
        else {
        	drawString(fontRenderer, "Minecraft Beta 1.7.3", 2, 2, 0x505050);
        }
        String s = "Copyright Mojang AB. Do not distribute.";
        drawString(fontRenderer, s, width - fontRenderer.getStringWidth(s) - 2, height - 10, 0xffffff);
        
        
        for(int k = 0; k < controlList.size(); k++)
        {
            GuiButton guibutton = (GuiButton)controlList.get(k);
            guibutton.drawButton(mc, i, j);
        }
    }

    private void renderSkybox(int i, int j, float f) {
    	GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(i, j, f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        float var5 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
        float var6 = (float)this.height * var5 / 256.0F;
        float var7 = (float)this.width * var5 / 256.0F;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int var8 = this.width;
        int var9 = this.height;
        var4.addVertexWithUV(0.0D, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F + var7));
        var4.addVertexWithUV((double)var8, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F - var7));
        var4.addVertexWithUV((double)var8, 0.0D, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F - var7));
        var4.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F + var7));
        var4.draw();
		
	}

	private void rotateAndBlurSkybox(float f) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.viewportTexture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColorMask(true, true, true, false);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        byte var3 = 3;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(var4 + 1));
            int var5 = this.width;
            int var6 = this.height;
            float var7 = (float)(var4 - var3 / 2) / 256.0F;
            var2.addVertexWithUV((double)var5, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 0.0D);
            var2.addVertexWithUV((double)var5, 0.0D, (double)this.zLevel, (double)(1.0F + var7), 0.0D);
            var2.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + var7), 1.0D);
            var2.addVertexWithUV(0.0D, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 1.0D);
        }

        var2.draw();
        GL11.glColorMask(true, true, true, true);
		
	}

	private void drawPanorama(int i, int j, float f) {
		Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        byte var5 = 8;

        for (int var6 = 0; var6 < var5 * var5; ++var6)
        {
            GL11.glPushMatrix();
            float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 64.0F;
            float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 64.0F;
            float var9 = 0.0F;
            GL11.glTranslatef(var7, var8, var9);
            GL11.glRotatef(MathHelper.sin(((float)this.updateCounter + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-((float)this.updateCounter + f) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int var10 = 0; var10 < 6; ++var10)
            {
                GL11.glPushMatrix();

                if (var10 == 1)
                {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 3)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 4)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var10 == 5)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture(field_73978_o[var10]));
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                float var11 = 0.0F;
                var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + var11), (double)(0.0F + var11));
                var4.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - var11), (double)(0.0F + var11));
                var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - var11), (double)(1.0F - var11));
                var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + var11), (double)(1.0F - var11));
                var4.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        var4.setTranslationD(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		
	}

	private void drawLogo(float f)
    {
    	if (customLogoConfigUpdated())
    	{
    		readCustomLogoConfig();
    		//mc.displayGuiScreen(new GuiMainMenuCustom(updateCounter));
    	}
    	minecraftLogo = getCustomLogo();
        
        if(logoEffects == null)
        {
        	
            logoEffects = new LogoEffectRandomizer[getCustomLogoWidth()][minecraftLogo.length];
            for(int i = 0; i < logoEffects.length; i++)
            {
                for(int j = 0; j < logoEffects[i].length; j++)
                {
                    logoEffects[i][j] = new LogoEffectRandomizer(i, j, rand);
                }

            }

        }
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int k = 120 * scaledresolution.scaleFactor;
        GLU.gluPerspective(70F, (float)mc.displayWidth / (float)k, 0.05F, 100F);
        GL11.glViewport(0, mc.displayHeight - k, mc.displayWidth, k);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        GL11.glCullFace(1029 /*GL_BACK*/);
        GL11.glDepthMask(true);
        RenderBlocksLogoFunc renderblockslogofunc = new RenderBlocksLogoFunc(true);
        for(int l = 0; l < 3; l++)
        {
        	
            GL11.glPushMatrix();
            GL11.glTranslatef(0.4F, 0.6F, -13F);
            if(l == 0)
            {
                GL11.glClear(256);
                GL11.glTranslatef(0.0F, -0.2F, 0.0F);
                GL11.glScalef(0.98F, 1.0F, 1.0F);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
            }
            if(l == 1)
            {
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glClear(256);
            }
            if(l == 2)
            {
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(768, 1);
            }
            
            GL11.glRotatef(-mod_BetaTweaks.logoAxisTilt, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(0.89F, 1.0F, 0.4F);
            GL11.glTranslatef((float)(-minecraftLogo[0].length()) * 0.4865F + mod_BetaTweaks.logoOffsetX, (float)(-minecraftLogo.length) * 0.5F - mod_BetaTweaks.logoOffsetY, mod_BetaTweaks.logoScale);
            
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(l == 0)
            {
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/title/black.png"));
            }
            for(int i1 = 0; i1 < minecraftLogo.length; i1++)
            {
                for(int j1 = 0; j1 < minecraftLogo[i1].length(); j1++)
                {
                    char c = minecraftLogo[i1].charAt(j1);
                    if(c == ' ')
                    {
                        continue;
                    }
                    GL11.glPushMatrix();
                    if(j1 < logoEffects.length && i1 < logoEffects[j1].length) {
                    LogoEffectRandomizer logoeffectrandomizer = logoEffects[j1][i1];
                    float f1 = (float)(logoeffectrandomizer.field_1311_b + (logoeffectrandomizer.field_1312_a - logoeffectrandomizer.field_1311_b) * (double)f);
                    float f2 = 1.0F;
                    float f3 = 1.0F;
                    float f4 = 0.0F;
                    if(l == 0)
                    {
                        f2 = f1 * 0.04F + 1.0F;
                        f3 = 1.0F / f2;
                        f1 = 0.0F;
                    }
                    GL11.glTranslatef(j1, minecraftLogo.length - 1 - i1, f1);
                    GL11.glScalef(f2, f2, f2);
                    GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
                    renderblockslogofunc.func_1238_a(getCustomLogoBlock(i1, j1), getCustomLogoBlockMetaData(i1, j1), f3);
                    }
                    GL11.glPopMatrix();
                    
                }

            }

            GL11.glPopMatrix();
        }

        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glPopMatrix();
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
    }

	private void drawVanillaLogo(float f)
    {
    	if(logoEffects == null)
        {
            logoEffects = new LogoEffectRandomizer[minecraftLogoVanilla[0].length()][minecraftLogoVanilla.length];
            for(int i = 0; i < logoEffects.length; i++)
            {
                for(int j = 0; j < logoEffects[i].length; j++)
                {
                    logoEffects[i][j] = new LogoEffectRandomizer(i, j, rand);
                }

            }

        }
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int k = 120 * scaledresolution.scaleFactor;
        GLU.gluPerspective(70F, (float)mc.displayWidth / (float)k, 0.05F, 100F);
        GL11.glViewport(0, mc.displayHeight - k, mc.displayWidth, k);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        GL11.glCullFace(1029 /*GL_BACK*/);
        GL11.glDepthMask(true);
        RenderBlocksLogoFunc renderblockslogofunc = new RenderBlocksLogoFunc(false);
        for(int l = 0; l < 3; l++)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.4F, 0.6F, -13F);
            if(l == 0)
            {
                GL11.glClear(256);
                GL11.glTranslatef(0.0F, -0.4F, 0.0F);
                GL11.glScalef(0.98F, 1.0F, 1.0F);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
            }
            if(l == 1)
            {
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glClear(256);
            }
            if(l == 2)
            {
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(768, 1);
            }
            GL11.glScalef(1.0F, -1F, 1.0F);
            GL11.glRotatef(15F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(0.89F, 1.0F, 0.4F);
            GL11.glTranslatef((float)(-minecraftLogoVanilla[0].length()) * 0.4865F, (float)(-minecraftLogoVanilla.length) * 0.5F, 0.0F);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(l == 0)
            {
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/title/black.png"));
            }
            for(int i1 = 0; i1 < minecraftLogoVanilla.length; i1++)
            {
                for(int j1 = 0; j1 < minecraftLogoVanilla[i1].length(); j1++)
                {
                    char c = minecraftLogoVanilla[i1].charAt(j1);
                    if(c == ' ')
                    {
                        continue;
                    }
                    GL11.glPushMatrix();
                    LogoEffectRandomizer logoeffectrandomizer = logoEffects[j1][i1];
                    float f1 = (float)(logoeffectrandomizer.field_1311_b + (logoeffectrandomizer.field_1312_a - logoeffectrandomizer.field_1311_b) * (double)f);
                    float f2 = 1.0F;
                    float f3 = 1.0F;
                    float f4 = 0.0F;
                    if(l == 0)
                    {
                        f2 = f1 * 0.04F + 1.0F;
                        f3 = 1.0F / f2;
                        f1 = 0.0F;
                    }
                    GL11.glTranslatef(j1, i1, f1);
                    GL11.glScalef(f2, f2, f2);
                    GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
                    renderblockslogofunc.func_1238_a(Block.stone, 0, f3);
                    GL11.glPopMatrix();
                }

            }

            GL11.glPopMatrix();
        }

        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glPopMatrix();
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
    }
    
	public static String[] getCustomLogo() {
		String as[] = new String[image.size()];
		for (int i = 0; i < image.size(); i++) {
			as[i] = image.get(i);
		}

		return as;
	}

	public static int getCustomLogoWidth() {
		return maxLineLength;
	}

	public static Block getCustomLogoBlock(int i, int j) {
		for (int k = 0; k < blockID.size(); k++) {
			if (image.get(i).charAt(j) == chars.get(k)) {
				if (blockID.get(k) < 255) {
					return Block.blocksList[blockID.get(k)];
				}
			}
		}
		return Block.torchWood;
	}

	public static int getCustomLogoBlockMetaData(int i, int j) {
		for (int k = 0; k < blockID.size(); k++) {
			if (image.get(i).charAt(j) == chars.get(k)) {
				return metaData.get(k);
			}
		}
		return 0;
	}

	public static Boolean customLogoConfigUpdated() {
		if (resetLogo) {
			resetLogo = false;
			return true;
		}
		if (configLogoFile.lastModified() != timeStamp) {
			timeStamp = configLogoFile.lastModified();
			if (!configLogoFile.exists()) {
				writeCustomLogoConfig();
			}
			return true;
		}
		return false;
	}

	public static void readCustomLogoConfig() {
		maxLineLength = 0;
		image.clear();
		chars.clear();
		blockID.clear();
		metaData.clear();

		try {
			BufferedReader configReader = new BufferedReader(new FileReader(configLogoFile));
			String s;
			Boolean readImage = false;
			Boolean readOptions = false;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue;
				} // Ignore comments
				else if (readOptions) {
					if (s.contains("=")) {
						String as[] = s.split("=");
						Field f1 = mod_BetaTweaks.class.getField("logo" + (as[0]));

						if (f1.getType() == float.class) {
							f1.set(null, Float.parseFloat(as[1]));
						}
						if (f1.getType() == Boolean.class) {
							f1.set(null, Boolean.parseBoolean(as[1]));
						}
					}
				}
				else if (readImage) {
					if (s.equals("#")) {
						readOptions = true;
					}
					else {
						image.add(s);
						if (s.length() > maxLineLength) {
							maxLineLength = s.length();
						}
					}
				} else if (s.contains("=")) {
					String as[] = s.split("=");
					chars.add(as[0].charAt(0));
					if (as[1].contains(":")) {
						String as1[] = as[1].split(":");
						blockID.add(Integer.parseInt(as1[0]));
						metaData.add(Integer.parseInt(as1[1]));
					} else if (as[1].contains("-")) {
						String as1[] = as[1].split("-");
						blockID.add(Integer.parseInt(as1[0]));
						metaData.add(Integer.parseInt(as1[1]));
					} else {
						blockID.add(Integer.parseInt(as[1]));
						metaData.add(0);
					}
				} else if (s.equals("#")) {
					readImage = true;
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
			
		}
	}

	public static void writeCustomLogoConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configLogoFile));
			configWriter.write(				   "// Config file for customising the animated logo."
					+ System.getProperty("line.separator") + "// NOTE: The logo will automatically update when this file is saved but you may need to reset it (ESC) for all the blocks to show."
					+ System.getProperty("line.separator") + "// "
					+ System.getProperty("line.separator") + "// To customise the logo, first create a key below with block IDs and characters to match the ascii."
					+ System.getProperty("line.separator") + "// This X=1 means that any block denoted with an X in the image section will be a stone block in the main menu."
					+ System.getProperty("line.separator") + "X=35:15"
					+ System.getProperty("line.separator") + "*=82"
					+ System.getProperty("line.separator") + "R=35:10"
					+ System.getProperty("line.separator") + "O=35:1"
					+ System.getProperty("line.separator") + "Y=35:4"
					+ System.getProperty("line.separator") + "G=35:5"
					+ System.getProperty("line.separator") + "C=35:3"
					+ System.getProperty("line.separator") + "B=35:11"
					+ System.getProperty("line.separator") + "W=80"
					+ System.getProperty("line.separator") + "P=35:6"
					+ System.getProperty("line.separator") + "T=12"
					+ System.getProperty("line.separator") + "M=1"
					+ System.getProperty("line.separator") + "// You can use blocks with metadata with ':' or '-' so for example: Y=35-6 would attribute Y with pink wool."
					+ System.getProperty("line.separator") + "// "
					+ System.getProperty("line.separator") + "#"
					+ System.getProperty("line.separator") + "// Below this # symbol is where the logo art is recorded."
					+ System.getProperty("line.separator") + "// If you want to use a blank line you must put at least 1 space in."
					+ System.getProperty("line.separator") + "// If you use a symbol below that has not been assigned a block, it will give the torch block (which does not look good)."
					+ System.getProperty("line.separator") + "// "
					+ System.getProperty("line.separator") + "        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR    "
					+ System.getProperty("line.separator") + "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRXWWWWWWWWWWWWWWWWWX"
					+ System.getProperty("line.separator") + "RRRRRRRROOOOOOOORRRRRRRROOOOOOOORRMMMMMOOMMMMMMMMMMMMMRMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOOOORRRRRRXWWWPPPPPPPPPPPPPWWWX"
					+ System.getProperty("line.separator") + "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOMTTTMOOMTTTMTTTMTTTMRMTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMOOOOOOOOOOXWWPPPPPPPRPPXXPPPWWX XXX"
					+ System.getProperty("line.separator") + "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOMTTTMMMMTTTMTTTMTTTMMMTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMOOOOOOOOOOXWPPRPPPPPPPX**XRPPWXX**X"
					+ System.getProperty("line.separator") + "OOOOOOOOYYYYYYYYOOOOOOOOYYYYYYYYOOMTTTTMMTTTTMTTTMTTTTMMTTTMTTTMMMMMTTTMMMMMTTTTTTTMTTMTMTTMTTTMMMMMMMTTTMMMYYYYOOOOOOXWPPPPPPPPPPX***PPPWX***X"
					+ System.getProperty("line.separator") + "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYMTTTTMMTTTTMTTTMTTTTMMTTTMTTTMMMMMTTTMYYYMTTTMTTTMTTTMTTTMTTTMMMMYYMTTTMYYYYYYYYXYYYXWPPPPPPPRPPX***XXXX****X"
					+ System.getProperty("line.separator") + "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYMTTTTTTTTTTMTTTMTTTTTTTTTMTTTTTTMMTTTMYYYMTTTMTTTMTTMMMTTMTTTTTTMYYMTTTMYYYYYYYX*XYYXWPPPPPPPPPPX***********X"
					+ System.getProperty("line.separator") + "YYYYYYYYGGGGGGGGYYYYYYYYGGGGGGGGYYMTTTTTTTTTTMTTTMTTTTTTTTTMTTTTTTMMTTTMGGGMTTTTTMMMTTMTMTTMTTTTTTMYYMTTTMGGGGGGYX*XXXXWPPPPPPPPPX*************X"
					+ System.getProperty("line.separator") + "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMTTTMTTMTTTMTTTMTTTTTTTTTMTTTMMMMMTTTMGGGMTTTTTTTMTTTTTTTMTTTMMMMGGMTTTMGGGGGGGG****XWPPPPPPPPRX***WX****WX**X"
					+ System.getProperty("line.separator") + "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMTTTMMMMTTTMTTTMTTTMMTTTTMTTTMMMMMTTTMGGGMTTTMTTTMTTTMTTTMTTTMGGGGGMTTTMGGGGGGGGXX**XWPRPPPPPPPX***XX**X*XX**X"
					+ System.getProperty("line.separator") + "GGGGGGGGCCCCCCCCGGGGGGGGCCCCCCCCGGMTTTMCCMTTTMTTTMTTTMMTTTTMTTTMMMMMTTTMMMMMTTTMTTTMTTTMTTTMTTTMGGGGGMTTTMCCCCCCGGGGXXXWPPPPPPPPPX*PP********PPX"
					+ System.getProperty("line.separator") + "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCMTTTMCCMTTTMTTTMTTTMMMTTTMTTTTTTTMTTTTTTTMTTTMTTTMTTTMTTTMTTTMCCCCCMTTTMCCCCCCCCCCCCXWWPPPPPRPPPX***XXXXXX**X"
					+ System.getProperty("line.separator") + "CCCCCCCCBBBBBBBBCCCCCCCCBBBBBBBBCCMTTTMBBMTTTMTTTMTTTMCMTTTMTTTTTTTMTTTTTTTMTTTMTTTMTTTMTTTMTTTMCCCCCMTTTMBBBBBBCWCCCXXWWWPPPPPPPPXX*********X"
					+ System.getProperty("line.separator") + "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBMMMMMBBMMMMMMMMMMMMMCMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMBBBBBMMMMMBBBBBBBBBWX*XXWWWWWWWWWWWXXXXXXXXXX"
					+ System.getProperty("line.separator") + "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBX***XXXXXXXXXXXXXXXXXX*X"
					+ System.getProperty("line.separator") + "BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        WBBBX**X X**       X**XX***X"
					+ System.getProperty("line.separator") + "                                                                                                                    XXX  XXX       XXX  XXXX"
					+ System.getProperty("line.separator") + "                                                                                                                   W"
					+ System.getProperty("line.separator") + "                                                                                                                 W"
					+ System.getProperty("line.separator") + "// "
					+ System.getProperty("line.separator") + "#"
					+ System.getProperty("line.separator") + "// Here are some basic settings to change the position of the logo. The light multiplier has a max value of 1.0"
					+ System.getProperty("line.separator") + "Scale=-30.0"
					+ System.getProperty("line.separator") + "OffsetX=-23.0"
					+ System.getProperty("line.separator") + "OffsetY=+1.5"
					+ System.getProperty("line.separator") + "AxisTilt=0.0"
					+ System.getProperty("line.separator") + "LightMultiplier=0.8"
					+ System.getProperty("line.separator") + "SplashTextEnabled=true"
					+ System.getProperty("line.separator") + "SplashTextOffsetX=-15.0"
					+ System.getProperty("line.separator") + "SplashTextOffsetY=-10.0");
			/*
				Field[] myFields = mod_BetaTweaks.class.getFields();
				for (int i = 0; i < myFields.length; i++) {
					if (myFields[i].getName().contains("logo"))
						try {
							configWriter.write(System.lineSeparator() + myFields[i].getName().replaceFirst("logo", "")
									+ "=" + myFields[i].get(null).toString());
						} catch (Exception exception) {
							exception.printStackTrace();
						}
				}
				*/
				configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
    
    private static final Random rand = new Random();
    private static Boolean undrawn = true;
    String minecraftLogo[] = getCustomLogo();
    String minecraftLogoVanilla[] = new String[] {
			  "X   X X X   X XXX XXX XXX XXX XXX XXX",
			  "XX XX X XX  X X   X   X X X X X    X ",
			  "X X X X X X X XX  X   XX  XXX XX   X ", 
			  "X   X X X  XX X   X   X X X X X    X ", 
			  "X   X X X   X XXX XXX X X X X X    X "};
    private LogoEffectRandomizer logoEffects[][];
    private float updateCounter;
    private String splashText;
    
    

	public static File configLogoFile = new File((Minecraft.getMinecraftDir()) + "/config/OldCustomLogo.cfg");
	private static Long timeStamp = configLogoFile.lastModified();
	private static List<String> image = new ArrayList<String>();
	private static List<Integer> blockID = new ArrayList<Integer>();
	private static List<Integer> metaData = new ArrayList<Integer>();
	private static List<Character> chars = new ArrayList<Character>();
	private static int maxLineLength = 0;
	
	public static Boolean resetLogo = true;

}
