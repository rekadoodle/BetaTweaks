// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.util.List;
import org.lwjgl.input.Keyboard;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            ServerNBTStorage

public class GuiScreenAddServer extends GuiScreen
{

    public GuiScreenAddServer(GuiScreen guiscreen, ServerNBTStorage servernbtstorage)
    {
        field_35362_a = guiscreen;
        field_35359_d = servernbtstorage;
    }

    public void updateScreen()
    {
        field_35361_c.updateCursorCounter();
        field_35360_b.updateCursorCounter();
    }

    public void initGui()
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Done"));
        controlList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, stringtranslate.translateKey("gui.cancel")));
        field_35361_c = new GuiTextField(this, fontRenderer, width / 2 - 100, 76, 200, 20, field_35359_d.field_35795_a);
        field_35361_c.isFocused = true;
        field_35361_c.setMaxStringLength(32);
        field_35360_b = new GuiTextField(this, fontRenderer, width / 2 - 100, 116, 200, 20, field_35359_d.field_35793_b);
        field_35360_b.setMaxStringLength(128);
        ((GuiButton)controlList.get(0)).enabled = field_35360_b.getText().length() > 0 && field_35361_c.getText().length() > 0;
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
            field_35362_a.deleteWorld(false, 0);
        } else
        if(guibutton.id == 0)
        {
            field_35359_d.field_35795_a = field_35361_c.getText();
            field_35359_d.field_35793_b = field_35360_b.getText();
            field_35362_a.deleteWorld(true, 0);
        }
    }

    protected void keyTyped(char c, int i)
    {
        field_35361_c.textboxKeyTyped(c, i);
        field_35360_b.textboxKeyTyped(c, i);
        if(c == '\t')
        {
            if(field_35361_c.isFocused)
            {
                field_35361_c.isFocused = false;
                field_35360_b.isFocused = true;
            } else
            {
                field_35361_c.isFocused = true;
                field_35360_b.isFocused = false;
            }
        }
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }
        ((GuiButton)controlList.get(0)).enabled = field_35360_b.getText().length() > 0 && field_35361_c.getText().length() > 0;
        if(((GuiButton)controlList.get(0)).enabled)
        {
            String s = field_35360_b.getText().trim();
            String as[] = s.split(":");
            if(as.length > 2)
            {
                ((GuiButton)controlList.get(0)).enabled = false;
            }
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        field_35360_b.mouseClicked(i, j, k);
        field_35361_c.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j, float f)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Edit Server Info", width / 2, (height / 4 - 60) + 20, 0xffffff);
        drawString(fontRenderer, "Server Name", width / 2 - 100, 63, 0xa0a0a0);
        drawString(fontRenderer, "Server Address", width / 2 - 100, 104, 0xa0a0a0);
        field_35361_c.drawTextBox();
        field_35360_b.drawTextBox();
        super.drawScreen(i, j, f);
    }

    private GuiScreen field_35362_a;
    private GuiTextField field_35360_b;
    private GuiTextField field_35361_c;
    private ServerNBTStorage field_35359_d;
}
