// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

// Referenced classes of package net.minecraft.src:
//            MouseFilter, GLAllocation, ItemRenderer, EntityLiving, 
//            MathHelper, World, GameSettings, PlayerController, 
//            MovingObjectPosition, Vec3D, PlayerControllerTest, AxisAlignedBB, 
//            Entity, Material, EntityPlayer, Block, 
//            RenderGlobal, EntityPlayerSP, MouseHelper, ScaledResolution, 
//            GuiIngame, GuiScreen, GuiParticle, ChunkProviderLoadOrGenerate, 
//            ClippingHelperImpl, Frustrum, ICamera, RenderEngine, 
//            RenderHelper, EffectRenderer, InventoryPlayer, WorldChunkManager, 
//            BiomeGenBase, EntitySmokeFX, EntityRainFX, Tessellator, 
//            WorldProvider

public class EntityRendererProxyFOV extends EntityRendererProxy
{

	private final Method renderHandMethod = getObfuscatedPrivateMethod(EntityRenderer.class, new String[] {"renderHand", "b", "func_4135_b"}, new Class<?>[] {float.class, int.class});
	private final Method hurtCameraEffectMethod = getObfuscatedPrivateMethod(EntityRenderer.class, new String[] {"hurtCameraEffect", "e"}, new Class<?>[] {float.class});
	private final Method setupViewBobbingMethod = getObfuscatedPrivateMethod(EntityRenderer.class, new String[] {"setupViewBobbing", "f"}, new Class<?>[] {float.class});
	private final Method orientCameraMethod = getObfuscatedPrivateMethod(EntityRenderer.class, new String[] {"orientCamera", "g"}, new Class<?>[] {float.class});
	private final Method updateFogColorMethod = getObfuscatedPrivateMethod(EntityRenderer.class, new String[] {"updateFogColor", "h"}, new Class<?>[] {float.class});
	
	private final Field rendererUpdateCountField = mod_BetaTweaks.getObfuscatedPrivateField(EntityRenderer.class, new String[] {"rendererUpdateCount", "l"});
	
	public static final Method getObfuscatedPrivateMethod(Class<?> target, String names[], Class<?> types[]) {
		for (String name : names) {
			try {
				Method method = target.getDeclaredMethod(name, types);
				method.setAccessible(true);
				return method;
			}  
			catch (NoSuchMethodException e) { }
        }
		System.out.println("ERROR: Turns out that wasn't a fart.");
        return null;
    }
	
    public EntityRendererProxyFOV(Minecraft minecraft)
    {
    	super(minecraft);
        farPlaneDistance = 0.0F;
        pointedEntity = null;
        debugCamFOV = 0.0F;
        prevDebugCamFOV = 0.0F;
        cloudFog = false;
        cameraZoom = 1.0D;
        cameraYaw = 0.0D;
        cameraPitch = 0.0D;
        mc = minecraft;
        itemRenderer = new ItemRenderer(minecraft);
    }

    private float getFOVModifier(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        float f1 = 70F + mod_BetaTweaks.optionsClientFovSliderValue * 40.0F;
        
		if(mc.gameSettings.smoothCamera = Keyboard.isKeyDown(mod_BetaTweaks.zoom.keyCode)) {
			f1 /= 4F;
		}
        if(entityliving.isInsideOfMaterial(Material.water))
        {
            f1 = 60F;
        }
        if(entityliving.health <= 0)
        {
            float f2 = (float)entityliving.deathTime + f;
            f1 /= (1.0F - 500F / (f2 + 500F)) * 2.0F + 1.0F;
        }
        return f1 + prevDebugCamFOV + (debugCamFOV - prevDebugCamFOV) * f;
    }
   
    private void setupCameraTransform(float f, int i)
    {
        farPlaneDistance = 256 >> mc.gameSettings.renderDistance;
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        float f1 = 0.07F;
        if(mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(-(i * 2 - 1)) * f1, 0.0F, 0.0F);
        }
        if(cameraZoom != 1.0D)
        {
            GL11.glTranslatef((float)cameraYaw, (float)(-cameraPitch), 0.0F);
            GL11.glScaled(cameraZoom, cameraZoom, 1.0D);
            GLU.gluPerspective(getFOVModifier(f), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance * 2.0F);
        } else
        {
            GLU.gluPerspective(getFOVModifier(f), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance * 2.0F);
        }
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        if(mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(i * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }
        hurtCameraEffect(f);
        if(mc.gameSettings.viewBobbing)
        {
            setupViewBobbing(f);
        }
        float f2 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * f;
        if(f2 > 0.0F)
        {
        	int renderUpdateCount = 0;
        	try {
				renderUpdateCount = rendererUpdateCountField.getInt(this);
			} 
        	catch (IllegalAccessException e) { e.printStackTrace(); }
            float f3 = 5F / (f2 * f2 + 5F) - f2 * 0.04F;
            f3 *= f3;
            GL11.glRotatef(((float)renderUpdateCount + f) * 20F, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / f3, 1.0F, 1.0F);
            GL11.glRotatef(-((float)renderUpdateCount + f) * 20F, 0.0F, 1.0F, 1.0F);
        }
        orientCamera(f);
    }

   
    
    public void renderWorld(float f, long l)
    {
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        if(mc.renderViewEntity == null)
        {
            mc.renderViewEntity = mc.thePlayer;
        }
        getMouseOver(f);
        EntityLiving entityliving = mc.renderViewEntity;
        RenderGlobal renderglobal = mc.renderGlobal;
        EffectRenderer effectrenderer = mc.effectRenderer;
        double d = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double)f;
        double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double)f;
        double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double)f;
        IChunkProvider ichunkprovider = mc.theWorld.getIChunkProvider();
        if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
            int j = MathHelper.floor_float((int)d) >> 4;
            int k = MathHelper.floor_float((int)d2) >> 4;
            chunkproviderloadorgenerate.setCurrentChunkOver(j, k);
        }
        for(int i = 0; i < 2; i++)
        {
            if(mc.gameSettings.anaglyph)
            {
                anaglyphField = i;
                if(anaglyphField == 0)
                {
                    GL11.glColorMask(false, true, true, false);
                } else
                {
                    GL11.glColorMask(true, false, false, false);
                }
            }
            GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
            updateFogColor(f);
            GL11.glClear(16640);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            setupCameraTransform(f, i);
            ClippingHelperImpl.getInstance();
            if(mc.gameSettings.renderDistance < 2)
            {
                setupFog(-1, f);
                renderglobal.renderSky(f);
            }
            GL11.glEnable(2912 /*GL_FOG*/);
            setupFog(1, f);
            if(mc.gameSettings.ambientOcclusion)
            {
                GL11.glShadeModel(7425 /*GL_SMOOTH*/);
            }
            Frustrum frustrum = new Frustrum();
            frustrum.setPosition(d, d1, d2);
            mc.renderGlobal.clipRenderersByFrustrum(frustrum, f);
            long l1;
            if(i == 0)
            {
                do
                {
                    if(mc.renderGlobal.updateRenderers(entityliving, false) || l == 0L)
                    {
                        break;
                    }
                    l1 = l - System.nanoTime();
                } while(l1 >= 0L && l1 <= 0x3b9aca00L);
            }
            setupFog(0, f);
            GL11.glEnable(2912 /*GL_FOG*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            RenderHelper.disableStandardItemLighting();
            renderglobal.sortAndRender(entityliving, 0, f);
            GL11.glShadeModel(7424 /*GL_FLAT*/);
            RenderHelper.enableStandardItemLighting();
            renderglobal.renderEntities(entityliving.getPosition(f), frustrum, f);
            effectrenderer.renderLitParticles(entityliving, f);
            RenderHelper.disableStandardItemLighting();
            setupFog(0, f);
            effectrenderer.renderParticles(entityliving, f);
            if(mc.objectMouseOver != null && entityliving.isInsideOfMaterial(Material.water) && (entityliving instanceof EntityPlayer))
            {
                EntityPlayer entityplayer = (EntityPlayer)entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.drawBlockBreaking(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }
            GL11.glBlendFunc(770, 771);
            setupFog(0, f);
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glDisable(2884 /*GL_CULL_FACE*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(mc.gameSettings.fancyGraphics)
            {
                if(mc.gameSettings.ambientOcclusion)
                {
                    GL11.glShadeModel(7425 /*GL_SMOOTH*/);
                }
                GL11.glColorMask(false, false, false, false);
                int i1 = renderglobal.sortAndRender(entityliving, 1, f);
                if(mc.gameSettings.anaglyph)
                {
                    if(anaglyphField == 0)
                    {
                        GL11.glColorMask(false, true, true, true);
                    } else
                    {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else
                {
                    GL11.glColorMask(true, true, true, true);
                }
                if(i1 > 0)
                {
                    renderglobal.renderAllRenderLists(1, f);
                }
                GL11.glShadeModel(7424 /*GL_FLAT*/);
            } else
            {
                renderglobal.sortAndRender(entityliving, 1, f);
            }
            GL11.glDepthMask(true);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            GL11.glDisable(3042 /*GL_BLEND*/);
            if(cameraZoom == 1.0D && (entityliving instanceof EntityPlayer) && mc.objectMouseOver != null && !entityliving.isInsideOfMaterial(Material.water))
            {
                EntityPlayer entityplayer1 = (EntityPlayer)entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.drawBlockBreaking(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }
            renderRainSnow(f);
            GL11.glDisable(2912 /*GL_FOG*/);
            if(pointedEntity == null);
            setupFog(0, f);
            GL11.glEnable(2912 /*GL_FOG*/);
            renderglobal.renderClouds(f);
            GL11.glDisable(2912 /*GL_FOG*/);
            setupFog(1, f);
            if(cameraZoom == 1.0D)
            {
                GL11.glClear(256);
                renderHand(f, i);
            }
            if(!mc.gameSettings.anaglyph)
            {
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
    }
    
    private FloatBuffer setFogColorBuffer(float f, float f1, float f2, float f3)
    {
        fogColorBuffer.clear();
        fogColorBuffer.put(f).put(f1).put(f2).put(f3);
        fogColorBuffer.flip();
        return fogColorBuffer;
    }
    
    private void setupFog(int i, float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        GL11.glFog(2918 /*GL_FOG_COLOR*/, setFogColorBuffer(fogColorRed, fogColorGreen, fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(cloudFog)
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 0.1F);
            float f1 = 1.0F;
            float f4 = 1.0F;
            float f7 = 1.0F;
            if(mc.gameSettings.anaglyph)
            {
                float f10 = (f1 * 30F + f4 * 59F + f7 * 11F) / 100F;
                float f13 = (f1 * 30F + f4 * 70F) / 100F;
                float f16 = (f1 * 30F + f7 * 70F) / 100F;
                f1 = f10;
                f4 = f13;
                f7 = f16;
            }
        } else
        if(entityliving.isInsideOfMaterial(Material.water))
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 0.1F);
            float f2 = 0.4F;
            float f5 = 0.4F;
            float f8 = 0.9F;
            if(mc.gameSettings.anaglyph)
            {
                float f11 = (f2 * 30F + f5 * 59F + f8 * 11F) / 100F;
                float f14 = (f2 * 30F + f5 * 70F) / 100F;
                float f17 = (f2 * 30F + f8 * 70F) / 100F;
                f2 = f11;
                f5 = f14;
                f8 = f17;
            }
        } else
        if(entityliving.isInsideOfMaterial(Material.lava))
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 2.0F);
            float f3 = 0.4F;
            float f6 = 0.3F;
            float f9 = 0.3F;
            if(mc.gameSettings.anaglyph)
            {
                float f12 = (f3 * 30F + f6 * 59F + f9 * 11F) / 100F;
                float f15 = (f3 * 30F + f6 * 70F) / 100F;
                float f18 = (f3 * 30F + f9 * 70F) / 100F;
                f3 = f12;
                f6 = f15;
                f9 = f18;
            }
        } else
        {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 9729 /*GL_LINEAR*/);
            GL11.glFogf(2915 /*GL_FOG_START*/, farPlaneDistance * 0.25F);
            GL11.glFogf(2916 /*GL_FOG_END*/, farPlaneDistance);
            if(i < 0)
            {
                GL11.glFogf(2915 /*GL_FOG_START*/, 0.0F);
                GL11.glFogf(2916 /*GL_FOG_END*/, farPlaneDistance * 0.8F);
            }
            if(GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
            if(mc.theWorld.worldProvider.isNether)
            {
                GL11.glFogf(2915 /*GL_FOG_START*/, 0.0F);
            }
        }
        GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
        GL11.glColorMaterial(1028 /*GL_FRONT*/, 4608 /*GL_AMBIENT*/);
    }
	
	private void updateFogColor(float f)
    {
		try {
			updateFogColorMethod.invoke(this, new Object[] {f});
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); } 
		catch (InvocationTargetException e) { e.printStackTrace(); }
    }
	
	private void renderHand(float f, int i)
    {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float temp = mod_BetaTweaks.optionsClientFovSliderValue;
        mod_BetaTweaks.optionsClientFovSliderValue = 0.0F;
        GLU.gluPerspective(getFOVModifier(f), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        mod_BetaTweaks.optionsClientFovSliderValue = temp;
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
		try {
			renderHandMethod.invoke(this, new Object[] {f, i});
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); } 
		catch (InvocationTargetException e) { e.printStackTrace(); }
    }
	
	private void hurtCameraEffect(float f)
    {
		try {
			hurtCameraEffectMethod.invoke(this, new Object[] {f});
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); } 
		catch (InvocationTargetException e) { e.printStackTrace(); }
    }
	
	private void setupViewBobbing(float f)
    {
		try {
			setupViewBobbingMethod.invoke(this, new Object[] {f});
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); } 
		catch (InvocationTargetException e) { e.printStackTrace(); }
    }
	
	private void orientCamera(float f)
    {
		try {
			orientCameraMethod.invoke(this, new Object[] {f});
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); } 
		catch (InvocationTargetException e) { e.printStackTrace(); }
    }

    private Minecraft mc;
    private float farPlaneDistance;
    public ItemRenderer itemRenderer;
    private Entity pointedEntity;
    private float debugCamFOV;
    private float prevDebugCamFOV;
    private boolean cloudFog;
    private double cameraZoom;
    private double cameraYaw;
    private double cameraPitch;

}
