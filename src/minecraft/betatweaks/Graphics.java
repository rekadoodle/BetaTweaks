package betatweaks;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class Graphics {
	
	public static final Gui gui = new Gui();
	public static final RenderItem itemRenderer = new RenderItem();
	private static final Minecraft mc = Utils.mc;
	
	// Used to draw graphics without a Gui object
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
	
	public static void drawTooltip(String s, int x, int y) {
		//stores tooltip info, to be drawn after overlay
		tooltipText = s;
		tooltipX = x;
		tooltipY = y;
	}
	
	public static void drawStoredToolTip() {
		if(tooltipText != null) {
			disableLighting();
			int k1 = tooltipX + 12;
			int i2 = tooltipY - 12;
			int j2 = mc.fontRenderer.getStringWidth(tooltipText);
			drawRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000);
			mc.fontRenderer.drawStringWithShadow(tooltipText, k1, i2, -1);
			tooltipText = null;
			postRender();
		}
	}
	
	public static void disableLighting() {
		if(lighting != Boolean.FALSE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			lighting = false;
		}
	}
	
	private static void enableLighting() {
		if(lighting != Boolean.TRUE) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(2896 /*GL_LIGHTING*/);
	        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			lighting = true;
		}
	}
	
	public static void bindTexture(String texturePath) {
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
	
	private static boolean itemLighting;
	public static Boolean lighting;
	private static String tooltipText;
	private static int tooltipX;
	private static int tooltipY;
	
	public static void drawString(String s, int x, int y) {
		disableLighting();
		int k1 = (x) + 12;
		int i2 = y - 12;
		mc.fontRenderer.drawStringWithShadow(s, k1, i2, -1);
	}
	
	public static void preRender() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		lighting = null;
		itemLighting = false;
	}

	public static void postRender() {
		lighting = null;
		RenderHelper.disableStandardItemLighting();
		enableLighting();
	}
}
