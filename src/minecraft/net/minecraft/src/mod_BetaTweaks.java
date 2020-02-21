package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import betatweaks.*;
import betatweaks.block.*;
import betatweaks.config.Config;
import betatweaks.gui.*;
import betatweaks.GuiAPIHandler;
import hmi.GuiRecipeViewer;


public class mod_BetaTweaks extends BaseMod {
	
	public String Version() {
		return "v1.2.2";
	}
	
	//Info for mine_diver's mod menu
	public String Description() {
		return "Beta but better";
	}
	
	public String Name() {
		return "Beta Tweaks";
	}
	
	public String Icon() {
		return resources + "/modMenu1";
	}
	
	public static GuiScreen firstGuiScreenAfterHijack;

	private boolean resetTallGrass = true;
	private boolean resetDeadBush = true;
	private boolean resetAchievements = true;
	
	private World currentWorld = null;

	private int lastTickHoeDamage;
	private int lastTickHoeX;
	private int lastTickHoeY;
	private int lastTickHoeZ;

	private final int guiOptionsButtonCount;

	private KeyBinding playerList = new KeyBinding("List Players", Keyboard.KEY_TAB);
	private KeyBinding customFullscreen = new KeyBinding("Custom Fullscreen", Keyboard.KEY_F8);
	public static KeyBinding zoom = new KeyBinding("Zoom", Keyboard.KEY_LCONTROL);

	private HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>> guiOverrides = new HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>>();
	public static boolean dontOverride = false;
	
	private betatweaks.config.Config cfg = new betatweaks.config.Config();
	
	public mod_BetaTweaks() {
		
		if(Utils.checkModInstalled("modloadermp", "ModLoaderMp")) {
			ModLoader.RegisterKey(this, playerList, false);
			//Load MP handler for BetaTweaks
			Utils.loadMod("BetaTweaksMP");
		}
		
		if(Utils.checkModInstalled("guiapi", "ModSettings")) {
			GuiAPIHandler.instance.init();
		}
		
		Utils.checkModInstalled("optifine", "GuiDetailSettingsOF");
		Utils.checkModInstalled("forge", "forge.ForgeHooksClient");
		Utils.checkModInstalled("aethersheep", "EntitySheepuff");
		Utils.checkModInstalled("shaders", "Shader");
		
		if (Utils.modInstalled("guiapi")) GuiAPIHandler.instance.loadSettings();

		initSettings(Utils.mc);

		ModLoader.SetInGameHook(this, true, false);
		ModLoader.SetInGUIHook(this, true, false);

		if ((cfg.logoStyle.getValue() != 0 || cfg.mainmenuPanorama.isEnabled())
				&& Utils.mc.currentScreen == null)
			Utils.mc.currentScreen = new GuiInitialHijack();
		
		if(!cfg.disableEntityRendererOverride.isEnabled()) {
			if(!Utils.modInstalled("optifine")) ModLoader.RegisterKey(this, zoom, false);
			Utils.mc.entityRenderer = new EntityRendererProxyFOV();
		}

		Object obj;
		if((obj = Utils.getStaticFieldValue(GuiOptions.class, "field_22135_k", "l")) instanceof Integer) {
			guiOptionsButtonCount = (Integer)obj;
		}
		else {
			guiOptionsButtonCount = 5;
		}
		
		if(cfg.showAllResolutionsInConsole.isEnabled()) {
			DisplayMode[] modes;
			try {
				modes = Display.getAvailableDisplayModes();
				for (int i=0;i<modes.length;i++) {
	                DisplayMode current = modes[i];
	                System.out.println(current.getWidth() + "," + current.getHeight() + "," +
	                                    current.getBitsPerPixel() + "," + current.getFrequency());
	            }
			} 
			catch (LWJGLException e) { e.printStackTrace(); }
		}
		if(!cfg.customFullscreenRes.getValue().isEmpty()) {
			Utils.setCustomRes(cfg.customFullscreenRes.getValue());
			ModLoader.RegisterKey(this, customFullscreen, false);
		}
		
	}
	
	public void KeyboardEvent(KeyBinding keybinding)
    {
		if(keybinding == customFullscreen) {
			Utils.toggleFullscreen();
		}
    }

	public void initSettings(Minecraft minecraft) {
		overrideIngameChat = true;
		GuiMainMenuCustom.resetLogo = true;
		minecraft.hideQuitButton = !cfg.mainmenuQuitButton.isEnabled();

		if (cfg.lightTNTwithFist.isEnabled() && !(Block.tnt instanceof BlockTNTPunchable)) new BlockTNTPunchable();
		if (cfg.indevStorageBlocks.isEnabled() && !(Block.blockSteel instanceof BlockOreStorageIndev)) initStorageBlocks();


		if (resetTallGrass) {
			resetTallGrass = false;
			Utils.clearBlockID(Block.tallGrass);
			if (cfg.hideLongGrass.isEnabled()) {
				Utils.replaceBlock(new BlockTallGrassHidden(), "tallGrass", "Y");
			} else {
				Utils.replaceBlock(new BlockTallGrass(31, 39).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("tallgrass"), "tallGrass", "Y");
			}
		}

		if (resetDeadBush) {
			resetDeadBush = false;
			Utils.clearBlockID(Block.deadBush);
			if (cfg.hideDeadBush.isEnabled()) {
				Utils.replaceBlock(new BlockDeadBushHidden(), "deadBush", "Z");
			} else {
				Utils.replaceBlock(new BlockDeadBush(32, 55).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("deadbush"), "deadBush", "Z");
			}
		}

		if (resetAchievements) {
			resetAchievements = false;
			if (cfg.hideAchievementNotifications.isEnabled()) {
				minecraft.guiAchievement = new GuiAchievementNull(minecraft);
			} else {
				minecraft.guiAchievement = new GuiAchievement(minecraft);
			}
		}
		
		guiOverrides.clear();
		if(cfg.improvedChat.isEnabled()) {
			guiOverrides.put(GuiChat.class, GuiImprovedChat.class);
		}
		if(cfg.scrollableControls.isEnabled()) {
			guiOverrides.put(GuiControls.class, GuiControlsScrollable.class);
		}
		if(cfg.serverList.isEnabled()) {
			guiOverrides.put(GuiMultiplayer.class, GuiServerList.class);
		}
		if (cfg.logoStyle.getValue() != 0 || cfg.mainmenuPanorama.isEnabled()) {
			guiOverrides.put(GuiMainMenu.class, GuiMainMenuCustom.class);
		}
		else {
			guiOverrides.put(GuiMainMenuCustom.class, GuiMainMenu.class);
		}
	}
	private int buttonCount = -1;
	private int buttonCount2 = -1;
	private GuiButton texturePackButton;
	private TexturePackBase initialTexturePack;
	private boolean overrideIngameChat = false;
	
	
	public void ModsLoaded() {
		Utils.checkModInstalled("hmi", ModLoader.isModLoaded("mod_HowManyItems"));
		Utils.checkModInstalled("minecolony", ModLoader.isModLoaded("mod_MineColony"));
	}
	
	@SuppressWarnings("unchecked")
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		if(overrideIngameChat || guiscreen instanceof GuiConnecting) {
			if(cfg.improvedChat.isEnabled()) {
				if(!(mc.ingameGUI instanceof GuiIngameImprovedChat)) {
					mc.ingameGUI = new GuiIngameImprovedChat(mc);
				}
			}
			else {
				if(mc.ingameGUI instanceof GuiIngameImprovedChat) {
					mc.ingameGUI = new GuiIngame(mc);
				}
			}
			overrideIngameChat = false;
		}
		
		if(guiOverrides.containsKey(guiscreen.getClass()) && !dontOverride) {
			Utils.overrideCurrentScreen(guiOverrides.get(guiscreen.getClass()));
		}
		if (firstGuiScreenAfterHijack != null) {
			Utils.overrideCurrentScreen(firstGuiScreenAfterHijack);
			firstGuiScreenAfterHijack = null;
		} else if (Utils.modInstalled("guiapi") && guiscreen instanceof GuiModScreen) {
			GuiAPIHandler.instance.handleTooltip((GuiModScreen)guiscreen, Utils.cursorX(), Utils.cursorY());
			if (Utils.getParentScreen() != guiscreen && guiscreen instanceof GuiModSelect && Utils.getParentScreen() instanceof GuiModScreen) {
				
				Boolean temp1 = cfg.indevStorageBlocks.isEnabled();
				Boolean temp2 = cfg.hideLongGrass.isEnabled();
				Boolean temp3 = cfg.hideDeadBush.isEnabled();
				Boolean temp4 = cfg.hideAchievementNotifications.isEnabled();
				GuiAPIHandler.instance.updateSettings();
				if (temp2 != cfg.hideLongGrass.isEnabled()) {
					resetTallGrass = true;
				}
				if (temp3 != cfg.hideDeadBush.isEnabled()) {
					resetDeadBush = true;
				}
				if (temp4 != cfg.hideAchievementNotifications.isEnabled()) {
					resetAchievements = true;
				}
				initSettings(mc);
				if (temp1 != cfg.indevStorageBlocks.isEnabled() || temp2 != cfg.hideLongGrass.isEnabled()
						|| temp3 != cfg.hideDeadBush.isEnabled()) {
					if (mc.theWorld != null)
						mc.renderGlobal.loadRenderers();
				}
			}
		} else if (cfg.ingameTexurePackButton.isEnabled() && guiscreen instanceof GuiIngameMenu) {
			if(buttonCount == -1 || guiscreen.controlList.size() == buttonCount) {
				buttonCount = guiscreen.controlList.size();
				texturePackButton = new GuiButton(137, guiscreen.width / 2 - 100, guiscreen.height / 4 + 72 + (byte)-16, "Mods and Texture Packs");
				texturePackButton.drawButton(mc, Utils.cursorX(), Utils.cursorY());
				guiscreen.controlList.add(texturePackButton);
				
			}
			if(Utils.buttonClicked(texturePackButton)) {
				mc.displayGuiScreen(new GuiTexturePacks(guiscreen));
			}
		} else if (guiscreen instanceof GuiOptions && !cfg.disableEntityRendererOverride.isEnabled()) {
			if(cfg.fovSlider.isEnabled() && (buttonCount2 == -1 || guiscreen.controlList.size() == buttonCount2)) {
				buttonCount2 = guiscreen.controlList.size();
				guiscreen.controlList.add(new GuiSliderBT(guiscreen.width / 2 - 155 + guiOptionsButtonCount % 2 * 160, guiscreen.height / 6 + 24 * (guiOptionsButtonCount >> 1), cfg.fov));
				((GuiButton)guiscreen.controlList.get(buttonCount2)).drawButton(mc, Utils.cursorX(), Utils.cursorY());
			}
			
		} else if (guiscreen instanceof GuiTexturePacks) {
			if(initialTexturePack == null) {
				initialTexturePack = Utils.mc.texturePackList.selectedTexturePack;
			}
			
		}
		Utils.updateParentScreen();
		if (mc.theWorld != currentWorld) {
			if (Utils.modInstalled("modloadermp") && mc.theWorld == null) {
				BetaTweaksMP.serverModInstalled = false;
			}
			if (Utils.modInstalled("guiapi")
					&& (currentWorld == null || !currentWorld.multiplayerWorld) != (mc.theWorld == null
							|| !mc.theWorld.multiplayerWorld)) {
				GuiAPIHandler.instance.loadSettings();
			}
			currentWorld = mc.theWorld;
		}
		
		if (cfg.draggingShortcuts.isEnabled() && guiscreen instanceof GuiContainer && (!Utils.modInstalled("hmi") || !(guiscreen instanceof GuiRecipeViewer))) {
			GuiContainer container = (GuiContainer)guiscreen;
			
			if(Utils.modInstalled("minecolony")) {
				if(container instanceof GuiHut && ((GuiHut)container).page != 0) {
					return true;
				}
				else if(container instanceof GuiCitizen && ((GuiCitizen)container).page != 0) {
					return true;
				}
			}
			
			int x = Utils.cursorX();
            int y = Utils.cursorY();
            EntityPlayerSP player = mc.thePlayer;
        	PlayerController controller = mc.playerController;
        	int windowId = container.inventorySlots.windowId;
        	Boolean shiftClick = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);

        	Slot slot = null;
			try {
				slot = (Slot)getSlot.invoke(container, new Object[]{x, y});
			} 
			catch (Exception e) { e.printStackTrace(); }
			
			if(slot != null) {
            while(Mouse.next()) {
            	
        		if(Mouse.getEventButtonState())
    	        {
        			if(Mouse.getEventButton() == 0 && slot.getHasStack() 
        					&& (player.inventory.getItemStack() == null || !slot.getStack().isItemEqual(player.inventory.getItemStack()))) {
        			
        				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
        					int i2 = slot.getStack().getMaxStackSize() / slot.getStack().stackSize;
        					for(int i = 0; i < i2; i++)
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
        				}
        				else {
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
            				if(!shiftClick) {
            					collecting = true;
            					itemClickedOn = true;
            				}
        				}
        			}
        			else if(Mouse.getEventButton() == 0 && !itemClickedOn && !slot.getHasStack()
        					&& player.inventory.getItemStack() != null) {
        				spreading = true;
        			}
        			else if(Mouse.getEventButton() == 0
        					&& player.inventory.getItemStack() != null && slot.getHasStack()
        					&& slot.getStack().isItemEqual(player.inventory.getItemStack())) {
        				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
        					int i2 = slot.getStack().getMaxStackSize() / slot.getStack().stackSize;
        					for(int i = 0; i < i2; i++)
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
        				}
        				else {
        					collecting = true;
            				lastSlotNo = slot.slotNumber;
            				
            				controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        				}
        			}
        			else if(Mouse.getEventButton() == 1 && player.inventory.getItemStack() == null && slot.getHasStack()) {
        				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
        					controller.handleMouseClick(windowId, slot.slotNumber, 1, shiftClick, player);
        				}
        				else if(shiftClick && slot.getHasStack()) {
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
        					controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        				}
        				else {
        					itemClickedOn = true;
        					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
        					lastSlotNo = slot.slotNumber;
        				}
    	            }
        			else if(Mouse.getEventButton() == 1/* && slot.getHasStack()*/) {
        				boolean itemHeld = player.inventory.getItemStack() != null;
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    					ItemStack item = player.inventory.getItemStack();
    					if(!itemHeld && player.inventory.getItemStack() != null) {
    						itemClickedOn = true;
    						if(!item.isItemEqual(player.inventory.getItemStack())) {
    							itemClickedOn = false;
    						}
    					}
    					if(item != null) {
    						lastSlotNo = slot.slotNumber;
    					}
    				}
    			} 
    			else
    	        {
    				if(Mouse.getEventButton() == 0 && !spreading && !collecting && (!slot.getHasStack() 
    						|| (player.inventory.getItemStack() != null && slot.getStack().isItemEqual(player.inventory.getItemStack())))) {
    					if (itemClickedOn) itemClickedOn = false;
    					else controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    					collecting = false;
    	            }
    				else if(Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1) {
    					itemClickedOn = false;
        				spreading = false;
        				collecting = false;
        				spreadSlots.clear();
        				spreadCount = -1;
        			}
    				else if(Mouse.getEventButton() == 1 && (!slot.getHasStack()
    						|| (player.inventory.getItemStack() != null && slot.getStack().isItemEqual(player.inventory.getItemStack())))) {
    					if (itemClickedOn) itemClickedOn = false;
    					//else controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    				}
    	        }
    			if(Mouse.isButtonDown(1)) {
    				if (!itemClickedOn)
					{
						if(slot.slotNumber != lastSlotNo /*&& !itemClickedOn*/
    							&& player.inventory.getItemStack() != null && (!slot.getHasStack() 
    							|| (slot.getStack().isItemEqual(player.inventory.getItemStack()) 
    									&& slot.getStack().getMaxStackSize() > slot.getStack().stackSize))) {
    						controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    						lastSlotNo = slot.slotNumber;
						}
					}
    					
    			}
    			else if(Mouse.isButtonDown(0)) {
    				if (spreading) {
    					if(!slot.getHasStack() && slot.isItemValid(player.inventory.getItemStack())) {
    						if(spreadCount == -1) {
    							spreadCount = player.inventory.getItemStack().stackSize;
    						}
    						
    						if(!spreadSlots.contains(slot.slotNumber) && spreadSlots.size() < spreadCount) {
    							
    							spreadSlots.add(slot.slotNumber);
    							for(int slotNo : spreadSlots) {
    								
    								for(Object obj : container.inventorySlots.slots) {
    									Slot currentSlot = (Slot)obj;
            							if(currentSlot.slotNumber == slotNo) {
            								controller.handleMouseClick(windowId, currentSlot.slotNumber, 0, false, player);
            								if(currentSlot.getHasStack()) {
            									controller.handleMouseClick(windowId, currentSlot.slotNumber, 0, false, player);
            								}
            								for(int i = 0; i < spreadCount / spreadSlots.size(); i++) {
            									controller.handleMouseClick(windowId, currentSlot.slotNumber, 1, false, player);
                							}
            							}
            						}
        						}
    							
    						}
    					}
    					
    					
    				}
    				else if (collecting){
    					if(lastSlotNo != slot.slotNumber) {
    					if(player.inventory.getItemStack() != null && slot.getHasStack() 
        						&& slot.getStack().isItemEqual(player.inventory.getItemStack()) && !(slot instanceof SlotCrafting)) {
    						
    						if(!shiftClick && player.inventory.getItemStack().getMaxStackSize() > player.inventory.getItemStack().stackSize) {
    							controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    						}
    					}
    					else if(player.inventory.getItemStack() == null && slot.slotNumber != lastSlotNo && lastSlotNo != -1 && !(slot instanceof SlotCrafting)) {
    						controller.handleMouseClick(windowId, lastSlotNo, 0, false, player);
    						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    					}
    					
    					}
    					if(shiftClick && player.inventory.getItemStack() != null && slot.getHasStack() 
        						&& slot.getStack().isItemEqual(player.inventory.getItemStack()) && !(slot instanceof SlotCrafting)) {
							controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
						}
    					lastSlotNo = slot.slotNumber;
    				}
    				else if(slot.getHasStack() && shiftClick && (player.inventory.getItemStack() == null
    						|| slot.getStack().isItemEqual(player.inventory.getItemStack())) && !(slot instanceof SlotCrafting)) {
						controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
    				}
    			}
        	}
            if(counter > -1) counter--;
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindDrop.keyCode) && !droppedItem && (counter == -1 || shiftClick) && player.inventory.getItemStack() == null  && slot.getHasStack()) {
            	counter = 20;
            	controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
            	controller.handleMouseClick(windowId, -999, shiftClick ? 0 : 1, false, player);
            	controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
            }
            	
            if(Keyboard.getEventKeyState())
            {
            	if(player.inventory.getItemStack() != null && Keyboard.getEventKey() == mc.gameSettings.keyBindDrop.keyCode && !droppedItem)
                {
            		droppedItem = true;
            		controller.handleMouseClick(windowId, -999, shiftClick ? 0 : 1, false, player);
                }
            }
            else {
            	if(Keyboard.getEventKey() == mc.gameSettings.keyBindDrop.keyCode)
                {
            		droppedItem = false;
                }
            }
            
            
		}
		else {
			lastSlotNo = -1;
			if(Mouse.isButtonDown(1)) {
				
			}
			if(!Mouse.isButtonDown(0)) {
				itemClickedOn = false;
				spreading = false;
				collecting = false;
				spreadSlots.clear();
				spreadCount = -1;
			}
		}
			if(spreadSlots.size() > 1) {
				GL11.glPushMatrix();
				Graphics.preRender();
				GL11.glTranslatef((container.width - container.xSize) / 2, (container.height - container.ySize) / 2, 0.0F);
				for(int slotNo : spreadSlots) {
					
					for(Object obj : container.inventorySlots.slots) {
						Slot currentSlot = (Slot)obj;
						if(currentSlot.slotNumber == slotNo && currentSlot != slot) {
							Graphics.drawSlotBackground(currentSlot.xDisplayPosition, currentSlot.yDisplayPosition, 0x80ffffff);
					        Graphics.drawItemStack(currentSlot.xDisplayPosition, currentSlot.yDisplayPosition, currentSlot.getStack());
						}
					}
				}
				InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
		        if(inventoryplayer.getItemStack() != null)
		        {
		        	int k = (container.width - container.xSize) / 2;
		            int l = (container.height - container.ySize) / 2;
		            GL11.glTranslatef(0.0F, 0.0F, 32F);
		            Graphics.drawItemStack(x - k - 8, y - l - 8, inventoryplayer.getItemStack());
		            GL11.glTranslatef(0.0F, 0.0F, -32F);
		        }
        		Graphics.postRender();
        		GL11.glPopMatrix();
			}
		}
		return true;
	}
	private int counter = -1;
	private Boolean droppedItem = false;
	private int spreadCount = -1;
	private Boolean spreading = false;
	private Boolean collecting = false;
	private Boolean itemClickedOn = false;
	private int lastSlotNo;
	private ArrayList<Integer> spreadSlots = new ArrayList<Integer>();
	private boolean rmbHeld;

    private final Method getSlot = Utils.getMethod(GuiContainer.class,  new Class<?>[] {int.class, int.class}, "getSlotAtPosition", "a");
    //private int debug;
	public boolean OnTickInGame(Minecraft minecraft) {
		
		//SCROLL TEST
		/*
		if(Keyboard.isKeyDown(Keyboard.KEY_K)) {
			minecraft.ingameGUI.addChatMessage("message" + debug++);
		}
		*/
		
		//Clear button override 'memory'
		if((buttonCount != -1 || buttonCount2 != -1) && !(minecraft.currentScreen instanceof GuiIngameMenu || minecraft.currentScreen instanceof GuiOptions)) {
			buttonCount = buttonCount2 = -1;
		}
		
		//Reload world if texture pack changed in game
		if(initialTexturePack != null && !(minecraft.currentScreen instanceof GuiTexturePacks)) {
			if(initialTexturePack != Utils.mc.texturePackList.selectedTexturePack && minecraft.theWorld != null) {
				minecraft.renderGlobal.loadRenderers();
			}	
			initialTexturePack = null;
		}
		
		//Equip armour from hotbar
		if(cfg.draggingShortcuts.isEnabled()) {
			if(Mouse.isButtonDown(1) && minecraft.currentScreen == null) {
				if(!rmbHeld) {
					rmbHeld = true;
					if(minecraft.thePlayer.inventory.getCurrentItem() != null && minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemArmor) {
						int armourSlotID = ((ItemArmor)minecraft.thePlayer.inventory.getCurrentItem().getItem()).armorType + 5;
						int heldSlotID = minecraft.thePlayer.inventorySlots.slots.size() - (9 - minecraft.thePlayer.inventory.currentItem);
						GuiInventory inv = new GuiInventory(minecraft.thePlayer);
						minecraft.playerController.handleMouseClick(inv.inventorySlots.windowId, heldSlotID, 0, false, minecraft.thePlayer);
						minecraft.playerController.handleMouseClick(inv.inventorySlots.windowId, armourSlotID, 0, false, minecraft.thePlayer);
						minecraft.playerController.handleMouseClick(inv.inventorySlots.windowId, heldSlotID, 0, false, minecraft.thePlayer);
					}
				}
				
			}
			else {
				rmbHeld = false;
			}
			
		}
		
		//Draw playerlist
		if (Utils.modInstalled("modloadermp") && BetaTweaksMP.serverModInstalled && BetaTweaksMP.playerListAllowed.isEnabled()
				&& Keyboard.isKeyDown(playerList.keyCode) && minecraft.currentScreen == null) {
			ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth,
					minecraft.displayHeight);
			int kx = scaledresolution.getScaledWidth();
			int j3 = BetaTweaksMP.maxPlayers;
			int i4 = j3;
			int k4 = 1;
			for (; i4 > 20; i4 = ((j3 + k4) - 1) / k4) {
				k4++;
			}

			int k5 = 300 / k4;
			if (k5 > 150) {
				k5 = 150;
			}
			int j6 = (kx - k4 * k5) / 2;
			byte byte2 = 10;
			Utils.gui.drawRect(j6 - 1, byte2 - 1, j6 + k5 * k4, byte2 + 9 * i4, 0x80000000);
			for (int k7 = 0; k7 < j3; k7++) {
				int i8 = j6 + (k7 % k4) * k5;
				int l8 = byte2 + (k7 / k4) * 9;
				Utils.gui.drawRect(i8, l8, (i8 + k5) - 1, l8 + 8, 0x20ffffff);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3008 /* GL_ALPHA_TEST */);
				if (k7 >= BetaTweaksMP.playerList.size()) {
					continue;
				}
				minecraft.fontRenderer.drawStringWithShadow(BetaTweaksMP.playerList.get(k7), i8, l8, 0xffffff);
			}
		}

		//laddergaps
		if ((cfg.ladderGaps.isEnabled() && !minecraft.theWorld.multiplayerWorld) || (Utils.modInstalled("modloadermp")
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.ladderGaps.isEnabled())) {

			for (int z = 0; z < minecraft.theWorld.loadedEntityList.size()
					+ minecraft.theWorld.playerEntities.size(); z++) {
				Entity entity;
				if (z < minecraft.theWorld.loadedEntityList.size()) {
					entity = (Entity) minecraft.theWorld.loadedEntityList.get(z);
				} else {
					entity = (Entity) minecraft.theWorld.playerEntities
							.get(z - minecraft.theWorld.loadedEntityList.size());
				}
				if (entity instanceof EntityLiving) {
					int i = MathHelper.floor_double(entity.posX);
					int j = MathHelper.floor_double(entity.boundingBox.minY);
					int k = MathHelper.floor_double(entity.posZ);
					int l = minecraft.theWorld.getBlockId(i, j + 1, k);
					if (!((EntityLiving) entity).isOnLadder() && l == Block.ladder.blockID) {
						float f4 = 0.15F;
						if (entity.motionX < (double) (-f4)) {
							entity.motionX = (double) -f4;
						}
						if (entity.motionX > (double) f4) {
							entity.motionX = (double) f4;
						}
						if (entity.motionX < (double) (-f4)) {
							entity.motionX = (double) -f4;
						}
						if (entity.motionX > (double) f4) {
							entity.motionX = (double) f4;
						}
						entity.fallDistance = 0.0F;
						if (entity.motionY < -0.14999999999999999D) {
							entity.motionY = -0.14999999999999999D;
						}
						if (entity.isSneaking() && entity.motionY < 0.0D) {
							entity.motionY = 0.0D;
						}
						if (!((EntityLiving) entity).isOnLadder() && entity.isCollidedHorizontally) {
							entity.motionY = 0.20000000000000001D;
							entity.motionY -= 0.080000000000000002D;
							entity.motionY *= 0.98000001907348633D;
						}
					}
				}

			}
		}
		
		//punch sheep for wool
		if (((cfg.punchSheepForWool.isEnabled() && !minecraft.theWorld.multiplayerWorld) || (Utils.modInstalled("modloadermp")
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.punchSheepForWool.isEnabled()))
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY && Mouse.isButtonDown(0)) {

			Entity e = minecraft.objectMouseOver.entityHit;
			if (e instanceof EntitySheep) {
				if (minecraft.theWorld.multiplayerWorld && Utils.modInstalled("modloadermp")
						&& BetaTweaksMP.punchSheepForWool.isEnabled()) {
					BetaTweaksMP.sheepPunched(e.entityId);
				} else if (e.beenAttacked) {
					if (!((EntitySheep) e).getSheared()) {

						((EntitySheep) e).setSheared(true);
						Random rand = new Random();
						int i = 2 + rand.nextInt(3);
						for (int j = 0; j < i; j++) {
							EntityItem wool = e.entityDropItem(
									new ItemStack(Block.cloth.blockID, 1, ((EntitySheep) e).getFleeceColor()), 1.0F);
							wool.motionY += rand.nextFloat() * 0.05F;
							wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}
					}
				}
			}
			else if(Utils.modInstalled("aethersheep") && e instanceof EntitySheepuff) {
				if (!minecraft.theWorld.multiplayerWorld && e.beenAttacked) {
					if (!((EntitySheepuff) e).getSheared()) {

						((EntitySheepuff) e).setSheared(true);
						Random rand = new Random();
						int i = 2 + rand.nextInt(3);
						for (int j = 0; j < i; j++) {
							EntityItem wool = e.entityDropItem(
									new ItemStack(Block.cloth.blockID, 1, ((EntitySheepuff) e).getFleeceColor()), 1.0F);
							wool.motionY += rand.nextFloat() * 0.05F;
							wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}
					}
				}
			}
		}

		//minecraft boosters
		if (cfg.minecartBoosters.isEnabled() && !minecraft.theWorld.multiplayerWorld) {
			for (int i = 0; i < minecraft.theWorld.loadedEntityList.size(); i++) {
				Entity entity = (Entity) minecraft.theWorld.loadedEntityList.get(i);
				if (entity instanceof EntityMinecart) {
					List list = minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(entity,
							entity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
					if (list != null && list.size() > 0) {
						for (int j1 = 0; j1 < list.size(); j1++) {
							Entity entity2 = (Entity) list.get(j1);
							if (entity2 != entity2.riddenByEntity && entity2.canBePushed()
									&& (entity2 instanceof EntityMinecart)) {
								double d = entity2.posX - entity.posX;
								double d1 = entity2.posZ - entity.posZ;
								double d2 = d * d + d1 * d1;
								if (d2 >= 9.9999997473787516E-005D) {
									d2 = MathHelper.sqrt_double(d2);
									d /= d2;
									d1 /= d2;
									double d3 = 1.0D / d2;
									if (d3 > 1.0D) {
										d3 = 1.0D;
									}
									d *= d3;
									d1 *= d3;
									d *= 0.10000000149011612D;
									d1 *= 0.10000000149011612D;
									d *= 1.0F - entity.entityCollisionReduction;
									d1 *= 1.0F - entity.entityCollisionReduction;
									d *= 0.5D;
									d1 *= 0.5D;
									entity.motionX *= 0.20000000298023224D;
									entity.motionZ *= 0.20000000298023224D;
									entity.addVelocity(entity2.motionX - d, 0.0D, entity2.motionZ - d1);
									entity2.motionX *= 0.69999998807907104D;
									entity2.motionZ *= 0.69999998807907104D;
								}
							}
						}
					}
				}
			}
		}

		//boat elevators
		if (cfg.boatElevators.isEnabled() && !minecraft.theWorld.multiplayerWorld) {
			for (int i = 0; i < minecraft.theWorld.loadedEntityList.size(); i++) {
				Entity entity = (Entity) minecraft.theWorld.loadedEntityList.get(i);
				if (entity instanceof EntityBoat) {

					int q = 5;
					double d = 0.0D;
					for (int j = 0; j < q; j++) {
						double d5 = (entity.boundingBox.minY
								+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 0)) / (double) i)
								- 0.125D;
						double d9 = (entity.boundingBox.minY
								+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 1)) / (double) i)
								- 0.125D;
						AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(entity.boundingBox.minX, d5,
								entity.boundingBox.minZ, entity.boundingBox.maxX, d9, entity.boundingBox.maxZ);
						if (minecraft.theWorld.isAABBInMaterial(axisalignedbb, Material.water)) {
							d += 1.0D / (double) q;
						}
					}

					if (d >= 1.0D) {
						double d3 = d * 2D - 1.0D;
						entity.moveEntity(0, (0.039999999105930328D * d3) / 2.5D - 0.0070000002160668373D, 0);
					}
				}
			}
		}

		//hoe dirt for seeds
		if (((cfg.hoeGrassForSeeds.isEnabled() && !minecraft.theWorld.multiplayerWorld) || (Utils.modInstalled("modloadermp")
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.hoeGrassForSeeds.isEnabled()))
				&& minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemHoe
				&& lastTickHoeDamage == minecraft.thePlayer.getCurrentEquippedItem().getItemDamage() - 1) {
			lastTickHoeDamage = -1;

			if (minecraft.theWorld.multiplayerWorld && Utils.modInstalled("modloadermp") && BetaTweaksMP.serverModInstalled
					&& BetaTweaksMP.hoeGrassForSeeds.isEnabled()) {

				BetaTweaksMP.grassHoed(lastTickHoeX, lastTickHoeY, lastTickHoeZ);
			} else {
				Random rand = new Random();
				int i;
				if ((i = Block.tallGrass.idDropped(0, rand)) != -1) {
					float f = 0.7F;
					float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
					float f2 = 1.2F;
					float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
					EntityItem seeds = new EntityItem(minecraft.theWorld, (double) lastTickHoeX + f1,
							(double) lastTickHoeY + f2, (double) lastTickHoeZ + f3, new ItemStack(i, 1, 0));
					seeds.delayBeforeCanPickup = 10;
					minecraft.theWorld.entityJoinedWorld(seeds);
				}
			}
			return true;
		}

		//handle block placing on invisible blocks
		if ((cfg.hideLongGrass.isEnabled() || cfg.hideDeadBush.isEnabled()
				|| ((cfg.hoeGrassForSeeds.isEnabled() && !minecraft.theWorld.multiplayerWorld) || (Utils.modInstalled("modloadermp")
						&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.hoeGrassForSeeds.isEnabled())))
				&& Mouse.isButtonDown(1) && minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {

			int x = minecraft.objectMouseOver.blockX;
			int y = minecraft.objectMouseOver.blockY;
			int z = minecraft.objectMouseOver.blockZ;

			if ((cfg.hoeGrassForSeeds.isEnabled() && !minecraft.theWorld.multiplayerWorld) || (Utils.modInstalled("modloadermp")
					&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.hoeGrassForSeeds.isEnabled())
					&& minecraft.thePlayer.getCurrentEquippedItem() != null
					&& minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemHoe) {
				int b = minecraft.theWorld.getBlockId(x, y, z);
				if (Block.blocksList[b] == Block.grass) {
					lastTickHoeDamage = minecraft.thePlayer.getCurrentEquippedItem().getItemDamage();
					lastTickHoeX = x;
					lastTickHoeY = y;
					lastTickHoeZ = z;
				}

			}

			if (minecraft.objectMouseOver.sideHit == 0) {
				y--;
			}
			if (minecraft.objectMouseOver.sideHit == 1) {
				y++;
			}
			if (minecraft.objectMouseOver.sideHit == 2) {
				z--;
			}
			if (minecraft.objectMouseOver.sideHit == 3) {
				z++;
			}
			if (minecraft.objectMouseOver.sideHit == 4) {
				x--;
			}
			if (minecraft.objectMouseOver.sideHit == 5) {
				x++;
			}
			int b = minecraft.theWorld.getBlockId(x, y, z);
			if (((Block.blocksList[b] == Block.tallGrass && cfg.hideLongGrass.isEnabled())
					|| (Block.blocksList[b] == Block.deadBush && cfg.hideDeadBush.isEnabled()))) {

				if (minecraft.theWorld.multiplayerWorld && Utils.modInstalled("modloadermp")
						&& BetaTweaksMP.serverModInstalled) {
					BetaTweaksMP.longgrassDestroyed(x, y, z);
				} else if (minecraft.theWorld.multiplayerWorld) {
					minecraft.playerController.clickBlock(x, y, z, 0);
				} else {
					minecraft.theWorld.setBlock(x, y, z, 0);
				}
				minecraft.thePlayer.getCurrentEquippedItem().useItemRightClick(minecraft.theWorld, minecraft.thePlayer);
			}

		}

		return true;
	}

	public static void initStorageBlocks() {
		new BlockOreStorageIndev(Block.blockSteel, new String[] {"blockSteel", "aj"}, resources + "/steelSide.png", resources + "/steelBottom.png");
		new BlockOreStorageIndev(Block.blockGold, new String[] {"blockGold", "ai"}, resources + "/goldSide.png", resources + "/goldBottom.png");
		new BlockOreStorageIndev(Block.blockDiamond, new String[] {"blockDiamond", "ay"}, resources + "/diamondSide.png", resources + "/diamondBottom.png");
		
		ModLoader.RegisterAllTextureOverrides(Utils.mc.renderEngine);
		
		final Field blocksEffectiveAgainst = Utils.getField(ItemTool.class, "blocksEffectiveAgainst", "bk");
		if (blocksEffectiveAgainst != null) {
			for(Item item : Item.itemsList) {
				if(item instanceof ItemPickaxe) {
					try {
						Block originalBlocks[] = (Block[])blocksEffectiveAgainst.get(item);

						Block newBlocks[] = Arrays.copyOf(originalBlocks, originalBlocks.length + 3);
						newBlocks[originalBlocks.length] = Block.blocksList[Block.blockSteel.blockID];
						newBlocks[originalBlocks.length + 1] = Block.blocksList[Block.blockGold.blockID];
						newBlocks[originalBlocks.length + 2] = Block.blocksList[Block.blockDiamond.blockID];
						blocksEffectiveAgainst.set(item, newBlocks);
					} 
					catch (IllegalAccessException e1) { e1.printStackTrace(); }
				}
			}
		}
		
	}

	
	public static String resources = "/betatweaks/resources";
	
	//These methods are used to avoid reflection in classes from the betatweaks package.
	public static void drawRect(int i, int j, int k, int l, int colour) {
		Utils.gui.drawRect(i, j, k, l, colour);
	}
	
	public static FloatBuffer fogColorBuffer(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBuffer;
	}
	
	public static float fogColorRed(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorRed;
	}
	
	public static float fogColorGreen(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorGreen;
	}
	
	public static float fogColorBlue(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBlue;
	}

}
