package net.minecraft.src.betatweaks.gui;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiConnecting;
import net.minecraft.src.GuiMultiplayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiYesNo;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.mod_BetaTweaks;
import net.minecraft.src.betatweaks.BetaTweaks;
import net.minecraft.src.betatweaks.CompressedStreamToolsMP;
import net.minecraft.src.betatweaks.ServerData;
import net.minecraft.src.betatweaks.Utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiServerList extends GuiScreen
{

    public GuiServerList()
    {
    	this(Utils.getParentScreen());
    }

    public GuiServerList(GuiScreen parentScreen) {
    	this.parentScreen = parentScreen;
    	BetaTweaks.dontOverride = true;
    	NBTTagCompound nbt = null;
    	try {
            nbt = CompressedStreamToolsMP.func_35622_a(new File(Minecraft.getMinecraftDir(), "servers.dat"));
        }
        catch(Exception exception) { exception.printStackTrace(); }
    	if(nbt != null) {
        	NBTTagList nbtList = nbt.getTagList("servers");
            for(int i = 0; i < nbtList.tagCount(); i++) {
            	serverList.add(new ServerData((NBTTagCompound)nbtList.tagAt(i)));
            }
        }
	}

    @Override
	@SuppressWarnings("unchecked")
	public void initGui()
    {
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
    
	@Override
    public void handleMouseInput() {
    	slots.scroll(Mouse.getEventDWheel());
		super.handleMouseInput();
	}

    private void saveServerList()
    {
        NBTTagList nbtList = new NBTTagList();
        for(int i = 0; i < serverList.size(); i++)
        {
        	nbtList.setTag(serverList.get(i).saveToNBT());
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("servers", nbtList);
        try
        {
            CompressedStreamToolsMP.func_35621_a(nbt, new File(Minecraft.getMinecraftDir(), "servers.dat"));
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id == 2) {
            String s = serverList.get(selectedServerIndex).name;
            if(s != null)
            {
                StringTranslate stringtranslate = StringTranslate.getInstance();
                String s1 = "Are you sure you want to remove this server?";
                String s2 = (new StringBuilder()).append("'").append(s).append("' ").toString();
                String s3 = "Yes";
                String s4 = stringtranslate.translateKey("gui.cancel");
                GuiYesNo guiyesno = new GuiYesNo(this, s1, s2, s3, s4, selectedServerIndex);
                mc.displayGuiScreen(guiyesno);
            }
        } 
        else if(guibutton.id == 1) {
        	joinServer(serverList.get(selectedServerIndex).ip);
        } 
        else if(guibutton.id == 4) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } 
        else if(guibutton.id == 3) {
            mc.displayGuiScreen(new GuiAddServer(this));
        } 
        else if(guibutton.id == 7) {
            ServerData serverToEdit = serverList.get(selectedServerIndex);
            mc.displayGuiScreen(new GuiAddServer(this, new ServerData(serverToEdit)));
        } 
        else if(guibutton.id == 0) {
        	BetaTweaks.dontOverride = false;
            mc.displayGuiScreen(parentScreen);
        }
        else if(guibutton.id == 8) {
            mc.displayGuiScreen(new GuiServerList(parentScreen));
        } 
        else {
        	slots.actionPerformed(guibutton);
        }
    }

    @Override
    public void deleteWorld(boolean flag, int i)
    {
    	if(flag)
        {
        	serverList.remove(i);
        	saveServerList();
        }
        mc.displayGuiScreen(this);
    }
    
    public void addServer(ServerData server) {
    	serverList.add(server);
    	saveServerList();
    }
    
    public void editSelectedServer(ServerData server) {
    	serverList.set(selectedServerIndex, server);
    	saveServerList();
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
        	actionPerformed((GuiButton)controlList.get(2));
        }
        else {
        	if(i == 1) BetaTweaks.dontOverride = false;
        	super.keyTyped(c, i);
        }
    }

    @Override
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
    
    private String[] splitIP(String ip) {
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
    
    public void joinServer(String address)
    {
    	BetaTweaks.dontOverride = false;
    	String[] ip = splitIP(address);
        mc.displayGuiScreen(new GuiConnecting(mc, ip[0], Integer.parseInt(ip[1])));
    }

    public void pollServer(ServerData server)
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
    
    public boolean onSlotChanged() {
    	boolean serverIsSelected = selectedServerIndex >= 0 && selectedServerIndex < slots.getSize();
    	for(int i = 0; i < 3; i++) {
    		((GuiButton)controlList.get(i)).enabled = serverIsSelected;
    	}
    	return serverIsSelected;
    }

    public List<ServerData> getServerList()
    {
        return serverList;
    }

    public static Object getSync()
    {
        return syncObj;
    }

    public void setTooltip(String s)
    {
        tooltip = s;
    }

    public int serversBeingPinged = 0;
    private static Object syncObj = new Object();
    private GuiScreen parentScreen;
    private GuiSlotServer slots;
    private List<ServerData> serverList = new ArrayList<ServerData>();
    int selectedServerIndex = -1;
    private String tooltip = null;

}
