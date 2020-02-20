package net.minecraft.src;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public abstract class GuiList {
	public final Minecraft mc;
	private final int left = 0;
	private final int right;
	protected final int width;
	protected final int height;
	protected final int top;
	protected final int bottom;
	protected final int slotHeight;
	protected float amountScrolled = 0;
	protected float scrollMultiplier;

	private boolean drag;
	private float dragY = 0;

	public GuiList(Minecraft mc, int width, int height, int top, int bottom, int slotHeight){
		this.mc = mc;
		this.height = height;
		this.width = width;
		this.top = top;
		this.bottom = bottom;
		this.slotHeight = slotHeight;
		this.right = width;
	}

	protected int getContentHeight() {
		return getSize() * slotHeight;
	}

	public abstract int getSize();

	public void mouseClicked(int mouseX, int mouseY, int button){
		if (mouseY > top && mouseY < bottom){
			drag = true;
			dragY = mouseY;

			int listRight = width / 2 + 124;
			int scrollRight = listRight + 6;
			if(mouseX >= listRight && mouseX <= scrollRight) {
				scrollMultiplier = -1F;
				int contentHeight = getContentHeight() - (bottom - top - 4);
				if(contentHeight < 1) {
					contentHeight = 1;
				}
				int i4 = (int)((float)((bottom - top) * (bottom - top)) / (float)getContentHeight());
				if(i4 < 32) {
					i4 = 32;
				}
				if(i4 > bottom - top - 8) {
					i4 = bottom - top - 8;
				}
				scrollMultiplier /= (float)(bottom - top - i4) / (float)contentHeight;
			} else {
				scrollMultiplier = 1F;
			}
		}
	}

	public void mouseMovedOrUp(int mouseX, int mouseY, int button){
		if (drag){
			if (button < 0){
				if (Mouse.isButtonDown(0)){
					updateScrolled(0 - ((float)mouseY - dragY) * scrollMultiplier);
					dragY = mouseY;
				}
			} else if (button == 0) {
				drag = false;
			}
		}
	}

	public void mouseScrolled(int amount){
		if(amount > 0) {
			amount = -1;
		} else if(amount < 0) {
			amount = 1;
		}

		updateScrolled((amount * slotHeight) / 2);
	}

	public void updateScrolled(float amount){
		int i = getContentHeight() - (bottom - top - 4);
		if(i < 0) {
			i /= 2;
		}

		amountScrolled += amount;
		if(amountScrolled < 0.0F) {
			amountScrolled = 0.0F;
		} else if(amountScrolled > (float)i) {
			amountScrolled = i;
		}
	}

	public abstract void keyTyped(char key, int keyId);

	public abstract void drawSlot(int id, int left, int top, int mouseX, int mouseY, Tessellator tessellator);

	public void drawScreen(int mouseX, int mouseY, float f){
		int size = getSize();
		int l = width / 2 + 124;
		int i1 = l + 6;

		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2912 /*GL_FOG*/);
		Tessellator tessellator = Tessellator.instance;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f1 = 32F;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(0x202020);
		tessellator.addVertexWithUV(left, bottom, 0.0D, (float)left / f1, (float)(bottom + (int)amountScrolled) / f1);
		tessellator.addVertexWithUV(right, bottom, 0.0D, (float)right / f1, (float)(bottom + (int)amountScrolled) / f1);
		tessellator.addVertexWithUV(right, top, 0.0D, (float)right / f1, (float)(top + (int)amountScrolled) / f1);
		tessellator.addVertexWithUV(left, top, 0.0D, (float)left / f1, (float)(top + (int)amountScrolled) / f1);
		tessellator.draw();
 
		int i2 = width / 2 - 92 - 16;
		int k2 = (top + 4) - (int)amountScrolled;
		for(int i3 = 0; i3 < size; i3++) {
			int k3 = k2 + i3 * slotHeight;
			int j4 = slotHeight - 4;
			if(k3 > bottom || k3 + j4 < top) {
				continue;
			}
			drawSlot(i3, i2, k3, mouseX, mouseY, tessellator);
		}

		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		byte byte0 = 4;
		overlayBackground(0, top, 255, 255);
		overlayBackground(bottom, height, 255, 255);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
		GL11.glShadeModel(7425 /*GL_SMOOTH*/);
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0, 0);
		tessellator.addVertexWithUV(left, top + byte0, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(right, top + byte0, 0.0D, 1.0D, 1.0D);
		tessellator.setColorRGBA_I(0, 255);
		tessellator.addVertexWithUV(right, top, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0, 255);
		tessellator.addVertexWithUV(left, bottom, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(right, bottom, 0.0D, 1.0D, 1.0D);
		tessellator.setColorRGBA_I(0, 0);
		tessellator.addVertexWithUV(right, bottom - byte0, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(left, bottom - byte0, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		int contentHeight = getContentHeight() - (bottom - top - 4);
		if(contentHeight > 0) {
			int k4 = ((bottom - top) * (bottom - top)) / getContentHeight();
			if(k4 < 32) {
				k4 = 32;
			}
			if(k4 > bottom - top - 8) {
				k4 = bottom - top - 8;
			}
			int i5 = ((int)amountScrolled * (bottom - top - k4)) / contentHeight + top;
			if(i5 < top) {
				i5 = top;
			}
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(l, bottom, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(i1, bottom, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(i1, top, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(l, top, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0x808080, 255);
			tessellator.addVertexWithUV(l, i5 + k4, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(i1, i5 + k4, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(i1, i5, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(l, i5, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0xc0c0c0, 255);
			tessellator.addVertexWithUV(l, (i5 + k4) - 1, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(i1 - 1, (i5 + k4) - 1, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(i1 - 1, i5, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(l, i5, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		}
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glShadeModel(7424 /*GL_FLAT*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
	}

	private void overlayBackground(int top, int bottom, int k, int l) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32F;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0x404040, l);
		tessellator.addVertexWithUV(0.0D, bottom, 0.0D, 0.0D, (float)bottom / f);
		tessellator.addVertexWithUV(width, bottom, 0.0D, (float)width / f, (float)bottom / f);
		tessellator.setColorRGBA_I(0x404040, k);
		tessellator.addVertexWithUV(width, top, 0.0D, (float)width / f, (float)top / f);
		tessellator.addVertexWithUV(0.0D, top, 0.0D, 0.0D, (float)top / f);
		tessellator.draw();
	}
}
