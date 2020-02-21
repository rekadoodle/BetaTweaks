package net.minecraft.src.betatweaks.references.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.references.HandlerShaders;

public class ConcreteHandlerShaders extends HandlerShaders {
	
	public void init(EntityRenderer renderer) {
		this.renderer = renderer;
		framebuffer = (FBO) getValue("framebuffer");
		water_config = (Water_Config) getValue("water_config");
		black = (Shader) getValue("black");
		white = (Shader) getValue("white");
		transparency = (Shader) getValue("transparency");
		water = (Shader) getValue("water");
		white_inverse_view = (Integer) getValue("white_inverse_view");
		transp_render_v3 = (Integer) getValue("transp_render_v3");
		transp_water = (Integer) getValue("transp_water");
		transp_waterfall = (Integer) getValue("transp_waterfall");
		transp_waterfall_color = (Integer) getValue("transp_waterfall_color");
		water_timer = (Integer) getValue("water_timer");
		water_colorMap = (Integer) getValue("water_colorMap");
		water_stencilMap = (Integer) getValue("water_stencilMap");
		water_reflectedColorMap = (Integer) getValue("water_reflectedColorMap");
		water_color = (Integer) getValue("water_color");
		second_renderpass = (Boolean) getValue("second_renderpass");
	}
	
	private Object getValue(String s) {
		try {
			return Utils.getField(EntityRenderer.class, s).get(renderer);
		} 
		catch (Exception e) { e.printStackTrace(); } 
		return null;
	}
	
	private EntityRenderer renderer;
	public FBO framebuffer;
	public Water_Config water_config;
	public Shader black;
	public Shader white;
	public Shader transparency;
	public Shader water;
	public int white_inverse_view;
	public int transp_render_v3;
	public int transp_water;
	public int transp_waterfall;
	public int transp_waterfall_color;
	public int water_timer;
	public int water_colorMap;
	public int water_stencilMap;
	public int water_reflectedColorMap;
	public int water_color;
	private float timer = 0.0f;
	private long previous_time = 0L;
	
	
	@Override
	public void updateFBOSize(int i, int j) {
		framebuffer.updateFBOsize(i, j);
	}

	@Override
	public int getWaterRenderMode() {
		return water_config.water_render_mode;
	}

	@Override
	public void bind(int i) {
		framebuffer.bind(i);
	}
	
	@Override
	public void unbind() {
		framebuffer.unbind();
	}

	@Override
	public void bindBlack() {
		black.bind();
	}
	
	@Override
	public void unbindBlack() {
		black.unbind();
	}
	
	@Override
	public void bindWhite(EntityLiving entityliving) {
		white.bind();
		white.setValueMat4f(white_inverse_view, shaderCalcInverseView(0.0D, 0.0D, 0.0D, entityliving.prevRotationYaw, entityliving.prevRotationPitch));
	}
	
	@Override
	public void unbindWhite() {
		white.unbind();
	}

	@Override
	public void bindTransparency() {
		GL11.glAlphaFunc(519, 0.0F);
        transparency.bind();
        transparency.setValue1f(transp_render_v3, water_config.render_v3);
        transparency.setValue1f(transp_water, water_config.water_surface_transparency);
        transparency.setValue1f(transp_waterfall, water_config.waterfall_transparency);
        transparency.setValueVec3f(transp_waterfall_color, water_config.waterfall_color[0], water_config.waterfall_color[1], water_config.waterfall_color[2]);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
	}
	
	@Override
	public void unbindTransparency() {
		transparency.unbind();
        GL11.glAlphaFunc(516, 0.01F);
	}

	@Override
	public boolean getWaterReflectiveItems() {
		return water_config.reflective_items;
	}
	
	@Override
	public boolean getWaterReflectivePlayer() {
		return water_config.reflect_player;
	}
	
	@Override
	public boolean getWaterReflectiveClouds() {
		return water_config.reflective_clouds;
	}

	@Override
	public void confusingShaderStuff() {
		Minecraft mc = Utils.mc;
		GL11.glFrontFace(2305 /*GL_CCW*/);
        GL11.glPushMatrix();
        water.bind();
        water.setValue1f(water_timer, timer);
        long l1 = System.nanoTime();
        timer += (0.002F * (float)(l1 - previous_time)) / 1.3E+007F;
        if((double)timer >= 1.0D)
        {
            timer = 0.0F;
        }
        previous_time = l1;
        framebuffer.bind_texture(0);
        water.setValue1i(water_colorMap, 0);
        GL13.glActiveTexture(33985 /*GL_TEXTURE1_ARB*/);
        framebuffer.bind_texture(1);
        water.setValue1i(water_stencilMap, 1);
        if(water_config.water_render_mode <= 3)
        {
            GL13.glActiveTexture(33986 /*GL_TEXTURE2_ARB*/);
            framebuffer.bind_texture(2);
            water.setValue1i(water_reflectedColorMap, 2);
        }
        water.setValueVec3f(water_color, water_config.water_color[0], water_config.water_color[1], water_config.water_color[2]);
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
        water.unbind();
        framebuffer.unbind_texture();
        GL11.glPopMatrix();
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
}
