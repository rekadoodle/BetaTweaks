// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiSlotServer, CompressedStreamTools, NBTTagCompound, 
//            NBTTagList, ServerNBTStorage, StringTranslate, GuiButton, 
//            GuiYesNo, GuiScreenServerList, StatCollector, GuiScreenAddServer, 
//            GuiConnecting, Packet, ChatAllowedCharacters, FontRenderer

public class GuiMultiplayerMenu extends GuiScreen
{

    public GuiMultiplayerMenu(GuiScreen guiscreen)
    {
    	parentScreen = guiscreen;
    }

    public void initGui()
    {
    	try {
            NBTTagCompound nbttagcompound = CompressedStreamToolsMP.func_35622_a(new File(mc.getMinecraftDir(), "servers.dat"));
            NBTTagList nbttaglist = nbttagcompound.getTagList("servers");
            serverList.clear();
            for(int i = 0; i < nbttaglist.tagCount(); i++) {
            	serverList.add(ServerData.func_35788_a((NBTTagCompound)nbttaglist.tagAt(i)));
            }
        }
        catch(Exception exception) { exception.printStackTrace(); }
    	
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        slots = new GuiSlotServer(this);
        slots.registerScrollButtons(controlList, 9, 10);
        StringTranslate stringtranslate = StringTranslate.getInstance();
        controlList.add(new GuiButton(7, width / 2 - 154, height - 28, 70, 20, "Edit"));
        controlList.add(new GuiButton(2, width / 2 - 74, height - 28, 70, 20, "Delete"));
        controlList.add(new GuiButton(1, width / 2 - 154, height - 52, 100, 20, "Join Server"));
        controlList.add(new GuiButton(4, width / 2 - 50, height - 52, 100, 20, "Direct Connect"));
        controlList.add(new GuiButton(3, width / 2 + 4 + 50, height - 52, 100, 20, "Add server"));
        controlList.add(new GuiButton(8, width / 2 + 4, height - 28, 70, 20, "Refresh"));
        controlList.add(new GuiButton(0, width / 2 + 4 + 76, height - 28, 75, 20, stringtranslate.translateKey("gui.cancel")));
        onSlotChanged();
    }
    
    public void handleMouseInput() {
    	slots.scroll(Mouse.getEventDWheel());
		super.handleMouseInput();
	}

    private void saveServerList()
    {
        try
        {
            NBTTagList nbttaglist = new NBTTagList();
            for(int i = 0; i < serverList.size(); i++)
            {
                nbttaglist.setTag(((ServerData)serverList.get(i)).func_35789_a());
            }

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", nbttaglist);
            CompressedStreamToolsMP.func_35621_a(nbttagcompound, new File(mc.getMinecraftDir(), "servers.dat"));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id == 2) {
            String s = ((ServerData)serverList.get(selectedServerIndex)).name;
            if(s != null)
            {
            	deleteServer = true;
                StringTranslate stringtranslate = StringTranslate.getInstance();
                String s1 = "Are you sure you want to remove this server?";
                String s2 = (new StringBuilder()).append("'").append(s).append("' ").append("will be lost forever! (A long time!)").toString();
                String s3 = "Delete";
                String s4 = stringtranslate.translateKey("gui.cancel");
                GuiYesNo guiyesno = new GuiYesNo(this, s1, s2, s3, s4, selectedServerIndex);
                mc.displayGuiScreen(guiyesno);
            }
        } 
        else if(guibutton.id == 1) {
        	joinServer(((ServerData)serverList.get(selectedServerIndex)).ip);
        } 
        else if(guibutton.id == 4) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } 
        else if(guibutton.id == 3) {
        	addServer = true;
            mc.displayGuiScreen(new GuiAddServer(this, server = new ServerData("Minecraft Server", "")));
        } 
        else if(guibutton.id == 7) {
        	editServer = true;
            ServerData serverToEdit = (ServerData)serverList.get(selectedServerIndex);
            mc.displayGuiScreen(new GuiAddServer(this, server = new ServerData(serverToEdit.name, serverToEdit.ip)));
        } 
        else if(guibutton.id == 0) {
            mc.displayGuiScreen(parentScreen);
        }
        else if(guibutton.id == 8) {
            mc.displayGuiScreen(new GuiMultiplayerMenu(parentScreen));
        } 
        else {
        	slots.actionPerformed(guibutton);
        }
    }

    public void deleteWorld(boolean flag, int i)
    {
        if(deleteServer)
        {
        	deleteServer = false;
            if(flag)
            {
            	serverList.remove(i);
            	saveServerList();
            }
            mc.displayGuiScreen(this);
        } else
        if(addServer)
        {
        	addServer = false;
            if(flag)
            {
            	serverList.add(server);
            	saveServerList();
            }
            mc.displayGuiScreen(this);
        } else
        if(editServer)
        {
        	editServer = false;
            if(flag)
            {
            	ServerData server = (ServerData)serverList.get(selectedServerIndex);
                server.name = server.name;
                server.ip = server.ip;
                saveServerList();
            }
            mc.displayGuiScreen(this);
        }
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
        	actionPerformed((GuiButton)controlList.get(2));
        }
        else super.keyTyped(c, i);
    }

    public void drawScreen(int i, int j, float f)
    {
    	tooltip = null;
        StringTranslate stringtranslate = StringTranslate.getInstance();
        drawDefaultBackground();
        slots.setMousePos(i, j);
        slots.drawScreen(i, j, f);
        drawCenteredString(fontRenderer, stringtranslate.translateKey("multiplayer.title"), width / 2, 20, 0xffffff);
        super.drawScreen(i, j, f);
        if(tooltip != null)
        {
        	int k = i + 12;
            int l = j - 12;
            int i1 = fontRenderer.getStringWidth(tooltip);
            drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, 0xc0000000, 0xc0000000);
            fontRenderer.drawStringWithShadow(tooltip, k, l, -1);
        }
    }
    
    String[] splitIP(String ip) {
    	 String s = ip;
         String as[] = s.split(":");
         if(s.startsWith("["))
         {
             int i = s.indexOf("]");
             if(i > 0)
             {
                 String s1 = s.substring(1, i);
                 String s2 = s.substring(i + 1).trim();
                 if(s2.startsWith(":") && s2.length() > 0)
                 {
                     s2 = s2.substring(1);
                     as = new String[2];
                     as[0] = s1;
                     as[1] = s2;
                 } else
                 {
                     as = new String[1];
                     as[0] = s1;
                 }
             }
         }
         if(as.length > 2)
         {
             as = new String[1];
             as[0] = s;
         }
         int port = 0;
         try
         {
             port =  Integer.parseInt(as[1].trim());
         }
         catch(Exception exception){ port =  25565; }
         
         return new String[] {as[0], Integer.toString(port)};
    }
    
    void joinServer(String address)
    {
    	String[] ip = splitIP(address);
        mc.displayGuiScreen(new GuiConnecting(mc, ip[0], Integer.parseInt(ip[1])));
    }

    void pollServer(ServerData server)
        throws IOException
    {
    	String[] ip = splitIP(server.ip);
        Socket socket = null;
        DataInputStream datainputstream = null;
        DataOutputStream dataoutputstream = null;
        try
        {
            socket = new Socket();
            socket.setSoTimeout(3000);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(18);
            socket.connect(new InetSocketAddress(ip[0], Integer.parseInt(ip[1])), 3000);
            datainputstream = new DataInputStream(socket.getInputStream());
            dataoutputstream = new DataOutputStream(socket.getOutputStream());
            dataoutputstream.write(254);
            if(datainputstream.read() != 255)
            {
                throw new IOException("Bad message");
            }
            String s4 = Packet.readString(datainputstream, 64);
            char ac[] = s4.toCharArray();
            for(int k = 0; k < ac.length; k++)
            {
                if(ac[k] != '\247' && ChatAllowedCharacters.allowedCharacters.indexOf(ac[k]) < 0)
                {
                    ac[k] = '?';
                }
            }

            s4 = new String(ac);
            String as1[] = s4.split("\247");
            s4 = as1[0];
            int l = -1;
            int i1 = -1;
            try
            {
                l = Integer.parseInt(as1[1]);
                i1 = Integer.parseInt(as1[2]);
            }
            catch(Exception exception) { }
            server.status = (new StringBuilder()).append("\2477").append(s4).toString();
            if(l >= 0 && i1 > 0)
            {
            	server.playerCount = (new StringBuilder()).append("\2477").append(l).append("\2478/\2477").append(i1).toString();
            } else
            {
            	server.playerCount = "\2478???";
            }
        }
        finally
        {
            try
            {
                if(datainputstream != null)
                {
                    datainputstream.close();
                }
            }
            catch(Throwable throwable) { }
            try
            {
                if(dataoutputstream != null)
                {
                    dataoutputstream.close();
                }
            }
            catch(Throwable throwable1) { }
            try
            {
                if(socket != null)
                {
                    socket.close();
                }
            }
            catch(Throwable throwable2) { }
        }
    }
    
    Boolean onSlotChanged() {
    	Boolean serverIsSelected = selectedServerIndex >= 0 && selectedServerIndex < slots.getSize();
    	for(int i = 0; i < 3; i++) {
    		((GuiButton)controlList.get(i)).enabled = serverIsSelected;
    	}
    	return serverIsSelected;
    }

    List getServerList()
    {
        return serverList;
    }

    static Object getSync()
    {
        return syncObj;
    }

    void setTooltip(String s)
    {
        tooltip = s;
    }

    int pingCount = 0;
    private static Object syncObj = new Object();
    private GuiScreen parentScreen;
    private GuiSlotServer slots;
    private List serverList = new ArrayList();
    int selectedServerIndex = -1;
    private boolean deleteServer = false;
    private boolean addServer = false;
    private boolean editServer = false;
    private String tooltip = null;
    private ServerData server = null;

}
