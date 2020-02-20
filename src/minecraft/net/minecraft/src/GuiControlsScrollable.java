// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//			GuiScreen, StringTranslate, GameSettings, GuiSmallButton, 
//			GuiButton, KeyBinding

public class GuiControlsScrollable extends GuiScreen
{
	private GuiScreen parentScreen;
	protected String screenTitle;
	private GameSettings options;
	private GuiListControls guiListControls;
	private Minecraft mc;

	public GuiControlsScrollable(GuiScreen guiscreen, GameSettings gamesettings)
	{
		mc = ModLoader.getMinecraftInstance();
		
		screenTitle = "Controls";
		parentScreen = guiscreen;
		options = gamesettings;
	}

	private int func_20080_j()
	{
		return width / 2 - 155;
	}

	public void initGui()
	{
		StringTranslate stringtranslate = StringTranslate.getInstance();
/*		int i = func_20080_j();
		for(int j = 0; j < options.keyBindings.length; j++)
		{
			controlList.add(new GuiSmallButton(j, i + (j % 2) * 160, height / 6 + 24 * (j >> 1), 70, 20, options.getOptionDisplayString(j)));
			System.out.println(Integer.toString(j) + ": " + Integer.toString(i + (j % 2) * 160));
*///		}

		guiListControls = new GuiListControls(parentScreen.mc, this, options);

		controlList.add(new GuiButton(-200, width / 2 - 100, height - 39, stringtranslate.translateKey("gui.done")));
		screenTitle = stringtranslate.translateKey("controls.title");
	}

	protected void actionPerformed(GuiButton guibutton){
		if(guibutton.id == -200){
			mc.displayGuiScreen(parentScreen);
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		guiListControls.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		guiListControls.mouseMovedOrUp(mouseX, mouseY, button);
		super.mouseMovedOrUp(mouseX, mouseY, button);
	}

	protected void keyTyped(char key, int keyId) {
		
		guiListControls.keyTyped(key, keyId);
		
		
	}
	
	protected void keyTypedWithoutBind(char key, int keyId) {
		super.keyTyped(key, keyId);
	}

	public void handleMouseInput() {
        int amount = Mouse.getEventDWheel();
        if(amount != 0 && guiListControls.getContentHeight() - (guiListControls.bottom - guiListControls.top - 4) > 0) {
        	guiListControls.mouseScrolled(amount);
        }
		super.handleMouseInput();
	}

	public void drawScreen(int i, int j, float f){
		//drawDefaultBackground();
		guiListControls.drawScreen(i, j, f);
		drawCenteredString(fontRenderer, screenTitle, width / 2, 20, 0xffffff);

        super.drawScreen(i, j, f);

	}
}
