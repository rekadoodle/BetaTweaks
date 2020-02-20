// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.List;
import org.lwjgl.input.Keyboard;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            ServerNBTStorage

public class GuiScreenServerList extends GuiScreen
{

    public GuiScreenServerList(GuiScreen guiscreen, ServerNBTStorage servernbtstorage)
    {
        field_35319_a = guiscreen;
        field_35318_c = servernbtstorage;
    }

    public void updateScreen()
    {
        field_35317_b.updateCursorCounter();
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Join Server"));
        controlList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, stringtranslate.translateKey("gui.cancel")));
        field_35317_b = new GuiTextField(this, fontRenderer, width / 2 - 100, 116, 200, 20, field_35318_c.field_35793_b);
        field_35317_b.setMaxStringLength(128);
        ((GuiButton)controlList.get(0)).enabled = field_35317_b.getText().length() > 0;
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
            field_35319_a.deleteWorld(false, 0);
        } else
        if(guibutton.id == 0)
        {
            field_35318_c.field_35793_b = field_35317_b.getText();
            field_35319_a.deleteWorld(true, 0);
        }
    }

    protected void keyTyped(char c, int i)
    {
        field_35317_b.textboxKeyTyped(c, i);
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }
        ((GuiButton)controlList.get(0)).enabled = field_35317_b.getText().length() > 0;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        field_35317_b.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j, float f)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Direct Connect", width / 2, (height / 4 - 60) + 20, 0xffffff);
        drawString(fontRenderer, "Server Address", width / 2 - 100, 100, 0xa0a0a0);
        field_35317_b.drawTextBox();
        super.drawScreen(i, j, f);
    }

    private GuiScreen field_35319_a;
    private GuiTextField field_35317_b;
    private ServerNBTStorage field_35318_c;
}
