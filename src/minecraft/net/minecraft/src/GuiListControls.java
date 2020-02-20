package net.minecraft.src;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class GuiListControls extends GuiList {
	private final GameSettings options;
	private final GuiControlsScrollable guiControls;
	private final ArrayList<Object> controlList = new ArrayList<Object>();
	private int selectedButton = -1;

	public GuiListControls(Minecraft minecraft, GuiControlsScrollable guiControls, GameSettings gamesettings) {
		super(minecraft, guiControls.width + 80, guiControls.height, 32, guiControls.height - 51, 24);
		this.options = gamesettings;
		this.guiControls = guiControls;

		for(int id = 0; id < options.keyBindings.length; id++) {
		
            	 controlList.add(new GuiSmallButton(id, 0, 0, 70, 20, options.getOptionDisplayString(id)));
            
		}
	}

	@Override
	public int getSize() {
		return (int) (options.keyBindings.length + 2 - 1) / 2;
	}

	protected void actionPerformed(GuiButton guibutton) {
		for(int i = 0; i < options.keyBindings.length; i++){
			
			((GuiButton)controlList.get(i)).displayString = options.getOptionDisplayString(i);
		}

		selectedButton = guibutton.id;
		guibutton.displayString = (new StringBuilder()).append("> ").append(options.getOptionDisplayString(guibutton.id)).append(" <").toString();
        
	}


	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		//if(selectedButton >= 0) {
		//	options.setKeyBinding(selectedButton, -100 + button);
		//	((GuiButton)controlList.get(selectedButton)).displayString = options.getOptionDisplayString(selectedButton);
		//	selectedButton = -1;
		//} else 
			
		if (button == 0) {
			boolean found = false;
            for(int l = 0; l < controlList.size(); l++) {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.mousePressed(mc, mouseX, mouseY)) {
                    mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    actionPerformed(guibutton);
                    found = true;
                }
            }

			if (!found){
				super.mouseClicked(mouseX, mouseY, button);
			}
        }
	}

	@Override
	public void keyTyped(char key, int keyId) {
		if(selectedButton >= 0) {
            if (keyId == 1) {
                options.setKeyBinding(selectedButton, 0);
            }
            else {
            	options.setKeyBinding(selectedButton, keyId);
            }
           
            selectedButton = -1;
		}
		else {
			guiControls.keyTypedWithoutBind(key, keyId);
		}
	}

	@Override
	public void drawSlot(int id, int left, int top, int mouseX, int mouseY, Tessellator tessellator) {
		left = guiControls.width / 2 - 155;
		int start = (id * 2);
		for (int i = start; (i < start + 2) && (i < options.keyBindings.length); i++) {
			int offset = (i % 2) * 160;
			if (i < controlList.size()) {
				
			
				guiControls.drawString(guiControls.fontRenderer, options.getKeyBindingDescription(i), left + offset + 70 + 6, top + 7, -1);

				

				boolean duplicate = false;
				for(int j = 0; j < options.keyBindings.length & !duplicate; j++) {
					if(i != j && options.keyBindings[i].keyCode == options.keyBindings[j].keyCode){
						duplicate = true;
					}
				}

				GuiButton button = (GuiButton) controlList.get(i);
				if(selectedButton == i) {
					button.displayString = "\247f> \247e??? \247f<";
				} else if (duplicate && options.keyBindings[i].keyCode != 0) {
					 button.displayString = (new StringBuilder()).append("\247c").append(options.getOptionDisplayString(i)).toString();
				} else {
					
					 button.displayString = options.getOptionDisplayString(i);
					
			   }
				button.xPosition = left + offset;
				button.yPosition = top;
				button.drawButton(mc, mouseX, mouseY);
			}
		}
	}
}
