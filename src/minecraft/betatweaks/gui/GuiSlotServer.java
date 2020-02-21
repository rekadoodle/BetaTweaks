// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package betatweaks.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import betatweaks.ServerData;
import betatweaks.ThreadPollServers;
import betatweaks.Utils;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.Tessellator;
import net.minecraft.src.mod_BetaTweaks;

// Referenced classes of package net.minecraft.src:
//            GuiSlot, GuiMultiplayer, GuiButton, ServerNBTStorage, 
//            ThreadPollServers, FontRenderer, RenderEngine, Tessellator

class GuiSlotServer extends GuiSlot
{
	private int posX;
    private int posY;
    private GuiButton buttonScrollUp = new GuiButton(9, 0, 0, null);
    private GuiButton buttonScrollDown = new GuiButton(10, 0, 0, null);

	public GuiSlotServer(GuiMultiplayerMenu guimultiplayer)
    {
        super(Utils.mc, guimultiplayer.width, guimultiplayer.height, 32, guimultiplayer.height - 64, 36);
        menu = guimultiplayer;
    }
	
	public void scroll(int i)
    {
		if(i < 0) {
			actionPerformed(buttonScrollDown);
		}
		else if(i > 0) {
			actionPerformed(buttonScrollUp);
		}
		
    }

    protected int getSize()
    {
        return menu.getServerList().size();
    }

    protected void elementClicked(int slotIndex, boolean doubleClick)
    {
    	menu.selectedServerIndex = slotIndex;
        if(menu.onSlotChanged() && doubleClick)
        {
        	menu.joinServer(((ServerData)menu.getServerList().get(slotIndex)).ip);
        }
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == menu.selectedServerIndex;
    }

    protected int getContentHeight()
    {
        return menu.getServerList().size() * 36;
    }

    protected void drawBackground()
    {
    	menu.drawDefaultBackground();
    }

    protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator)
    {
    	ServerData server = (ServerData)menu.getServerList().get(i);
        synchronized(GuiMultiplayerMenu.getSync())
        {
            if(server.shouldPing && menu.pingCount < 5 && !server.pinged)
            {
            	server.pinged = true;
            	server.ping = -2L;
            	server.status = "";
            	server.playerCount = "";
                menu.pingCount++;
                (new ThreadPollServers(menu, server)).start();
            }
        }
        menu.drawString(Utils.mc.fontRenderer, server.name, j + 2, k + 1, 0xffffff);
        menu.drawString(Utils.mc.fontRenderer, server.status, j + 2, k + 12, 0x808080);
        menu.drawString(Utils.mc.fontRenderer, server.playerCount, (j + 215) - Utils.mc.fontRenderer.getStringWidth(server.playerCount), k + 12, 0x808080);
        menu.drawString(Utils.mc.fontRenderer, server.ip, j + 2, k + 12 + 11, 0x303030);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Utils.mc.renderEngine.bindTexture(Utils.mc.renderEngine.getTexture(mod_BetaTweaks.resources + "/icons.png"));
        int i1 = 0;
        int j1 = 0;
        String s = null;
        if(server.pinged && server.ping != -2L)
        {
            i1 = 0;
            j1 = 0;
            if(server.ping < 0L)
            {
                j1 = 5;
            } else
            if(server.ping < 150L)
            {
                j1 = 0;
            } else
            if(server.ping < 300L)
            {
                j1 = 1;
            } else
            if(server.ping < 600L)
            {
                j1 = 2;
            } else
            if(server.ping < 1000L)
            {
                j1 = 3;
            } else
            {
                j1 = 4;
            }
            if(server.ping < 0L)
            {
                s = "(no connection)";
            } else
            {
                s = (new StringBuilder()).append(server.ping).append("ms").toString();
            }
        } else
        {
        	i1 = 1;
        	j1 = (int)(System.currentTimeMillis() / 100L + (long)(i * 2) & 7L);
        	if(j1 > 4)
        	{	
            	j1 = 8 - j1;
            }
            s = "Polling..";
        	
        }
        if(server.shouldPing)
        menu.drawTexturedModalRect(j + 205, k, 0 + i1 * 10, 176 + j1 * 8, 10, 8);
        byte byte0 = 4;
        if(posX >= (j + 205) - byte0 && posY >= k - byte0 && posX <= j + 205 + 10 + byte0 && posY <= k + 8 + byte0)
        {
        	menu.setTooltip(s);
        }
    }

    final GuiMultiplayerMenu menu; /* synthetic field */

	public void setMousePos(int i, int j) {
		this.posX = i;
		this.posY = j;
	}
}
