// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package betatweaks;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import org.lwjgl.input.Keyboard;
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

	private final Method renderHandMethod = Utils.getMethod(EntityRenderer.class, new Class<?>[] {float.class, int.class}, "renderHand", "b", "func_4135_b");
	private final Method hurtCameraEffectMethod = Utils.getMethod(EntityRenderer.class, new Class<?>[] {float.class}, "hurtCameraEffect", "e");
	private final Method setupViewBobbingMethod = Utils.getMethod(EntityRenderer.class, new Class<?>[] {float.class}, "setupViewBobbing", "f");
	private final Method orientCameraMethod = Utils.getMethod(EntityRenderer.class, new Class<?>[] {float.class}, "orientCamera", "g");
	private final Method updateFogColorMethod = Utils.getMethod(EntityRenderer.class, new Class<?>[] {float.class}, "updateFogColor", "h");
	
	private final Field rendererUpdateCountField = Utils.getField(EntityRenderer.class, "rendererUpdateCount", "l");
	
    public EntityRendererProxyFOV()
    {
    	super(Utils.mc);
        farPlaneDistance = 0.0F;
        pointedEntity = null;
        cloudFog = false;
        cameraZoom = 1.0D;
        cameraYaw = 0.0D;
        cameraPitch = 0.0D;
        mc = Utils.mc;
        if(mod_BetaTweaks.shaderModInstalled) {
        	shaderHandler = new ShaderFOVHandler(this);
        	second_renderpass = shaderHandler.second_renderpass;
        	matrixbuffer = ByteBuffer.allocateDirect(64);
            projectionmatrixbuffer = ByteBuffer.allocateDirect(64);
        }
    }

    private float getFOVModifier(float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        float fov = 70F + Config.clientFovSliderValue * 40.0F;
        
		
        if(entityliving.isInsideOfMaterial(Material.water))
        {
        	fov *= 60.0F / 70.0F;
        }
        if(mc.gameSettings.smoothCamera = Keyboard.isKeyDown(mod_BetaTweaks.zoom.keyCode) && ModLoader.isGUIOpen(null)) {
        	fov /= 4F;
		}
        if(entityliving.health <= 0)
        {
            float f2 = (float)entityliving.deathTime + f;
            fov /= (1.0F - 500F / (f2 + 500F)) * 2.0F + 1.0F;
        }
        return fov * Config.clientFovMultiplier;
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
    	boolean optifine = mod_BetaTweaks.optifineInstalled;
    	boolean shader = mod_BetaTweaks.shaderModInstalled && optifine && net.minecraft.src.Config.isWaterFancy();
    	second_renderpass = shader;
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
        boolean flag = false;
        if(shader) {
        	shaderHandler.framebuffer.updateFBOsize(mc.displayWidth, mc.displayHeight);
        }
        for(int i = 0; (!shader && i < 2) || (shader && i < shaderHandler.water_config.water_render_mode); i++)
        {
            if(mc.gameSettings.anaglyph)
            {
            	second_renderpass = false;
                anaglyphField = i;
                if(anaglyphField == 0)
                {
                    GL11.glColorMask(false, true, true, false);
                } else
                {
                    GL11.glColorMask(true, false, false, false);
                }
            }
            if(second_renderpass)
            {
            	shaderHandler.framebuffer.bind(i);
                if(i == 1)
                {
                    flag = true;
                }
            } else
            {
                GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
            }
            if(i != 1 || !shader)
            {
                updateFogColor(f);
            }
            GL11.glClear(16640);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            if(shader) {
            	if(second_renderpass)
                {
                    setupCameraTransform(f, 0);
                    if(i == 0 || i == 1)
                    {
                        GL11.glFrontFace(2305 /*GL_CCW*/);
                    } else
                    {
                        GL11.glPushMatrix();
                        double d3 = 127.64D - d1 * 2D;
                        if(on_ground_delay == 10)
                        {
                            if(!entityliving.isInWater())
                            {
                                double d4 = shaderPlaneCasting(d, d1, d2);
                                if(d4 != 0.0D)
                                {
                                    last_water_height = 127.24000000953674D - 2D * d4;
                                }
                            }
                            on_ground_delay = 0;
                        }
                        if(entityliving.onGround)
                        {
                            on_ground_delay++;
                        }
                        d3 -= last_water_height;
                        GL11.glTranslatef(0.0F, (float)d3, 0.0F);
                        GL11.glScalef(1.0F, -1F, 1.0F);
                        float af[] = new float[16];
                        matrixbuffer.clear();
                        matrixbuffer.order(ByteOrder.nativeOrder());
                        GL11.glGetFloat(2982 /*GL_MODELVIEW_MATRIX*/, matrixbuffer.asFloatBuffer());
                        matrixbuffer.asFloatBuffer().get(af);
                        af = shaderInverseOrth(af);
                        af = shaderTranspose(af);
                        double d5 = (d3 - 127.64D) / -2D - 63.700000000000003D;
                        float af1[] = {
                            0.0F, 1.0F, 0.0F, (float)d5
                        };
                        af1 = shaderMultMatrixVector(af, af1);
                        shaderModifyProjectionMatrix(af1);
                        GL11.glFrontFace(2304 /*GL_CW*/);
                    }
                } else
                {
                    setupCameraTransform(f, i);
                }
                if(i == 1)
                {
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                }
                if(i == 0)
                {
                    ClippingHelperImpl.getInstance();
                }
            }
            else {
                setupCameraTransform(f, i);
                ClippingHelperImpl.getInstance();
            }
            if(mc.gameSettings.renderDistance < 2 || (optifine && net.minecraft.src.Config.isFarView()))
            {
                setupFog(-1, f);
                renderglobal.renderSky(f);
            }
            if(i != 1 || !shader) {
            	GL11.glEnable(2912 /*GL_FOG*/);
            	setupFog(1, f);
            	if(mc.gameSettings.ambientOcclusion)
            	{
            		GL11.glShadeModel(7425 /*GL_SMOOTH*/);
            	}
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
                } while(l1 >= 0L && ((!optifine && l1 <= 0x3b9aca00L) || (optifine && (double)l1 <= 1000000000D)));
            }
            if(i != 1 || !shader)
            {
                setupFog(0, f);
                GL11.glEnable(2912 /*GL_FOG*/);
            }
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(flag)
            {
                GL11.glClear(17664);
                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                GL11.glColor3f(1.0F, 0.0F, 0.0F);
            }
            RenderHelper.disableStandardItemLighting();
            if(flag)
            {
            	shaderHandler.black.bind();
            }
            else if(optifine && net.minecraft.src.Config.isUseAlphaFunc())		
            {		
                GL11.glAlphaFunc(516, net.minecraft.src.Config.getAlphaFuncLevel());		
            }
            renderglobal.sortAndRender(entityliving, 0, f);
            GL11.glShadeModel(7424 /*GL_FLAT*/);
            RenderHelper.enableStandardItemLighting();
            if(shader && (shaderHandler.water_config.reflective_items || i != 2) && !mc.gameSettings.anaglyph)
            {
            	boolean third_person = false;
                if(shaderHandler.water_config.reflect_player && i == 2)
                {
                    third_person = mc.gameSettings.thirdPersonView;
                    mc.gameSettings.thirdPersonView = true;
                }
                renderglobal.renderEntities(entityliving.getPosition(f), frustrum, f);
                if(shaderHandler.water_config.reflect_player && i == 2)
                {
                    mc.gameSettings.thirdPersonView = third_person;
                }
                effectrenderer.renderLitParticles(entityliving, f);
            }
            else if(!shader) {
                renderglobal.renderEntities(entityliving.getPosition(f), frustrum, f);
                effectrenderer.renderLitParticles(entityliving, f);
            }
            RenderHelper.disableStandardItemLighting();
            if(!shader || i != 1)
            {
                setupFog(0, f);
            }
            if(!shader || i == 0)
            {
                effectrenderer.renderParticles(entityliving, f);
                if(mc.objectMouseOver != null && entityliving.isInsideOfMaterial(Material.water) && (entityliving instanceof EntityPlayer))
                {
                	EntityPlayer entityplayer = (EntityPlayer)entityliving;
                    GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                    if(!mod_BetaTweaks.forgeInstalled || !BetaTweaksForgeHandler.ForgeHooksClient_onBlockHighlight(renderglobal,entityplayer,
                			mc.objectMouseOver,0,
                			entityplayer.inventory.getCurrentItem(),f)) {
                        renderglobal.drawBlockBreaking(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                        renderglobal.drawSelectionBox(entityplayer, mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                    }
                    GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                }
                GL11.glBlendFunc(770, 771);
                setupFog(0, f);
            }
            if(flag)
            {
            	shaderHandler.black.unbind();
            }
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glDisable(2884 /*GL_CULL_FACE*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
            if(flag)
            {
                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                shaderHandler.white.bind();
                shaderHandler.white.setValueMat4f(shaderHandler.white_inverse_view, shaderCalcInverseView(0.0D, 0.0D, 0.0D, entityliving.prevRotationYaw, entityliving.prevRotationPitch));
            }
            
            if(!shader || (i == 0 || i == 1))
            {
                if(second_renderpass && i == 0)
                {
                    GL11.glAlphaFunc(519, 0.0F);
                    shaderHandler.transparency.bind();
                    shaderHandler.transparency.setValue1f(shaderHandler.transp_render_v3, shaderHandler.water_config.render_v3);
                    shaderHandler.transparency.setValue1f(shaderHandler.transp_water, shaderHandler.water_config.water_surface_transparency);
                    shaderHandler.transparency.setValue1f(shaderHandler.transp_waterfall, shaderHandler.water_config.waterfall_transparency);
                    shaderHandler.transparency.setValueVec3f(shaderHandler.transp_waterfall_color, shaderHandler.water_config.waterfall_color[0], shaderHandler.water_config.waterfall_color[1], shaderHandler.water_config.waterfall_color[2]);
                    GL11.glEnable(3042 /*GL_BLEND*/);
                    GL11.glBlendFunc(770, 771);
                }
                if(!shader) {
                	if((!optifine && mc.gameSettings.fancyGraphics) || (optifine && net.minecraft.src.Config.isWaterFancy()))
                    {
                        if(mc.gameSettings.ambientOcclusion)
                        {
                            GL11.glShadeModel(7425 /*GL_SMOOTH*/);
                        }
                        GL11.glColorMask(false, false, false, false);
                        int i1;
                        if(optifine) i1 = renderglobal.renderAllSortedRenderers(1, f);
                        else i1 = renderglobal.sortAndRender(entityliving, 1, f);
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
                    } 
                    else
                    {
                        renderglobal.sortAndRender(entityliving, 1, f);
                    }
                }
                else {
                	renderglobal.renderAllSortedRenderers(1, f);
                }
                
                if(second_renderpass && i == 0)
                {
                	shaderHandler.transparency.unbind();
                    GL11.glAlphaFunc(516, 0.01F);
                }
            }
            if(i == 1 && flag)
            {
            	shaderHandler.white.unbind();
            	shaderHandler.black.bind();
            }
            GL11.glDepthMask(true);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            GL11.glDisable(3042 /*GL_BLEND*/);
            if(cameraZoom == 1.0D && (entityliving instanceof EntityPlayer) && mc.objectMouseOver != null && !entityliving.isInsideOfMaterial(Material.water))
            {
                EntityPlayer entityplayer1 = (EntityPlayer)entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                if(!mod_BetaTweaks.forgeInstalled || !BetaTweaksForgeHandler.ForgeHooksClient_onBlockHighlight(renderglobal,entityplayer1,
            			mc.objectMouseOver,0,
            			entityplayer1.inventory.getCurrentItem(),f)) {
                    renderglobal.drawBlockBreaking(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                    if(!shader || i == 0)
                    {
                        renderglobal.drawSelectionBox(entityplayer1, mc.objectMouseOver, 0, entityplayer1.inventory.getCurrentItem(), f);
                    }
                }
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }
            if(!shader || i != 2)
            {
                renderRainSnow(f);
            }
            if(!shader || i != 1)
            {
                GL11.glDisable(2912 /*GL_FOG*/);
                if((!optifine && pointedEntity == null) || (optifine && pointedEntity != null));
                setupFog(0, f);
            }
            if(!shader || (shaderHandler.water_config.reflective_clouds || i != 2)) {
                GL11.glEnable(2912 /*GL_FOG*/);
                renderglobal.renderClouds(f);
                GL11.glDisable(2912 /*GL_FOG*/);
            }
            if(!shader || i != 1)
            {
                setupFog(1, f);
            }
            if(cameraZoom == 1.0D && (!shader || i != 2))
            {
                GL11.glClear(256);
                renderHand(f, i);
            }
            if(second_renderpass)
            {
                if(i == 1)
                {
                    if(flag)
                    {
                    	shaderHandler.black.unbind();
                    }
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                    flag = false;
                }
                shaderHandler.framebuffer.unbind();
                if(i == 2)
                {
                    GL11.glPopMatrix();
                }
                continue;
            }
            if(!mc.gameSettings.anaglyph)
            {
                return;
            }
        }
        if(second_renderpass)
        {
            GL11.glFrontFace(2305 /*GL_CCW*/);
            GL11.glPushMatrix();
            shaderHandler.water.bind();
            shaderHandler.water.setValue1f(shaderHandler.water_timer, timer);
            long l1 = System.nanoTime();
            timer += (0.002F * (float)(l1 - previous_time)) / 1.3E+007F;
            if((double)timer >= 1.0D)
            {
                timer = 0.0F;
            }
            previous_time = l1;
            shaderHandler.framebuffer.bind_texture(0);
            shaderHandler.water.setValue1i(shaderHandler.water_colorMap, 0);
            GL13.glActiveTexture(33985 /*GL_TEXTURE1_ARB*/);
            shaderHandler.framebuffer.bind_texture(1);
            shaderHandler.water.setValue1i(shaderHandler.water_stencilMap, 1);
            if(shaderHandler.water_config.water_render_mode <= 3)
            {
                GL13.glActiveTexture(33986 /*GL_TEXTURE2_ARB*/);
                shaderHandler.framebuffer.bind_texture(2);
                shaderHandler.water.setValue1i(shaderHandler.water_reflectedColorMap, 2);
            }
            shaderHandler.water.setValueVec3f(shaderHandler.water_color, shaderHandler.water_config.water_color[0], shaderHandler.water_config.water_color[1], shaderHandler.water_config.water_color[2]);
            GL13.glActiveTexture(33984 /*GL_TEXTURE0_ARB*/);
            GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
            GL11.glLoadIdentity();
            GL11.glOrtho(-1D, 1.0D, -1D, 1.0D, 1.0D, 40D);
            GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
            GL11.glLoadIdentity();
            GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
            GL11.glClear(17664);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex3f(-1F, 1.0F, -1F);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex3f(-1F, -1F, -1F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex3f(1.0F, -1F, -1F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex3f(1.0F, 1.0F, -1F);
            GL11.glEnd();
            shaderHandler.water.unbind();
            shaderHandler.framebuffer.unbind_texture();
            GL11.glPopMatrix();
        } else
        {
            GL11.glColorMask(true, true, true, false);
        }
    }
    
    private FloatBuffer setFogColorBuffer(float f, float f1, float f2, float f3)
    {
    	FloatBuffer fogColorBuffer = mod_BetaTweaks.fogColorBuffer(this);
        fogColorBuffer.clear();
        fogColorBuffer.put(f).put(f1).put(f2).put(f3);
        fogColorBuffer.flip();
        return fogColorBuffer;
    }
    
    private void setupFog(int i, float f)
    {
        EntityLiving entityliving = mc.renderViewEntity;
        GL11.glFog(2918 /*GL_FOG_COLOR*/, setFogColorBuffer(mod_BetaTweaks.fogColorRed(this), mod_BetaTweaks.fogColorGreen(this), mod_BetaTweaks.fogColorBlue(this), 1.0F));
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
        float temp = Config.clientFovSliderValue;
        Config.clientFovSliderValue = 0.0F;
        GLU.gluPerspective(getFOVModifier(f), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        Config.clientFovSliderValue = temp;
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
	
	private float[] shaderMultMatrixVector(float af[], float af1[])
    {
        float af2[] = new float[4];
        af2[0] = af[0] * af1[0] + af[4] * af1[1] + af[8] * af1[2] + af[12] * af1[3];
        af2[1] = af[1] * af1[0] + af[5] * af1[1] + af[9] * af1[2] + af[13] * af1[3];
        af2[2] = af[2] * af1[0] + af[6] * af1[1] + af[10] * af1[2] + af[14] * af1[3];
        af2[3] = af[3] * af1[0] + af[7] * af1[1] + af[11] * af1[2] + af[15] * af1[3];
        return af2;
    }
	
	private float[] shaderTranspose(float af[])
    {
        float af1[] = new float[16];
        af1[0] = af[0];
        af1[1] = af[4];
        af1[2] = af[8];
        af1[3] = af[12];
        af1[4] = af[1];
        af1[5] = af[5];
        af1[6] = af[9];
        af1[7] = af[13];
        af1[8] = af[2];
        af1[9] = af[6];
        af1[10] = af[10];
        af1[11] = af[14];
        af1[12] = af[3];
        af1[13] = af[7];
        af1[14] = af[11];
        af1[15] = af[15];
        return af1;
    }

    private float[] shaderInverseOrth(float af[])
    {
        float af1[] = new float[16];
        af1[0] = af[0];
        af1[1] = af[4];
        af1[2] = af[8];
        af1[3] = 0.0F;
        af1[4] = af[1];
        af1[5] = af[5];
        af1[6] = af[9];
        af1[7] = 0.0F;
        af1[8] = af[2];
        af1[9] = af[6];
        af1[10] = af[10];
        af1[11] = 0.0F;
        af1[12] = -af[12] * af[0] - af[13] * af[1] - af[14] * af[2];
        af1[13] = -af[12] * af[4] - af[13] * af[5] - af[14] * af[6];
        af1[14] = -af[12] * af[8] - af[13] * af[9] - af[14] * af[10];
        af1[15] = 1.0F;
        return af1;
    }
	
	private double shaderPlaneCasting(double d, double d1, double d2)
    {
        Vec3D vec3d = mc.renderViewEntity.getLookVec();
        Vec3D vec3d1 = Vec3D.createVector(vec3d.zCoord, vec3d.yCoord, -vec3d.xCoord);
        vec3d1.normalize();
        Vec3D vec3d2 = vec3d1.crossProduct(vec3d).normalize();
        float af[] = {
            0.8F, 0.01F, 0.01F
        };
        float af1[] = {
            0.8F, 0.05F, 0.05F
        };
        byte byte0 = 14;
        byte byte1 = 14;
        byte byte2 = 30;
        boolean aflag[][] = new boolean[2 * byte0][2 * byte1];
        for(int i = 0; i < byte1; i++)
        {
            for(int k = 0; k < byte1; k++)
            {
                aflag[i][k] = true;
            }

        }

        for(int j = 1; j < byte2; j++)
        {
            double d3 = d + (double)j * vec3d.xCoord * (double)af[0];
            double d4 = d1 + (double)j * vec3d.yCoord * (double)af[0];
            double d5 = d2 + (double)j * vec3d.zCoord * (double)af[0];
            for(int l = -byte1; l < byte1; l++)
            {
                double d6 = d3 - (double)l * vec3d2.xCoord * (double)af[1];
                double d7 = d4 - (double)l * vec3d2.yCoord * (double)af[1];
                double d8 = d5 - (double)l * vec3d2.zCoord * (double)af[1];
                for(int i1 = 0; i1 < byte0; i1++)
                {
                    if(aflag[byte0 - i1][byte1 + l])
                    {
                        double d9 = d6 - (double)i1 * vec3d1.xCoord * (double)af[2];
                        double d11 = d7 - (double)i1 * vec3d1.yCoord * (double)af[2];
                        double d13 = d8 - (double)i1 * vec3d1.zCoord * (double)af[2];
                        int j1 = mc.theWorld.getBlockId((int)d9, (int)d11, (int)d13);
                        if(j1 == 9)
                        {
                            int l1 = mc.theWorld.getBlockMetadata((int)d9, (int)d11, (int)d13);
                            if(l1 == 0)
                            {
                                return Math.floor(d11) + 0.62000000476837003D;
                            }
                        } else
                        if(j1 != 0)
                        {
                            aflag[byte0 - i1][byte1 + l] = false;
                        }
                    }
                    if(!aflag[byte0 + i1][byte1 + l])
                    {
                        continue;
                    }
                    double d10 = d6 + (double)i1 * vec3d1.xCoord * (double)af[2];
                    double d12 = d7 + (double)i1 * vec3d1.yCoord * (double)af[2];
                    double d14 = d8 + (double)i1 * vec3d1.zCoord * (double)af[2];
                    int k1 = mc.theWorld.getBlockId((int)d10, (int)d12, (int)d14);
                    if(k1 == 9)
                    {
                        int i2 = mc.theWorld.getBlockMetadata((int)d10, (int)d12, (int)d14);
                        if(i2 == 0)
                        {
                            return Math.floor(d12) + 0.62000000476837003D;
                        }
                        continue;
                    }
                    if(k1 != 0)
                    {
                        aflag[byte0 + i1][byte1 + l] = false;
                    }
                }

            }

            af[1] += af1[1];
            af[2] += af1[2];
        }

        return 0.0D;
    }
	
	private void shaderModifyProjectionMatrix(float af[])
    {
        float af1[] = new float[16];
        float af2[] = new float[4];
        projectionmatrixbuffer.clear();
        projectionmatrixbuffer.order(ByteOrder.nativeOrder());
        GL11.glGetFloat(2983 /*GL_PROJECTION_MATRIX*/, projectionmatrixbuffer.asFloatBuffer());
        projectionmatrixbuffer.asFloatBuffer().get(af1);
        af2[0] = (Math.signum(af[0]) + af1[8]) / af1[0];
        af2[1] = (Math.signum(af[1]) + af1[9]) / af1[5];
        af2[2] = -1F;
        af2[3] = (1.0F + af1[10]) / af1[14];
        float af3[] = new float[4];
        float f = af[0] * af2[0] + af[1] * af2[1] + af[2] * af2[2] + af[3] * af2[3];
        af3[0] = af[0] * (2.0F / f);
        af3[1] = af[1] * (2.0F / f);
        af3[2] = af[2] * (2.0F / f);
        af3[3] = af[3] * (2.0F / f);
        af1[2] = af3[0];
        af1[6] = af3[1];
        af1[10] = af3[2] + 1.0F;
        af1[14] = af3[3];
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        projectionmatrixbuffer = ByteBuffer.allocateDirect(64);
        projectionmatrixbuffer.order(ByteOrder.nativeOrder());
        projectionmatrixbuffer.asFloatBuffer().put(af1);
        GL11.glLoadMatrix(projectionmatrixbuffer.asFloatBuffer());
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
    }
	
	private float[] shaderCalcInverseView(double d, double d1, double d2, double d3, double d4)
    {
        d4 = Math.toRadians(d4);
        d3 = Math.toRadians(d3);
        float af[] = {
            1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 
            1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
        };
        float af1[] = {
            1.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float)Math.cos(-d4), (float)(-Math.sin(-d4)), 0.0F, 0.0F, (float)Math.sin(-d4), 
            (float)Math.cos(-d4), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
        };
        float af2[] = {
            (float)Math.cos(-d3), 0.0F, (float)Math.sin(-d3), 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, (float)(-Math.sin(-d3)), 0.0F, 
            (float)Math.cos(-d3), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
        };
        float af3[] = {
            1.0F, 0.0F, 0.0F, (float)(-d), 0.0F, 1.0F, 0.0F, (float)(-d1), 0.0F, 0.0F, 
            1.0F, (float)(-d2), 0.0F, 0.0F, 0.0F, 1.0F
        };
        af = shaderMultMatrix(af3, shaderMultMatrix(af2, shaderMultMatrix(af1, af)));
        return af;
    }
	
	private float[] shaderMultMatrix(float af[], float af1[])
    {
        float af2[] = new float[16];
        af2[0] = af[0] * af1[0] + af[1] * af1[4] + af[2] * af1[8] + af[3] * af1[12];
        af2[1] = af[0] * af1[1] + af[1] * af1[5] + af[2] * af1[9] + af[3] * af1[13];
        af2[2] = af[0] * af1[2] + af[1] * af1[6] + af[2] * af1[10] + af[3] * af1[14];
        af2[3] = af[0] * af1[3] + af[1] * af1[7] + af[2] * af1[11] + af[3] * af1[15];
        af2[4] = af[4] * af1[0] + af[5] * af1[4] + af[6] * af1[8] + af[7] * af1[12];
        af2[5] = af[4] * af1[1] + af[5] * af1[5] + af[6] * af1[9] + af[7] * af1[13];
        af2[6] = af[4] * af1[2] + af[5] * af1[6] + af[6] * af1[10] + af[7] * af1[14];
        af2[7] = af[4] * af1[3] + af[5] * af1[7] + af[6] * af1[11] + af[7] * af1[15];
        af2[8] = af[8] * af1[0] + af[9] * af1[4] + af[10] * af1[8] + af[11] * af1[12];
        af2[9] = af[8] * af1[1] + af[9] * af1[5] + af[10] * af1[9] + af[11] * af1[13];
        af2[10] = af[8] * af1[2] + af[9] * af1[6] + af[10] * af1[10] + af[11] * af1[14];
        af2[11] = af[8] * af1[3] + af[9] * af1[7] + af[10] * af1[11] + af[11] * af1[15];
        af2[12] = af[12] * af1[0] + af[13] * af1[4] + af[14] * af1[8] + af[15] * af1[12];
        af2[13] = af[12] * af1[1] + af[13] * af1[5] + af[14] * af1[9] + af[15] * af1[13];
        af2[14] = af[12] * af1[2] + af[13] * af1[6] + af[14] * af1[10] + af[15] * af1[14];
        af2[15] = af[12] * af1[3] + af[13] * af1[7] + af[14] * af1[11] + af[15] * af1[15];
        return af2;
    }

    private ByteBuffer matrixbuffer;
    private ByteBuffer projectionmatrixbuffer;
	private double last_water_height = 0.0D;
	private float timer = 0.0f;
	private long previous_time = 0L;
	private boolean second_renderpass = false;
	private int on_ground_delay = 0; 
	private ShaderFOVHandler shaderHandler;
    private Minecraft mc;
    private float farPlaneDistance;
    private Entity pointedEntity;
    private boolean cloudFog;
    private double cameraZoom;
    private double cameraYaw;
    private double cameraPitch;

}
