package net.minecraft.src.betatweaks.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.Tessellator;
import net.minecraft.src.mod_BetaTweaks;
import net.minecraft.src.betatweaks.ServerData;
import net.minecraft.src.betatweaks.ThreadPollProxy;
import net.minecraft.src.betatweaks.ThreadPollServers;
import net.minecraft.src.betatweaks.Utils;


public class GuiSlotServer extends GuiSlot
{
	private int posX;
    private int posY;
    private GuiButton buttonScrollUp = new GuiButton(9, 0, 0, null);
    private GuiButton buttonScrollDown = new GuiButton(10, 0, 0, null);

	public GuiSlotServer(GuiServerList guimultiplayer)
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
    	ServerData server = menu.getServerList().get(i);
    	if(server.pingType != ServerData.DONT_PING && menu.serversBeingPinged < 5 && !server.responded) {
    		server.responded = true;
    		server.responseTime = ServerData.POLLING;
            menu.serversBeingPinged++;
    		synchronized(GuiServerList.getSync())
            {
                if(server.pingType == ServerData.PING_DIRECT)
                {
                    (new ThreadPollServers(menu, server)).start();
                }
                else if(server.pingType == ServerData.PING_VIA_JOHNY_MUFFIN) {
            		try {
            			(new ThreadPollProxy(menu, server)).start();
            		}
            		catch(UnsupportedClassVersionError e) {
            			menu.serversBeingPinged--;
            			server.setConnectionFailed("Java 8 required to request info");
            		}
                }
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
        if(server.responded && server.responseTime != ServerData.POLLING)
        {
            i1 = 0;
            j1 = 0;
            if(server.responseTime == ServerData.OFFLINE) {
                j1 = 5;
            } 
            else if(server.responseTime < 150L) {
                j1 = 0;
            } 
            else if(server.responseTime < 300L) {
                j1 = 1;
            } 
            else if(server.responseTime < 600L) {
                j1 = 2;
            } 
            else if(server.responseTime < 1000L) {
                j1 = 3;
            } 
            else {
                j1 = 4;
            }
            
            if(server.responseTime == ServerData.OFFLINE) {
                s = "(no connection)";
            }
            else if(server.responseTime == ServerData.ONLINE) {
            	s = null;
            }
            else {
                s = (new StringBuilder()).append(server.responseTime).append("ms").toString();
            }
        } else
        {
        	if(server.responseTime == ServerData.POLLING) {
        		i1 = 1;
            	j1 = (int)(System.currentTimeMillis() / 100L + (long)(i * 2) & 7L);
            	if(j1 > 4)
            	{	
                	j1 = 8 - j1;
                }
        	}
        	else {
        		j1 = 8;
        	}
        	
            s = "Polling..";
        }
        if(server.pingType != ServerData.DONT_PING)
        menu.drawTexturedModalRect(j + 205, k, 0 + i1 * 10, 176 + j1 * 8, 10, 8);
        byte byte0 = 4;
        if(posX >= (j + 205) - byte0 && posY >= k - byte0 && posX <= j + 205 + 10 + byte0 && posY <= k + 8 + byte0)
        {
        	menu.setTooltip(s);
        }
    }

    final GuiServerList menu; /* synthetic field */

	public void setMousePos(int i, int j) {
		this.posX = i;
		this.posY = j;
	}
}
