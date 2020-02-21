package net.minecraft.src.betatweaks;

import net.minecraft.src.*;

public class ShaderFOVHandler {
	
	public ShaderFOVHandler(EntityRenderer renderer) {
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
	public boolean second_renderpass;
}
