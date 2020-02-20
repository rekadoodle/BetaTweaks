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

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiSlotServer, CompressedStreamTools, NBTTagCompound, 
//            NBTTagList, ServerNBTStorage, StringTranslate, GuiButton, 
//            GuiYesNo, GuiScreenServerList, StatCollector, GuiScreenAddServer, 
//            GuiConnecting, Packet, ChatAllowedCharacters, FontRenderer

public class GuiMultiplayerMenu extends GuiScreen
{

    public GuiMultiplayerMenu(GuiScreen guiscreen)
    {
        field_35340_f = new ArrayList();
        field_35341_g = -1;
        field_35346_k = false;
        field_35353_s = false;
        field_35352_t = false;
        field_35351_u = false;
        field_35350_v = null;
        field_35349_w = null;
        parentScreen = guiscreen;
    }

    public void updateScreen()
    {
    }

    public void initGui()
    {
        func_35324_p();
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        field_35342_d = new GuiSlotServer(this);
        func_35337_c();
    }

    private void func_35324_p()
    {
        try
        {
            NBTTagCompound nbttagcompound = CompressedStreamToolsMP.func_35622_a(new File(mc.getMinecraftDir(), "servers.dat"));
            NBTTagList nbttaglist = nbttagcompound.getTagList("servers");
            field_35340_f.clear();
            for(int i = 0; i < nbttaglist.tagCount(); i++)
            {
                field_35340_f.add(ServerNBTStorage.func_35788_a((NBTTagCompound)nbttaglist.tagAt(i)));
            }

        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void func_35323_q()
    {
        try
        {
            NBTTagList nbttaglist = new NBTTagList();
            for(int i = 0; i < field_35340_f.size(); i++)
            {
                nbttaglist.setTag(((ServerNBTStorage)field_35340_f.get(i)).func_35789_a());
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

    public void func_35337_c()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        controlList.add(field_35347_h = new GuiButton(7, width / 2 - 154, height - 28, 70, 20, "Edit"));
        controlList.add(field_35345_j = new GuiButton(2, width / 2 - 74, height - 28, 70, 20, "Delete"));
        controlList.add(field_35348_i = new GuiButton(1, width / 2 - 154, height - 52, 100, 20, "Join Server"));
        controlList.add(new GuiButton(4, width / 2 - 50, height - 52, 100, 20, "Direct Connect"));
        controlList.add(new GuiButton(3, width / 2 + 4 + 50, height - 52, 100, 20, "Add server"));
        controlList.add(new GuiButton(8, width / 2 + 4, height - 28, 70, 20, "Refresh"));
        controlList.add(new GuiButton(0, width / 2 + 4 + 76, height - 28, 75, 20, stringtranslate.translateKey("gui.cancel")));
        boolean flag = field_35341_g >= 0 && field_35341_g < field_35342_d.getSize();
        field_35348_i.enabled = flag;
        field_35347_h.enabled = flag;
        field_35345_j.enabled = flag;
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 2)
        {
            String s = ((ServerNBTStorage)field_35340_f.get(field_35341_g)).field_35795_a;
            if(s != null)
            {
                field_35346_k = true;
                StringTranslate stringtranslate = StringTranslate.getInstance();
                String s1 = "Are you sure you want to remove this server?";
                String s2 = (new StringBuilder()).append("'").append(s).append("' ").append("will be lost forever! (A long time!)").toString();
                String s3 = "Delete";
                String s4 = stringtranslate.translateKey("gui.cancel");
                GuiYesNo guiyesno = new GuiYesNo(this, s1, s2, s3, s4, field_35341_g);
                mc.displayGuiScreen(guiyesno);
            }
        } else
        if(guibutton.id == 1)
        {
            func_35322_a(field_35341_g);
        } else
        if(guibutton.id == 4)
        {
            field_35351_u = true;
            mc.displayGuiScreen(new GuiScreenServerList(this, field_35349_w = new ServerNBTStorage("Minecraft Server", "")));
        } else
        if(guibutton.id == 3)
        {
            field_35353_s = true;
            mc.displayGuiScreen(new GuiScreenAddServer(this, field_35349_w = new ServerNBTStorage("Minecraft Server", "")));
        } else
        if(guibutton.id == 7)
        {
            field_35352_t = true;
            ServerNBTStorage servernbtstorage = (ServerNBTStorage)field_35340_f.get(field_35341_g);
            mc.displayGuiScreen(new GuiScreenAddServer(this, field_35349_w = new ServerNBTStorage(servernbtstorage.field_35795_a, servernbtstorage.field_35793_b)));
        } else
        if(guibutton.id == 0)
        {
            mc.displayGuiScreen(parentScreen);
        } else
        if(guibutton.id == 8)
        {
            mc.displayGuiScreen(new GuiMultiplayerMenu(parentScreen));
        } else
        {
            field_35342_d.actionPerformed(guibutton);
        }
    }

    public void deleteWorld(boolean flag, int i)
    {
        if(field_35346_k)
        {
            field_35346_k = false;
            if(flag)
            {
                field_35340_f.remove(i);
                func_35323_q();
            }
            mc.displayGuiScreen(this);
        } else
        if(field_35351_u)
        {
            field_35351_u = false;
            if(flag)
            {
                func_35330_a(field_35349_w);
            } else
            {
                mc.displayGuiScreen(this);
            }
        } else
        if(field_35353_s)
        {
            field_35353_s = false;
            if(flag)
            {
                field_35340_f.add(field_35349_w);
                func_35323_q();
            }
            mc.displayGuiScreen(this);
        } else
        if(field_35352_t)
        {
            field_35352_t = false;
            if(flag)
            {
                ServerNBTStorage servernbtstorage = (ServerNBTStorage)field_35340_f.get(field_35341_g);
                servernbtstorage.field_35795_a = field_35349_w.field_35795_a;
                servernbtstorage.field_35793_b = field_35349_w.field_35793_b;
                func_35323_q();
            }
            mc.displayGuiScreen(this);
        }
    }

    private int parseIntWithDefault(String s, int i)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch(Exception exception)
        {
            return i;
        }
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(2));
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j, float f)
    {
        field_35350_v = null;
        StringTranslate stringtranslate = StringTranslate.getInstance();
        drawDefaultBackground();
        field_35342_d.passvars(i, j);
        field_35342_d.drawScreen(i, j, f);
        drawCenteredString(fontRenderer, stringtranslate.translateKey("multiplayer.title"), width / 2, 20, 0xffffff);
        super.drawScreen(i, j, f);
        if(field_35350_v != null)
        {
            func_35325_a(field_35350_v, i, j);
        }
    }

    private void func_35322_a(int i)
    {
        func_35330_a((ServerNBTStorage)field_35340_f.get(i));
    }

    private void func_35330_a(ServerNBTStorage servernbtstorage)
    {
        String s = servernbtstorage.field_35793_b;
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
        mc.displayGuiScreen(new GuiConnecting(mc, as[0], as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565)));
    }

    private void func_35328_b(ServerNBTStorage servernbtstorage)
        throws IOException
    {
        String s = servernbtstorage.field_35793_b;
        String as[] = s.split(":");
        if(s.startsWith("["))
        {
            int i = s.indexOf("]");
            if(i > 0)
            {
                String s2 = s.substring(1, i);
                String s3 = s.substring(i + 1).trim();
                if(s3.startsWith(":") && s3.length() > 0)
                {
                    s3 = s3.substring(1);
                    as = new String[2];
                    as[0] = s2;
                    as[1] = s3;
                } else
                {
                    as = new String[1];
                    as[0] = s2;
                }
            }
        }
        if(as.length > 2)
        {
            as = new String[1];
            as[0] = s;
        }
        String s1 = as[0];
        int j = as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565);
        Socket socket = null;
        DataInputStream datainputstream = null;
        DataOutputStream dataoutputstream = null;
        try
        {
            socket = new Socket();
            socket.setSoTimeout(3000);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(18);
            socket.connect(new InetSocketAddress(s1, j), 3000);
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
            servernbtstorage.field_35791_d = (new StringBuilder()).append("\2477").append(s4).toString();
            if(l >= 0 && i1 > 0)
            {
                servernbtstorage.field_35794_c = (new StringBuilder()).append("\2477").append(l).append("\2478/\2477").append(i1).toString();
            } else
            {
                servernbtstorage.field_35794_c = "\2478???";
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

    protected void func_35325_a(String s, int i, int j)
    {
        if(s == null)
        {
            return;
        } else
        {
            int k = i + 12;
            int l = j - 12;
            int i1 = fontRenderer.getStringWidth(s);
            drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, 0xc0000000, 0xc0000000);
            fontRenderer.drawStringWithShadow(s, k, l, -1);
            return;
        }
    }

    static List func_35320_a(GuiMultiplayerMenu guimultiplayer)
    {
        return guimultiplayer.field_35340_f;
    }

    static int func_35326_a(GuiMultiplayerMenu guimultiplayer, int i)
    {
        return guimultiplayer.field_35341_g = i;
    }

    static int func_35333_b(GuiMultiplayerMenu guimultiplayer)
    {
        return guimultiplayer.field_35341_g;
    }

    static GuiButton func_35329_c(GuiMultiplayerMenu guimultiplayer)
    {
        return guimultiplayer.field_35348_i;
    }

    static GuiButton func_35334_d(GuiMultiplayerMenu guimultiplayer)
    {
        return guimultiplayer.field_35347_h;
    }

    static GuiButton func_35339_e(GuiMultiplayerMenu guimultiplayer)
    {
        return guimultiplayer.field_35345_j;
    }

    static void func_35332_b(GuiMultiplayerMenu guimultiplayer, int i)
    {
        guimultiplayer.func_35322_a(i);
    }

    static Object func_35321_g()
    {
        return field_35343_b;
    }

    static int func_35338_m()
    {
        return field_35344_a;
    }

    static int func_35331_n()
    {
        return field_35344_a++;
    }

    static void func_35336_a(GuiMultiplayerMenu guimultiplayer, ServerNBTStorage servernbtstorage)
        throws IOException
    {
        guimultiplayer.func_35328_b(servernbtstorage);
    }

    static int func_35335_o()
    {
        return field_35344_a--;
    }

    static String func_35327_a(GuiMultiplayerMenu guimultiplayer, String s)
    {
        return guimultiplayer.field_35350_v = s;
    }

    private static int field_35344_a = 0;
    private static Object field_35343_b = new Object();
    private GuiScreen parentScreen;
    private GuiSlotServer field_35342_d;
    private List field_35340_f;
    private int field_35341_g;
    private GuiButton field_35347_h;
    private GuiButton field_35348_i;
    private GuiButton field_35345_j;
    private boolean field_35346_k;
    private boolean field_35353_s;
    private boolean field_35352_t;
    private boolean field_35351_u;
    private String field_35350_v;
    private ServerNBTStorage field_35349_w;

}
