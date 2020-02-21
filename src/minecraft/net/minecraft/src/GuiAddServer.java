// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import org.lwjgl.input.Keyboard;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            ServerNBTStorage

public class GuiAddServer extends GuiScreen
{

    public GuiAddServer(GuiScreen guiscreen, ServerData servernbtstorage)
    {
    	parentScreen = guiscreen;
        server = servernbtstorage;
    }

    public void updateScreen()
    {
    	nameTextbox.updateCursorCounter();
    	ipTextbox.updateCursorCounter();
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Done"));
        controlList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, stringtranslate.translateKey("gui.cancel")));
        controlList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 - 12 - 12, 100, 20, "Don't Ping Server"));
        controlList.add(new GuiButton(3, width / 2, height / 4 + 96 - 12 - 12, 100, 20, "Ping Server"));
        ((GuiButton)controlList.get(2)).enabled = server.shouldPing;
        ((GuiButton)controlList.get(3)).enabled = !server.shouldPing;
        nameTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100, 76 - 12, 200, 20, server.name);
        nameTextbox.isFocused = true;
        nameTextbox.setMaxStringLength(32);
        ipTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100, 116 - 12, 200, 20, server.ip);
        ipTextbox.setMaxStringLength(128);
        ((GuiButton)controlList.get(0)).enabled = ipTextbox.getText().length() > 0 && nameTextbox.getText().length() > 0;
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
        if(guibutton.id == 1)
        {
        	parentScreen.deleteWorld(false, 0);
        } 
        else if(guibutton.id == 0)
        {
        	server.name = nameTextbox.getText();
            server.ip = ipTextbox.getText();
            parentScreen.deleteWorld(true, 0);
        }
        else if(guibutton.id == 2)
        {
        	server.shouldPing = false;
        	((GuiButton)controlList.get(2)).enabled = server.shouldPing;
        	((GuiButton)controlList.get(3)).enabled = !server.shouldPing;
        }
        else if(guibutton.id == 3)
        {
        	server.name = nameTextbox.getText();
            server.ip = ipTextbox.getText();
        	StringTranslate stringtranslate = StringTranslate.getInstance();
            String s1 = "Are you sure you want to start pinging this server?";
            String s2 = "You may receive 'End of Stream' as servers can think you are DDOSing";
            String s3 = "Yes, I understand";
            String s4 = stringtranslate.translateKey("gui.cancel");
            //mc.displayGuiScreen(new GuiYesNo(this, s1, s2, s3, s4, 0));
            server.shouldPing = true;
            ((GuiButton)controlList.get(2)).enabled = server.shouldPing;
        	((GuiButton)controlList.get(3)).enabled = !server.shouldPing;
        }
    }
    
    public void deleteWorld(boolean flag, int i)
    {
        if(flag) {
        	((GuiButton)controlList.get(3)).enabled = false;
        	((GuiButton)controlList.get(2)).enabled = true;
        	server.shouldPing = true;
        }
        mc.displayGuiScreen(this);
        
    }

    protected void keyTyped(char c, int i)
    {
    	nameTextbox.textboxKeyTyped(c, i);
    	ipTextbox.textboxKeyTyped(c, i);
        if(c == '\t')
        {
            if(nameTextbox.isFocused)
            {
            	nameTextbox.isFocused = false;
            	ipTextbox.isFocused = true;
            } else
            {
            	nameTextbox.isFocused = true;
            	ipTextbox.isFocused = false;
            }
        }
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }
        ((GuiButton)controlList.get(0)).enabled = ipTextbox.getText().length() > 0 && nameTextbox.getText().length() > 0;
        if(((GuiButton)controlList.get(0)).enabled)
        {
            String s = ipTextbox.getText().trim();
            String as[] = s.split(":");
            if(as.length > 2)
            {
                ((GuiButton)controlList.get(0)).enabled = false;
            }
        }
        super.keyTyped(c, i);
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        ipTextbox.mouseClicked(i, j, k);
        nameTextbox.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j, float f)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Edit Server Info", width / 2, (height / 4 - 60) + 20, 0xffffff);
        drawString(fontRenderer, "Server Name", width / 2 - 100, 63 - 12, 0xa0a0a0);
        drawString(fontRenderer, "Server Address", width / 2 - 100, 104 - 12, 0xa0a0a0);
        if(server.shouldPing) drawString(fontRenderer, "WARNING: Pinging server may cause End Of Stream with DDOS protection", width / 2 - 175, 104 + 100 + 16, 0xa0a0a0);
        nameTextbox.drawTextBox();
        ipTextbox.drawTextBox();
        super.drawScreen(i, j, f);
    }

    private GuiScreen parentScreen;
    private GuiTextField nameTextbox;
    private GuiTextField ipTextbox;
    private ServerData server;
}
