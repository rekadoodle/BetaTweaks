package net.minecraft.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class mod_BetaTweaks extends BaseMod {

	public String Version() {
		return "v1.1.2";
	}
	
	public String Description() {
		//For mine_diver's mod menu
		return "A collection of quality of life improvements";
	}
	
	public String Name() {
		//For mine_diver's mod menu
		return "Beta Tweaks";
	}

	enum LogoState {
		STANDARD, ANIMATED, CUSTOM
	};

	public static Boolean optionsClientDraggingShortcuts = true;
	public static LogoState optionsClientLogo = LogoState.STANDARD;
	public static Boolean optionsClientPanoramaEnabled = false;
	public static Boolean optionsClientQuitGameButton = true;
	public static Boolean optionsClientMultiplayerMenu = true;
	public static Boolean optionsClientScrollableControls = true;
	public static Boolean optionsClientIngameTexturePackButton = false;
	public static Boolean optionsClientDisableAchievementNotifications = false;
	public static Boolean optionsClientIndevStorageBlocks = false;
	public static Boolean optionsClientHideLongGrass = false;
	public static Boolean optionsClientHideDeadBush = false;

	public static Boolean optionsGameplayPunchableSheep = true;
	public static Boolean optionsGameplayLadderGaps = true;
	public static Boolean optionsGameplayLightTNTwithFist = true;
	public static Boolean optionsGameplayHoeDirtSeeds = false;
	public static Boolean optionsGameplayMinecartBoosters = true;
	public static Boolean optionsGameplayElevatorBoats = true;

	public static Boolean guiAPIinstalled;
	public static Boolean modloaderMPinstalled;
	public static GuiScreen parentScreen;
	public static GuiScreen firstGuiScreenAfterHijack;

	private static Boolean TNTinitialised = false;
	private static Boolean storageBlocksInitialised = false;
	private static Boolean customLogoInitialised = false;
	private static Boolean resetTallGrass = true;
	private static Boolean resetDeadBush = true;
	private static Boolean resetAchievements = true;
	
	private static World currentWorld = null;
	
	public static float logoScale = 0;
	public static float logoOffsetX = 0;
	public static float logoOffsetY = 0;
	public static float logoAxisTilt= 15;
	public static float logoLightMultiplier = 1;
	public static Boolean logoSplashTextEnabled = true;
	public static float logoSplashTextOffsetX = 0;
	public static float logoSplashTextOffsetY = 0;

	private static int lastTickHoeDamage;
	private static int lastTickHoeX;
	private static int lastTickHoeY;
	private static int lastTickHoeZ;

	private static File configFile = new File((Minecraft.getMinecraftDir()) + "/config/BetaTweaks.cfg");
	protected Random rand;

	private KeyBinding playerList = new KeyBinding("List Players", 15);

	mod_BetaTweaks() {
		try {
			Class.forName("ModLoaderMp");
			modloaderMPinstalled = true;
			ModLoader.RegisterKey(this, playerList, false);
			ClassLoader classloader = (net.minecraft.src.ModLoader.class).getClassLoader();
			Method addmodMethod;
			try {
				addmodMethod = ModLoader.class.getDeclaredMethod("addMod", ClassLoader.class, String.class);
				addmodMethod.setAccessible(true);
				try {
					addmodMethod.invoke(null, new Object[] {
							classloader, "BetaTweaksMP.class"
						});
				} 
				catch (IllegalAccessException e) { e.printStackTrace(); } 
				catch (IllegalArgumentException e) { e.printStackTrace(); } 
				catch (InvocationTargetException e) { e.printStackTrace(); }
			} 
			catch (NoSuchMethodException e1) { e1.printStackTrace(); }
			catch (SecurityException e1) { e1.printStackTrace(); }
		} 
		catch (ClassNotFoundException e) { modloaderMPinstalled = false; }
		try {
			Class.forName("ModSettings");
			guiAPIinstalled = true;
			BetaTweaksGuiAPI.instance.init();
		} 
		catch (ClassNotFoundException e) { guiAPIinstalled = false; }

		if (!configFile.exists()) writeConfig();
		readConfig();
		if (guiAPIinstalled) BetaTweaksGuiAPI.instance.loadSettings();

		initSettings(ModLoader.getMinecraftInstance());

		ModLoader.SetInGameHook(this, true, false);
		ModLoader.SetInGUIHook(this, true, false);

		if ((optionsClientLogo != LogoState.STANDARD || optionsClientPanoramaEnabled)
				&& ModLoader.getMinecraftInstance().currentScreen == null)
			ModLoader.getMinecraftInstance().currentScreen = new GuiInitialHijack();

	}

	public static void initSettings(Minecraft minecraft) {
		GuiMainMenuCustom.resetLogo = true;
		minecraft.hideQuitButton = !optionsClientQuitGameButton;

		if (optionsGameplayLightTNTwithFist && !TNTinitialised) {
			TNTinitialised = true;
			Block.blocksList[Block.tnt.blockID] = null;
			ModLoader.AddName(new BlockTNTPunchable(), StringTranslate.getInstance().translateNamedKey(Block.tnt.getBlockName()));
		}

		if (optionsClientIndevStorageBlocks && !storageBlocksInitialised) {
			storageBlocksInitialised = true;
			initStorageBlocks();
		}

		if (optionsClientLogo == LogoState.CUSTOM && !customLogoInitialised) {
			if (!GuiMainMenuCustom.configLogoFile.exists())
				GuiMainMenuCustom.writeCustomLogoConfig();
			GuiMainMenuCustom.readCustomLogoConfig();
		}

		if (resetTallGrass) {
			resetTallGrass = false;
			Block.blocksList[Block.tallGrass.blockID] = null;
			Field x = Block.class.getDeclaredFields()[47];
			x.setAccessible(true);
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
				if (optionsClientHideLongGrass) {
					x.set(null, new BlockTallGrassHidden(Block.tallGrass));
				} else {
					x.set(null, (BlockTallGrass) (new BlockTallGrass(31, 39)).setHardness(0.0F)
							.setStepSound(Block.soundGrassFootstep).setBlockName("tallgrass"));
				}
			} 
			catch (NoSuchFieldException e) { e.printStackTrace();} 
			catch (IllegalAccessException e) { e.printStackTrace();}
		}

		if (resetDeadBush) {
			resetDeadBush = false;
			Block.blocksList[Block.deadBush.blockID] = null;
			Field x = Block.class.getDeclaredFields()[48];
			x.setAccessible(true);
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
				if (optionsClientHideDeadBush) {
					x.set(null, new BlockDeadBushHidden(Block.deadBush));
				} else {
					x.set(null, (BlockDeadBush) (new BlockDeadBush(32, 55)).setHardness(0.0F)
							.setStepSound(Block.soundGrassFootstep).setBlockName("deadbush"));
				}
			} 
			catch (NoSuchFieldException e1) { e1.printStackTrace(); } 
			catch (IllegalAccessException e) { e.printStackTrace(); }
		}

		if (resetAchievements) {
			resetAchievements = false;
			if (optionsClientDisableAchievementNotifications) {
				minecraft.guiAchievement = new GuiAchievementNull(minecraft);
			} else {
				minecraft.guiAchievement = new GuiAchievement(minecraft);
			}
		}
	}
	private int texturePackButtonIndex = -1;
	private TexturePackBase initialTexturePack;
	
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		
		if (firstGuiScreenAfterHijack != null) {
			mc.displayGuiScreen(firstGuiScreenAfterHijack);
			firstGuiScreenAfterHijack = null;
		} else if (guiAPIinstalled && guiscreen instanceof GuiModScreen) {
			BetaTweaksGuiAPI.instance.handleTooltip((GuiModScreen)guiscreen);
			if (parentScreen != guiscreen && guiscreen instanceof GuiModSelect && parentScreen instanceof GuiModScreen) {
				Boolean temp1 = optionsClientIndevStorageBlocks;
				Boolean temp2 = optionsClientHideLongGrass;
				Boolean temp3 = optionsClientHideDeadBush;
				Boolean temp4 = optionsClientDisableAchievementNotifications;
				BetaTweaksGuiAPI.instance.updateSettings();
				if (temp2 != optionsClientHideLongGrass) {
					resetTallGrass = true;
				}
				if (temp3 != optionsClientHideDeadBush) {
					resetDeadBush = true;
				}
				if (temp4 != optionsClientDisableAchievementNotifications) {
					resetAchievements = true;
				}
				initSettings(mc);
				if (temp1 != optionsClientIndevStorageBlocks || temp2 != optionsClientHideLongGrass
						|| temp3 != optionsClientHideDeadBush) {
					if (mc.theWorld != null)
						mc.renderGlobal.loadRenderers();
				}
			}
			if(parentScreen != guiscreen) {
				parentScreen = guiscreen;
			}
		} else if (guiscreen instanceof GuiMainMenu && !(guiscreen instanceof GuiMainMenuCustom)
				&& (optionsClientLogo != LogoState.STANDARD || optionsClientPanoramaEnabled)) {
			mc.displayGuiScreen(new GuiMainMenuCustom());
		} else if (guiscreen instanceof GuiMainMenuCustom && !(guiscreen instanceof GuiMainMenu)
				&& (optionsClientLogo == LogoState.STANDARD && !optionsClientPanoramaEnabled)) {
			mc.displayGuiScreen(new GuiMainMenu());
		} else if (optionsClientMultiplayerMenu && guiscreen instanceof GuiMultiplayer && !(parentScreen instanceof GuiMultiplayerMenu || parentScreen instanceof GuiMultiplayer)) {
			mc.displayGuiScreen(new GuiMultiplayerMenu(parentScreen));
		} else if (optionsClientScrollableControls && guiscreen instanceof GuiControls) {
			mc.displayGuiScreen(new GuiControlsScrollable(parentScreen, mc.gameSettings));
		} else if (guiscreen instanceof GuiIngameMenu) {
			if(optionsClientIngameTexturePackButton && (texturePackButtonIndex == -1 || guiscreen.controlList.size() == texturePackButtonIndex)) {
				texturePackButtonIndex = guiscreen.controlList.size();
				guiscreen.controlList.add(new GuiButton(137, guiscreen.width / 2 - 100, guiscreen.height / 4 + 72 + (byte)-16, "Mods and Texture Packs"));
			}
			if(optionsClientIngameTexturePackButton) {
				try {
					Field x = GuiScreen.class.getDeclaredFields()[7];
					x.setAccessible(true);
					GuiButton currentButton = (GuiButton)x.get(guiscreen);
					if(currentButton != null) {
						if(currentButton.id == 137) {
							mc.displayGuiScreen(new GuiTexturePacks(guiscreen));
							x.set(guiscreen, null);
						}
					}
				}  
				catch (IllegalArgumentException e) { e.printStackTrace(); } 
				catch (IllegalAccessException e) { e.printStackTrace(); } 
			}
		} else if (guiscreen instanceof GuiTexturePacks) {
			if(initialTexturePack == null) {
				initialTexturePack = ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack;
			}
			
		} else if (guiscreen != parentScreen) {
			parentScreen = guiscreen;
		}
		if (mc.theWorld != currentWorld) {
			if (guiAPIinstalled
					&& (currentWorld == null || !currentWorld.multiplayerWorld) != (mc.theWorld == null
							|| !mc.theWorld.multiplayerWorld)) {
				BetaTweaksGuiAPI.instance.loadSettings();
			}
			if (modloaderMPinstalled && mc.theWorld == null) {
				BetaTweaksMP.serverModInstalled = false;
			}
			currentWorld = mc.theWorld;
		}
		
		if (optionsClientDraggingShortcuts && guiscreen instanceof GuiContainer) {
			GuiContainer container = (GuiContainer)guiscreen;
			int x = (Mouse.getEventX() * container.width) / mc.displayWidth;
            int y = container.height - (Mouse.getEventY() * container.height) / mc.displayHeight - 1;
            
            EntityPlayerSP player = mc.thePlayer;
        	PlayerController controller = mc.playerController;
        	int windowId = container.inventorySlots.windowId;
        	Boolean shiftClick = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        	
        	Slot slot = null;
        	Method getSlot = null;
			try {
				getSlot = GuiContainer.class.getDeclaredMethod("getSlotAtPosition", int.class, int.class);
				getSlot.setAccessible(true);
				slot = (Slot)getSlot.invoke(container, new Object[]{x, y});
			} 
			catch (NoSuchMethodException e1) {
				try {
					getSlot = GuiContainer.class.getDeclaredMethod("a", int.class, int.class);
					getSlot.setAccessible(true);
					slot = (Slot)getSlot.invoke(container, new Object[]{x, y});
				} 
				catch (NoSuchMethodException e) { e.printStackTrace(); } 
				catch (IllegalAccessException e) { e.printStackTrace(); } 
				catch (IllegalArgumentException e) { e.printStackTrace(); } 
				catch (InvocationTargetException e) { e.printStackTrace(); }  
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); } 
			catch (IllegalArgumentException e) { e.printStackTrace(); } 
			catch (InvocationTargetException e) { e.printStackTrace(); } 
			
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
    					itemClickedOn = true;
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    					lastSlotNo = slot.slotNumber;
    	            }
        			else if(Mouse.getEventButton() == 1/* && slot.getHasStack()*/) {
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    					if(player.inventory.getItemStack() != null) {
    						itemClickedOn = true;
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
    				else if(Mouse.getEventButton() == 0) {
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
    				
    					if(slot.slotNumber != lastSlotNo /*&& !itemClickedOn*/
    							&& player.inventory.getItemStack() != null && (!slot.getHasStack() 
    							|| (slot.getStack().isItemEqual(player.inventory.getItemStack()) 
    									&& slot.getStack().getMaxStackSize() > slot.getStack().stackSize))) {
    						controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    						lastSlotNo = slot.slotNumber;
						}
    			}
    			else if(Mouse.isButtonDown(0)) {
    				if (spreading) {
    					if(!slot.getHasStack()) {
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

	public boolean OnTickInGame(Minecraft minecraft) {
		if(texturePackButtonIndex != -1 && !(minecraft.currentScreen instanceof GuiIngameMenu)) {
			texturePackButtonIndex = -1;
		}
		if(initialTexturePack != null && initialTexturePack != ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack 
				&& !(minecraft.currentScreen instanceof GuiTexturePacks)) {
			initialTexturePack = null;
			if (minecraft.theWorld != null) minecraft.renderGlobal.loadRenderers();
		}

		if (modloaderMPinstalled && BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsServerAllowPlayerList
				&& Keyboard.isKeyDown(playerList.keyCode) && minecraft.currentScreen == null) {
			Gui x = new Gui();
			ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth,
					minecraft.displayHeight);
			int kx = scaledresolution.getScaledWidth();
			FontRenderer fontrenderer = minecraft.fontRenderer;
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
			x.drawRect(j6 - 1, byte2 - 1, j6 + k5 * k4, byte2 + 9 * i4, 0x80000000);
			for (int k7 = 0; k7 < j3; k7++) {
				int i8 = j6 + (k7 % k4) * k5;
				int l8 = byte2 + (k7 / k4) * 9;
				x.drawRect(i8, l8, (i8 + k5) - 1, l8 + 8, 0x20ffffff);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3008 /* GL_ALPHA_TEST */);
				if (k7 >= BetaTweaksMP.playerList.size()) {
					continue;
				}
				fontrenderer.drawStringWithShadow(BetaTweaksMP.playerList.get(k7), i8, l8, 0xffffff);
			}
		}

		if ((optionsGameplayLadderGaps && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayLadderGaps)) {

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
		if (((optionsGameplayPunchableSheep && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayPunchableSheep))
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY && Mouse.isButtonDown(0)) {

			Entity e = minecraft.objectMouseOver.entityHit;
			if (e instanceof EntitySheep) {
				if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled
						&& BetaTweaksMP.optionsGameplayPunchableSheep) {
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

		}

		if (optionsGameplayMinecartBoosters && !minecraft.theWorld.multiplayerWorld) {
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

		if (optionsGameplayElevatorBoats && !minecraft.theWorld.multiplayerWorld) {
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

		if (((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds))
				&& minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemHoe
				&& lastTickHoeDamage == minecraft.thePlayer.getCurrentEquippedItem().getItemDamage() - 1) {
			lastTickHoeDamage = -1;

			if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled && BetaTweaksMP.serverModInstalled
					&& BetaTweaksMP.optionsGameplayHoeDirtSeeds) {

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

		if ((optionsClientHideLongGrass || optionsClientHideDeadBush
				|| ((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
						&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds)))
				&& Mouse.isButtonDown(1) && minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {

			int x = minecraft.objectMouseOver.blockX;
			int y = minecraft.objectMouseOver.blockY;
			int z = minecraft.objectMouseOver.blockZ;

			if ((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
					&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds)
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
			if (((Block.blocksList[b] == Block.tallGrass && optionsClientHideLongGrass)
					|| (Block.blocksList[b] == Block.deadBush && optionsClientHideDeadBush))) {

				if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled
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
		Block.blocksList[Block.blockSteel.blockID] = null;
		Block.blocksList[Block.blockGold.blockID] = null;
		Block.blocksList[Block.blockDiamond.blockID] = null;
		new BlockOreStorageIndev(Block.blockSteel, 60, "/BetaTweaks/steelSide.png", "/BetaTweaks/steelBottom.png");
		new BlockOreStorageIndev(Block.blockGold, 59, "/BetaTweaks/goldSide.png", "/BetaTweaks/goldBottom.png");
		new BlockOreStorageIndev(Block.blockDiamond, 75, "/BetaTweaks/diamondSide.png", "/BetaTweaks/diamondBottom.png");
		
		try {
			Class.forName("SAPI");
		} 
		catch (ClassNotFoundException e) { 
			Field blocksEffectiveAgainst = ItemTool.class.getDeclaredFields()[0];
			blocksEffectiveAgainst.setAccessible(true);
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

	public static void writeConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for Beta Tweaks");

			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("options"))
					try {
						configWriter.write(System.getProperty("line.separator") + myFields[i].getName().replaceFirst("options", "")
								+ "=" + myFields[i].get(null).toString());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
			}
			configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void readConfig() {
		try {
			BufferedReader configReader = new BufferedReader(new FileReader(configFile));
			String s;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue;
				} // Ignore comments

				if (s.contains("=")) {
					String as[] = s.split("=");
					Field f1 = mod_BetaTweaks.class.getField("options" + (as[0]));

					if (f1.getType() == int.class) {
						f1.set(this, Integer.parseInt(as[1]));
					} else if (f1.getType() == Boolean.class) {
						f1.set(this, Boolean.parseBoolean(as[1]));
					} else if (f1.getType() == LogoState.class) {
						f1.set(this, LogoState.valueOf(as[1].toUpperCase()));
					}
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
