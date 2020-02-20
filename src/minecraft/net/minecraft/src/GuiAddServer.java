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
        nameTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100, 76, 200, 20, server.name);
        nameTextbox.isFocused = true;
        nameTextbox.setMaxStringLength(32);
        ipTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100, 116, 200, 20, server.ip);
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
        } else
        if(guibutton.id == 0)
        {
        	server.name = nameTextbox.getText();
            server.ip = ipTextbox.getText();
            parentScreen.deleteWorld(true, 0);
        }
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
        drawString(fontRenderer, "Server Name", width / 2 - 100, 63, 0xa0a0a0);
        drawString(fontRenderer, "Server Address", width / 2 - 100, 104, 0xa0a0a0);
        nameTextbox.drawTextBox();
        ipTextbox.drawTextBox();
        super.drawScreen(i, j, f);
    }

    private GuiScreen parentScreen;
    private GuiTextField nameTextbox;
    private GuiTextField ipTextbox;
    private ServerData server;
}
