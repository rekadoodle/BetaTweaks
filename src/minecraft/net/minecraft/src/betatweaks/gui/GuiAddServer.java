package net.minecraft.src.betatweaks.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.betatweaks.ServerData;

public class GuiAddServer extends GuiScreen
{

    private GuiServerList guiServerList;
    private GuiTextField nameTextbox;
    private GuiTextField ipTextbox;
    private GuiTextField proxyTextbox;
    private ServerData server;
    
    private GuiButton togglePingButton;
    private GuiButton doneButton;
    
    private boolean java8orHigher;
    private boolean newServer = false;
    
    private int relativeHeight;
    private int textHeight;
    
    public GuiAddServer(GuiServerList guiscreen) {
    	this(guiscreen, new ServerData("Minecraft Server", "", ServerData.DONT_PING));
    	newServer = true;
    }
    
    public GuiAddServer(GuiServerList guiscreen, ServerData servernbtstorage)
    {
    	guiServerList = guiscreen;
        server = servernbtstorage;
        java8orHigher = Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8D;
    }

    @Override
    public void updateScreen()
    {
    	nameTextbox.updateCursorCounter();
    	ipTextbox.updateCursorCounter();
    	proxyTextbox.updateCursorCounter();
    }

    @Override
    @SuppressWarnings("unchecked")
	public void initGui()
    {
    	relativeHeight = height / 4 - 40;
    	textHeight = relativeHeight + 40;
    	final int buttonHeight = textHeight + 70;
    	
        Keyboard.enableRepeatEvents(true);
        doneButton = new GuiButton(0, width / 2 - 100, buttonHeight + 12 + 30, "Done");
        togglePingButton = new GuiButton(2, width / 2 - 100, textHeight + 92 - 12 - 12 - 2, 200, 20, "");
        
        controlList.clear();
        controlList.add(doneButton);
        controlList.add(new GuiButton(1, width / 2 - 100, buttonHeight + 24 + 12 + 30, StringTranslate.getInstance().translateKey("gui.cancel")));
        controlList.add(togglePingButton);
        
        String s = nameTextbox != null ? nameTextbox.getText() : server.name;
        nameTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100 + 1, textHeight, 197, 20, s);
        nameTextbox.isFocused = true;
        nameTextbox.setMaxStringLength(32);
        
        s = ipTextbox != null ? ipTextbox.getText() : server.ip;
        ipTextbox = new GuiTextField(this, fontRenderer, width / 2 - 100 + 1, textHeight + 40, 197, 20, s);
        ipTextbox.setMaxStringLength(128);
        
        s = proxyTextbox != null ? proxyTextbox.getText() : server.proxyName;
        if(s == null) s = "";
        proxyTextbox = new GuiTextField(this, fontRenderer, width / 2 - 38, textHeight + 90 - 1, 136, 20, s);
        proxyTextbox.setMaxStringLength(32);
        update();
    }
    
    private void update() {
    	if(server.pingType == ServerData.PING_VIA_JOHNY_MUFFIN) {
    		togglePingButton.displayString = "Ping type: Via JohnyMuffin Proxy";
    		if(java8orHigher) proxyTextbox.isEnabled = true;
    	}
    	else {
    		proxyTextbox.isEnabled = false;
    		if(server.pingType == ServerData.DONT_PING) {
        		togglePingButton.displayString = "Ping type: Don't Ping";
        	}
        	else if(server.pingType == ServerData.PING_DIRECT) {
        		togglePingButton.displayString = "Ping type: Direct";
        	}
    	}
    	
        if(ipTextbox.getText().length() > 0 && nameTextbox.getText().length() > 0)
        {
        	if(server.pingType != ServerData.PING_VIA_JOHNY_MUFFIN || (proxyTextbox.getText().length() > 0 && java8orHigher)) {
                String s = ipTextbox.getText().trim();
                doneButton.enabled = s.split(":").length <= 2;
                return;
        	}
        }
    	doneButton.enabled = false;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 1)
        {
        	mc.displayGuiScreen(guiServerList);
        } 
        else if(guibutton == doneButton)
        {
        	server.name = nameTextbox.getText();
            server.ip = ipTextbox.getText();
            if(server.pingType == ServerData.PING_VIA_JOHNY_MUFFIN)
            	server.proxyName = proxyTextbox.getText();
            if(newServer) {
                guiServerList.addServer(server);
            }
        	else{
        		guiServerList.editSelectedServer(server);
        	}
            mc.displayGuiScreen(guiServerList);
        }
        else if(guibutton == togglePingButton) {
        	server.pingType++;
        	if(server.pingType > ServerData.PING_VIA_JOHNY_MUFFIN) {
        		server.pingType = ServerData.DONT_PING;
        	}
        }
        update();
    }

    @Override
    protected void keyTyped(char c, int i)
    {
    	if(nameTextbox.isFocused)
        {
        	nameTextbox.textboxKeyTyped(c, i);
        }
    	else if(ipTextbox.isFocused) {
        	ipTextbox.textboxKeyTyped(c, i);
    	}
    	else {
        	proxyTextbox.textboxKeyTyped(c, i);
    	}
        if(c == '\r')
        {
            actionPerformed(doneButton);
        }
        update();
        super.keyTyped(c, i);
    }
    
    @Override
    public void selectNextField()
    {
    	if(nameTextbox.isFocused)
        {
        	nameTextbox.isFocused = false;
        	ipTextbox.isFocused = true;
        } 
    	else if(ipTextbox.isFocused)
        {
    		if(server.pingType == ServerData.PING_VIA_JOHNY_MUFFIN) {
    			proxyTextbox.isFocused = true;
    		}
    		else {
            	nameTextbox.isFocused = true;
    		}
        	ipTextbox.isFocused = false;
        }
    	else {
    		nameTextbox.isFocused = true;
    		proxyTextbox.isFocused = false;
    	}
    }

    @Override
    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        ipTextbox.mouseClicked(i, j, k);
        nameTextbox.mouseClicked(i, j, k);
        proxyTextbox.mouseClicked(i, j, k);
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Edit Server Info", width / 2, relativeHeight, 0xffffff);
        drawString(fontRenderer, "Server Name", width / 2 - 100, textHeight - 13, 0xa0a0a0);
        drawString(fontRenderer, "Server Address", width / 2 - 100, textHeight + 40 - 13, 0xa0a0a0);
        if(server.pingType == ServerData.PING_VIA_JOHNY_MUFFIN) {
        	if(java8orHigher) {
            	drawString(fontRenderer, "Proxy Name", width / 2 - 100, textHeight + 90 + 6, 0xa0a0a0);
        	}
        	else {
        		drawCenteredString(fontRenderer, "\u00a74ERROR: Java 8 required for proxy", width / 2, textHeight + 90 + 6, 0xa0a0a0);
        	}
        }
        else if(server.pingType == ServerData.PING_DIRECT) {
        	drawString(fontRenderer, "WARNING: Pinging server may cause End Of Stream with DDOS protection", width / 2 - 175, textHeight + 90 + 6, 0xa0a0a0);
        }
        nameTextbox.drawTextBox();
        ipTextbox.drawTextBox();
        if(proxyTextbox.isEnabled)
        	proxyTextbox.drawTextBox();
        super.drawScreen(i, j, f);
    }
}
