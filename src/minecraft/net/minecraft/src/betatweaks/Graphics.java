package net.minecraft.src.betatweaks;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

// Used to draw graphics without a Gui object
public class Graphics {
	
	public static final Gui gui = new Gui();
	public static final RenderItem itemRenderer = new RenderItem();
	private static final Minecraft mc = Utils.MC;
	private static boolean itemLighting;
	private static boolean preRender;
	public static Boolean lighting;
	
	public static void drawRect(int i, int j, int k, int l, int colour) {
		disableLighting();
		mod_BetaTweaks.drawRect(i, j, k, l, colour);
	}
	
	public static void drawSlotBackground(int x, int y, int colour) {
		drawRect(x, y, x + 16, y + 16, colour);
	}
	
	public static void drawSlot(int x, int y, int colour) {
		drawRect(x, y, x + 18, y + 18, colour);
	}
	
	public static void disableLighting() {
		preRender();
		if(lighting != Boolean.FALSE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			lighting = false;
		}
	}
	
	public static void drawMultiLineTooltip(String... s) {
		if(s == null) return;
		int i = mc.currentScreen.width / 2 - 150;
        int j = mc.currentScreen.height / 6 - 30;
        if(Utils.cursorY() <= mc.currentScreen.height / 2)
        {
        	//j += 105;
            j = mc.currentScreen.height - j - s.length * 11;
        }
        int j1 = i + 150 + 150;
        int k1 = j + 11 * s.length + 6;
        
        drawRect(i, j, j1, k1, 0xe0000000);
        for(int l1 = 0; l1 < s.length; l1++)
        {
            drawString(s[l1], i + 5, j - 6 + (l1 + 1) * 11);
        }
        postRender();
	}
	
	private static void enableLighting() {
		preRender();
		if(lighting != Boolean.TRUE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(2896 /*GL_LIGHTING*/);
	        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			lighting = true;
		}
	}
	
	public static void bindTexture(String texturePath) {
		preRender();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(texturePath));
	}
	
	public static void drawItemStack(int x, int y, ItemStack item) {
		drawItemStack(x, y, item, true);
	}
	
	public static void drawItemStack(int x, int y, ItemStack item, boolean drawOverlay) {
		enableItemLighting();
		itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item, x, y);
		if(drawOverlay) {
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, item, x, y);
		}
	}
	
	private static void enableItemLighting() {
		preRender();
		enableLighting();
		if(!itemLighting) {
			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
			GL11.glPushMatrix();
			GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
			itemLighting = true;
		}
	}
	
	public static void drawString(String s, int x, int y) {
		disableLighting();
		mc.fontRenderer.drawStringWithShadow(s, x, y, -1);
	}
	
	public static void preRender() {
		if(!preRender) {
			preRender = true;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			lighting = null;
			itemLighting = false;
		}
	}

	public static void postRender() {
		preRender = false;
		lighting = null;
		RenderHelper.disableStandardItemLighting();
		enableLighting();
	}
}
