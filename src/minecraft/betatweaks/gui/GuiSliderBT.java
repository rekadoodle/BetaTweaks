package betatweaks.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import betatweaks.Config;


public class GuiSliderBT extends GuiButton
{
	public GuiSliderBT(int x, int y, Field field)
    {
        this(x, y, field, false);
    }
	
	public GuiSliderBT(int x, int y, Field field, boolean showChatDebugWhenDragging)
    {
        super(-1, x, y, 150, 20, Config.getSliderText(field));
        this.field = field;
        sliderValue = Config.getFloatValue(field);
        dragging = false;
        this.showChatDebugWhenDragging = showChatDebugWhenDragging;
    }

    protected int getHoverState(boolean flag)
    {
        return 0;
    }

    protected void mouseDragged(Minecraft minecraft, int i, int j)
    {
        if(!enabled2)
        {
            return;
        }
        if(dragging)
        {
        	if(showChatDebugWhenDragging) {
            	GuiImprovedChat.drawChatBoxArea();
        	}
            sliderValue = (float)(i - (xPosition + 4)) / (float)(width - 8);
            if(sliderValue < 0.0F)
            {
                sliderValue = 0.0F;
            }
            if(sliderValue > 1.0F)
            {
                sliderValue = 1.0F;
            }
            Config.setFloatValue(field, sliderValue);
            displayString = Config.getSliderText(field);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        byte byte0 = 0;
        if(super.mousePressed(minecraft, i, j) || dragging)
        {
            byte0 = 20;
        }
        drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)), yPosition, 0, 66 + byte0, 4, 20);
        drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)) + 4, yPosition, 196, 66 + byte0, 4, 20);
    }

    public boolean mousePressed(Minecraft minecraft, int i, int j)
    {
    	boolean returnval;
        if(returnval = super.mousePressed(minecraft, i, j))
        {
            dragging = true;
        }
        return returnval;
    }

    public void mouseReleased(int i, int j)
    {
        dragging = false;
        Config.writeConfig();
    }

    private boolean showChatDebugWhenDragging;
    private float sliderValue;
    private boolean dragging;
    private Field field;
}
