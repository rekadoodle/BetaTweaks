package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.mod_BetaTweaks.LogoState;

import org.lwjgl.input.Mouse;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextWidget;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class BetaTweaksGuiAPI {

	private static ModSettings settings = new ModSettings("mod_BetaTweaks");
	private static ModSettingScreen screen = new ModSettingScreen("Beta Tweaks");
	
	private static WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetServer = new WidgetSinglecolumn(new Widget[0]);

	private static SettingText motd;
	private WidgetText motdWidget;
	
	public static BetaTweaksGuiAPI instance = new BetaTweaksGuiAPI();

	private Button serverBT;
	
	
	public void init() {

		// CLIENTSIDE
		
		SimpleButtonModel clientsideBM = new SimpleButtonModel();
		clientsideBM.addActionCallback(new ModAction(this, "clientside", new Class[0]));
        Button clientsideBT = new Button(clientsideBM);
        clientsideBT.setText("Clientside Settings");
        screen.append(clientsideBT);
        
        Label guihudLB = new Label();
		guihudLB.setText("GUI/HUD Settings");
		widgetClientside.add(guihudLB);
        
        SettingBoolean draggingShortcuts = new SettingBoolean("optionsClientDraggingShortcuts", mod_BetaTweaks.optionsClientDraggingShortcuts);
        settings.append(draggingShortcuts);
        WidgetBoolean draggingShortcutsWidget = new WidgetBoolean(draggingShortcuts, "Inventory Dragging Shortcuts", "ON", "OFF");
        draggingShortcutsWidget.setTooltipContent(new String[] {
        		"Adds some shortcuts to help spread, collect or transfer",
        		"items in the inventory, here are the main ones:",
        		"  Hold LMB to collect or spread items evenly.",
        		"  Hold RMB to drop 1 item on each slot.",
        		"  Hold Shift + LMB to shift click lots of items quickly.",
        		"  Press Shift + LMB on a crafting result to craft a stack.",
        		"  Press Q to drop 1 of the item held.",
        		"  Hold Q to quickly drop the item hovered."
        	});
        widgetClientside.add(draggingShortcutsWidget);
		
		SettingMulti logoState = new SettingMulti("optionsClientLogo", 0, new String[] {"Standard", "Animated", "Custom"});
		settings.append(logoState);
		WidgetMulti logoStateWidget = new WidgetMulti(logoState, "Title Screen Logo");
		logoStateWidget.setTooltipContent(new String[] {
				"  Standard - The normal Minecraft logo",
				"  Animated - The Beta 1.3 animated logo",
				"  Custom - A custom version of the Beta 1.3 animated logo",
				"Go to: '.minecraft/config/OldCustomLogo.cfg' to configure."
	        });
		widgetClientside.add(logoStateWidget);

		SettingBoolean panorama = new SettingBoolean("optionsClientPanoramaEnabled", mod_BetaTweaks.optionsClientPanoramaEnabled);
		settings.append(panorama);
		WidgetBoolean panoramaWidget = new WidgetBoolean(panorama, "Title Screen Background", "Panorama", "Standard");
		panoramaWidget.setTooltipContent(new String[] {
				"  Standard - The classic dirt background",
				"  Panorama - The animated background added in Beta 1.8"
        	});
		widgetClientside.add(panoramaWidget);

		SettingBoolean quitButton = new SettingBoolean("optionsClientQuitGameButton", mod_BetaTweaks.optionsClientQuitGameButton);
		settings.append(quitButton);
		WidgetBoolean quitButtonWidget = new WidgetBoolean(quitButton, "Quit Game Button", "ON", "OFF");
		quitButtonWidget.setTooltipContent(new String[] {
				"Adds a button to quit the game on the Title Screen."
        	});
		widgetClientside.add(quitButtonWidget);
		
		SettingBoolean multiplayerMenu = new SettingBoolean("optionsClientMultiplayerMenu", mod_BetaTweaks.optionsClientMultiplayerMenu);
		settings.append(multiplayerMenu);
		WidgetBoolean multiplayerMenuWidget = new WidgetBoolean(multiplayerMenu, "Multiplayer Menu", "ON", "OFF");
		multiplayerMenuWidget.setTooltipContent(new String[] {
				"Uses the Beta 1.8 menu which allows multiple servers to",
				"be saved.",
				"You have the option to ping them but servers with DDOS",
				"protection can give you End Of Stream when you try to",
				"join them. To fix this you may have to restart the client",
				"or wait roughly 5-10 minutes."
        	});
		widgetClientside.add(multiplayerMenuWidget);
		
		SettingBoolean ctrlsMenu = new SettingBoolean("optionsClientScrollableControls", mod_BetaTweaks.optionsClientScrollableControls);
		settings.append(ctrlsMenu);
		WidgetBoolean ctrlsMenuWidget = new WidgetBoolean(ctrlsMenu, "Scrollable Controls", "ON", "OFF");
		ctrlsMenuWidget.setTooltipContent(new String[] {
				"Improves functionality of the controls menu by",
				"introducing a scrollbar and letting you unbind keys with",
				"ESC."
        	});
		widgetClientside.add(ctrlsMenuWidget);
		
		SettingBoolean texturepackButton = new SettingBoolean("optionsClientIngameTexturePackButton", mod_BetaTweaks.optionsClientIngameTexturePackButton);
		settings.append(texturepackButton);
		WidgetBoolean texturepackButtonWidget = new WidgetBoolean(texturepackButton, "ESC Menu Texture Pack Button", "ON", "OFF");
		texturepackButtonWidget.setTooltipContent(new String[] {
        		"Adds a button to the ingame menu that lets you change",
        		"texture packs without going to the main menu."
        	});
		widgetClientside.add(texturepackButtonWidget);

		SettingBoolean fovSlider = new SettingBoolean("optionsClientFovSliderVisible", mod_BetaTweaks.optionsClientFovSliderVisible);
		settings.append(fovSlider);
		WidgetBoolean fovSliderWidget = new WidgetBoolean(fovSlider, "FOV Slider", "ON", "OFF");
		fovSliderWidget.setTooltipContent(new String[] {
	       		"Adds an fov slider to the options menu.",
	       		"",
	       		"Won't work if ClientDisableEntityRendererOverride",
	       		"in config is set to false."
	       	});
		widgetClientside.add(fovSliderWidget);
		
		SettingBoolean chieveNotifications = new SettingBoolean("optionsClientDisableAchievementNotifications", mod_BetaTweaks.optionsClientDisableAchievementNotifications);
		settings.append(chieveNotifications);
		WidgetBoolean chieveNotificationsWidget = new WidgetBoolean(chieveNotifications, "Hide Achievement Notifications");
		chieveNotificationsWidget.setTooltipContent(new String[] {
				"Hides the popup notifications that appear when you get",
				"an achievement."
        	});
		widgetClientside.add(chieveNotificationsWidget);
		
		Label blockLB = new Label();
		blockLB.setText("Block Settings");
		widgetClientside.add(blockLB);

		SettingBoolean indevStorageTextures = new SettingBoolean("optionsClientIndevStorageBlocks", mod_BetaTweaks.optionsClientIndevStorageBlocks);
		settings.append(indevStorageTextures);
		WidgetBoolean indevStorageTexturesWidget = new WidgetBoolean(indevStorageTextures, "Indev Storage Block Textures", "ON", "OFF");
		indevStorageTexturesWidget.setTooltipContent(new String[] {
        		"Replaces the textures on iron, gold and diamond blocks",
				"with their traditional indev counterparts.",
				"(You may need to restart for the textures to load.)"
        	});
		widgetClientside.add(indevStorageTexturesWidget);
		
		SettingBoolean hideLongGrass = new SettingBoolean("optionsClientHideLongGrass", mod_BetaTweaks.optionsClientHideLongGrass);
		settings.append(hideLongGrass);
		WidgetBoolean hideLongGrassWidget = new WidgetBoolean(hideLongGrass, "Disable Long Grass");
		hideLongGrassWidget.setTooltipContent(new String[] {
				"Hides long grass from the world. Works on vanilla",
				"servers though you will notice when you break them."
        	});
		widgetClientside.add(hideLongGrassWidget);
		
		SettingBoolean hideDeadBush = new SettingBoolean("optionsClientHideDeadBush", mod_BetaTweaks.optionsClientHideDeadBush);
		settings.append(hideDeadBush);
		WidgetBoolean hideDeadBushWidget = new WidgetBoolean(hideDeadBush, "Disable Dead Shrubs");
		hideDeadBushWidget.setTooltipContent(new String[] {
				"Hides dead shrubs that spawn in the desert. Works on",
				"vanilla servers though you will notice when you break",
				"them."
        	});
		widgetClientside.add(hideDeadBushWidget);

		// GAMEPLAY

		SimpleButtonModel gameplayBM = new SimpleButtonModel();
		gameplayBM.addActionCallback(new ModAction(this, "gameplay", new Class[0]));
        Button gameplayBT = new Button(gameplayBM);
        gameplayBT.setText("Gameplay Settings");
        screen.append(gameplayBT);
		
        SettingBoolean punchSheep = new SettingBoolean("optionsGameplayPunchableSheep", mod_BetaTweaks.optionsGameplayPunchableSheep);
		settings.append(punchSheep);
		WidgetBoolean punchSheepWidget = new WidgetBoolean(punchSheep, "Punch Sheep for Wool");
		punchSheepWidget.setTooltipContent(new String[] {
				"Punching wooly sheep will shear them and provide wool.",
				"This was removed in Beta 1.7 in favour of shears."
        	});
		widgetGameplay.add(punchSheepWidget);
		
		SettingBoolean ladderGaps = new SettingBoolean("optionsGameplayLadderGaps", mod_BetaTweaks.optionsGameplayLadderGaps);
		settings.append(ladderGaps);
		WidgetBoolean ladderGapsWidget = new WidgetBoolean(ladderGaps, "Allow Gaps in Ladders");
		ladderGapsWidget.setTooltipContent(new String[] {
				"You can climb up ladders with 1 block gaps in them.",
				"This was removed in Beta 1.5"
        	});
		widgetGameplay.add(ladderGapsWidget);
		
		SettingBoolean punchTNT = new SettingBoolean("optionsGameplayLightTNTwithFist", mod_BetaTweaks.optionsGameplayLightTNTwithFist);
		settings.append(punchTNT);
		WidgetBoolean punchTNTWidget = new WidgetBoolean(punchTNT, "Punch TNT to ignite");
		punchTNTWidget.setTooltipContent(new String[] {
				"TNT can be primed by punching it. This was removed",
				"in Beta 1.7 in favour of the flint & steel or a",
				"redstone signal."
        	});
		widgetGameplay.add(punchTNTWidget);
		
		SettingBoolean hoeCreatesSeeds = new SettingBoolean("optionsGameplayHoeDirtSeeds", mod_BetaTweaks.optionsGameplayHoeDirtSeeds);
		settings.append(hoeCreatesSeeds);
		WidgetBoolean hoeCreatesSeedsWidget = new WidgetBoolean(hoeCreatesSeeds, "Hoe Grass for Seeds");
		hoeCreatesSeedsWidget.setTooltipContent(new String[] {
				"Seeds can be obtained by tilling grass with a hoe.",
				"This was removed in Beta 1.6 in favour of long grass."
        	});
		widgetGameplay.add(hoeCreatesSeedsWidget);
		
		SettingBoolean oldMinecartBoosters = new SettingBoolean("optionsGameplayMinecartBoosters", mod_BetaTweaks.optionsGameplayMinecartBoosters);
		settings.append(oldMinecartBoosters);
		WidgetBoolean oldMinecartBoostersWidget = new WidgetBoolean(oldMinecartBoosters, "Minecart Boosters");
		oldMinecartBoostersWidget.setTooltipContent(new String[] {
				"Minecarts can be arranged in a way such that they can",
				"accelerate each other. This was removed in Beta 1.5",
				"in favour of powered rails."
        	});
		widgetGameplay.add(oldMinecartBoostersWidget);

		SettingBoolean elevatorBoats = new SettingBoolean("optionsGameplayElevatorBoats", mod_BetaTweaks.optionsGameplayElevatorBoats);
		settings.append(elevatorBoats);
		WidgetBoolean elevatorBoatsWidget = new WidgetBoolean(elevatorBoats, "Elevator Boats");
		elevatorBoatsWidget.setTooltipContent(new String[] {
        		"Submerged boats rise very quickly in water.",
				"This was removed in Beta 1.6"
        	});
		widgetGameplay.add(elevatorBoatsWidget);

		//SERVER SETTINGS
		
		SimpleButtonModel serverBM = new SimpleButtonModel();
		serverBM.addActionCallback(new ModAction(this, "server", new Class[0]));
        serverBT = new Button(serverBM);
        serverBT.setText("Server Settings");
        screen.append(serverBT);
		
		if (mod_BetaTweaks.modloaderMPinstalled) {
			SettingBoolean playerList = new SettingBoolean("optionsServerAllowPlayerList", BetaTweaksMP.optionsServerAllowPlayerList);
			settings.append(playerList);
			WidgetBoolean playerListWidget = new WidgetBoolean(playerList, "Enable Player List");
			playerListWidget.setTooltipContent(new String[] {
					"Should players be able to use the player list that was",
					"introduced in Beta 1.8"
	        	});
			widgetServer.add(playerListWidget);
			
			motd = new SettingText("optionsServerMOTD", BetaTweaksMP.optionsServerMOTD);
			settings.append(motd);
			motdWidget = new WidgetText(motd, "MOTD");
			motdWidget.setTooltipContent(new String[] {
					"The server description displayed in the server browser."
	        	});
			widgetServer.add(motdWidget);
		}
		
	}
	
	private WidgetSimplewindow clientside;
	public void clientside()
    {
		if(clientside == null) {
			clientside = new WidgetSimplewindow(widgetClientside, "Clientside Settings");
		}
		GuiModScreen.show(clientside);
    }
	
	private WidgetSimplewindow gameplay;
	public void gameplay()
    {
		if(gameplay == null) {
			gameplay = new WidgetSimplewindow(widgetGameplay, "Gameplay Settings");
		}
		GuiModScreen.show(gameplay);
    }
	
	private WidgetSimplewindow server;
	public void server()
    {
		if(server == null) {
			server = new WidgetSimplewindow(widgetServer, "Server Settings");
		}
		GuiModScreen.show(server);
    }
	
	private int lastMouseX;
    private int lastMouseY;
    private int scrollPos;
    private long mouseStillTime;
	
	public void handleTooltip(GuiModScreen guiscreen) {
		int posX = (Mouse.getX() * guiscreen.width) / ModLoader.getMinecraftInstance().displayWidth;
        int posY = guiscreen.height - (Mouse.getY() * guiscreen.height) / ModLoader.getMinecraftInstance().displayHeight - 1;
		
        if(Math.abs(posX - lastMouseX) > 5 || Math.abs(posY - lastMouseY) > 5 ||
        		scrollPos != getScrollPos(guiscreen))
        {
            lastMouseX = posX;
            lastMouseY = posY;
            scrollPos = getScrollPos(guiscreen);
            mouseStillTime = System.currentTimeMillis();
            return;
        }
        int k = 700;
        if(System.currentTimeMillis() < mouseStillTime + (long)k)
        {
            return;
        }
        WidgetSetting setting = null;
        if (guiscreen.mainwidget == clientside) {
        	Widget x = widgetClientside.getWidgetAt(posX, posY);
			if(x instanceof Button) {
				for(int i = 0; i < widgetClientside.getNumChildren(); i++) {
					Widget n = widgetClientside.getChild(i);
					if(widgetEqualsButton(n, (Button)x)) {
						setting = (WidgetSetting)n;
					}
				}
			}
		}
        else if (guiscreen.mainwidget == gameplay) {
        	Widget x = widgetGameplay.getWidgetAt(posX, posY);
			if(x instanceof Button) {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++) {
					Widget n = widgetGameplay.getChild(i);
					if(widgetEqualsButton(n, (Button)x)) {
						setting = (WidgetSetting)n;
					}
				}
			}
		}
        else if (guiscreen.mainwidget == server) {
        	Widget x = widgetServer.getWidgetAt(posX, posY);
			if(x instanceof Button) {
				for(int i = 0; i < widgetServer.getNumChildren(); i++) {
					Widget n = widgetServer.getChild(i);
					if(widgetEqualsButton(n, (Button)x)) {
						setting = (WidgetSetting)n;
					}
				}
			}
			else if(x instanceof TextWidget) {
				setting = motdWidget;
			}
		}
        if(setting != null && setting.getTooltipContent() instanceof String[])
        {
        	String[] tooltip = (String[])setting.getTooltipContent();
        	int i = guiscreen.width / 2 - 150;
            int j = guiscreen.height / 6 - 5;
            if(posY <= j + 98)
            {
            	//j += 105;
                j += 182 - 11 * tooltip.length;
            }
            int j1 = i + 150 + 150;
            //int k1 = j + 84 + 10;
            int k1 = j + 11 * tooltip.length + 17;
            
            guiscreen.drawGradientRect(i, j, j1, k1, 0xe0000000, 0xe0000000);
            guiscreen.fontRenderer.drawStringWithShadow(getTooltipHeader(setting), i + 5, j + 5, 0xdddddd);
            for(int l1 = 0; l1 < tooltip.length; l1++)
            {
                String line = tooltip[l1];
                guiscreen.fontRenderer.drawStringWithShadow(line, i + 5, j + 5 + (l1 + 1) * 11, 0xdddddd);
            }
        }
	}
	
	private Boolean widgetEqualsButton(Widget widget, Button button) {
		if(widget instanceof WidgetBoolean) {
			return ((WidgetBoolean)widget).userString().equals(button.getText());
		}
		if(widget instanceof WidgetMulti) {
			return ((WidgetMulti)widget).userString().equals(button.getText());
        }
        if(widget instanceof WidgetText) {
        	return ((WidgetText)widget).userString().equals(button.getText());
        }
		return false;
	}
	
	private String getBackendName(Setting setting) {
		Field backendNameField = null;
		try {
			backendNameField = Setting.class.getField("backendName");
		} catch (NoSuchFieldException e) {
			try {
				backendNameField = Setting.class.getField("backendname");
			} catch (NoSuchFieldException e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
		if(backendNameField != null) {
			try {
				return (String)backendNameField.get(setting);
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); }
		}
		return null;
	}
	
	private int getScrollPos(GuiModScreen guiscreen) {
		Field mainWidgetField = null;
		try {
			mainWidgetField = WidgetSimplewindow.class.getField("mainWidget");
		} catch (NoSuchFieldException e) {
			try {
				mainWidgetField = WidgetSimplewindow.class.getField("mainwidget");
			} catch (NoSuchFieldException e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
		if(mainWidgetField != null) {
			try {
				return ((ScrollPane)mainWidgetField.get((WidgetSimplewindow)guiscreen.mainwidget)).getScrollPositionY();
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); }
		}
		return -1;
	}
	
	private String getTooltipHeader(WidgetSetting widget) {
		Field niceNameReference = null;
		try {
			niceNameReference = WidgetSetting.class.getField("niceName");
		} catch (NoSuchFieldException e) {
			try {
				niceNameReference = WidgetSetting.class.getField("nicename");
			} catch (NoSuchFieldException e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
		String name = null;
		if(niceNameReference != null) {
			try {
				name = (String)niceNameReference.get(widget);
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); }
		}
		
		Setting setting = null;
		String defaultValue = null;
        if(widget instanceof WidgetBoolean) {
        	Field settingField = null;
        	try {
        		settingField = WidgetBoolean.class.getField("settingReference");
        	} 
        	catch (NoSuchFieldException e) {
    			try {
    				settingField = WidgetBoolean.class.getField("value");
    			} 
    			catch (NoSuchFieldException e2) { e.printStackTrace(); e2.printStackTrace(); }
    		}
    		if(settingField != null) {
    			try {
    				setting = (Setting)settingField.get(widget);
    			} 
    			catch (IllegalAccessException e) { e.printStackTrace(); }
    		}
        }
        else if(widget instanceof WidgetMulti) {
        	setting = ((WidgetMulti)widget).value;
        }
        else if(widget instanceof WidgetText) {
        	Field settingField = null;
        	try {
        		settingField = WidgetText.class.getField("settingReference");
        	} 
        	catch (NoSuchFieldException e) {
    			try {
    				settingField = WidgetText.class.getField("value");
    			} 
    			catch (NoSuchFieldException e2) { e.printStackTrace(); e2.printStackTrace(); }
    		}
    		if(settingField != null) {
    			try {
    				setting = (Setting)settingField.get(widget);
    			} 
    			catch (IllegalAccessException e) { e.printStackTrace(); }
    		}
        }
        
        Field defaultValueField = null;
        try {
        	defaultValueField = Setting.class.getField("defaultValue");
        } 
        catch (NoSuchFieldException e) {
        	try {
    			defaultValueField = Setting.class.getField("defvalue");
    		} 
    		catch (NoSuchFieldException e2) { e.printStackTrace(); e2.printStackTrace(); }
    	}
           
    	if(defaultValueField != null) {
    		try {
    			defaultValue = (String)defaultValueField.get(setting).toString();
    			if(setting instanceof SettingMulti) {
    				try {
    		        	Field labelsField = SettingMulti.class.getField("labelValues");
    		        	defaultValue = ((String[])labelsField.get(setting))[Integer.parseInt(defaultValueField.get(setting).toString())];
    		        } 
    		        catch (NoSuchFieldException e) {
    		        	try {
    		        		Field labelsField = SettingMulti.class.getField("labels");
    		        		defaultValue = ((String[])labelsField.get(setting))[Integer.parseInt(defaultValueField.get(setting).toString())];
    		    		} 
    		    		catch (NoSuchFieldException e2) { e.printStackTrace(); e2.printStackTrace(); }
    		    	}
    			}
    		} 
    		catch (IllegalAccessException e) { e.printStackTrace(); }
    	}
        
        return name + " - (default : " + defaultValue + ")";
	}
	
	public ArrayList<SettingBoolean> getAllBooleanSettings()
    {
        ArrayList<SettingBoolean> settings = new ArrayList<SettingBoolean>();
        for (Setting setting : this.settings.Settings)
        {
            if (!SettingBoolean.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingBoolean) setting);
        }
        return settings;
    }
	
	public ArrayList<SettingMulti> getAllMultiSettings()
    {
        ArrayList<SettingMulti> settings = new ArrayList<SettingMulti>();
        for (Setting setting : this.settings.Settings)
        {
            if (!SettingMulti.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingMulti) setting);
        }
        return settings;
    }
	
	

	public void loadSettings() {
		if (ModLoader.getMinecraftInstance().theWorld == null) {
			widgetGameplay.setEnabled(true);
			for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
			widgetGameplay.getChild(i).setEnabled(true);
			serverBT.setVisible(false);

			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("options")) {
					try {
						for (int j = 0; j < getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == getBackendName(getAllBooleanSettings().get(j))) {
								getAllBooleanSettings().get(j).set((Boolean) myFields[i].get(null));
							}
						}
						for (int j = 0; j < getAllMultiSettings().size(); j++) {
							if (myFields[i].getName() == getBackendName(getAllMultiSettings().get(j))) {
								if (myFields[i].getType() == LogoState.class) {
									getAllMultiSettings().get(j)
											.set(LogoState.valueOf(myFields[i].get(null).toString()).ordinal());
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}

		else if (ModLoader.getMinecraftInstance().theWorld.multiplayerWorld && mod_BetaTweaks.modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled) {
			widgetGameplay.setEnabled(true);
			if (BetaTweaksMP.isOp) {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(true);
				serverBT.setVisible(true);
			} else {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(false);
				serverBT.setVisible(false);
			}

			Field[] myFields = BetaTweaksMP.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("optionsGameplay") || myFields[i].getName().contains("optionsServer")) {
					try {
						for (int j = 0; j < getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == getBackendName(getAllBooleanSettings().get(j))) {
								getAllBooleanSettings().get(j).set((Boolean) myFields[i].get(null));
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
			motd.set(BetaTweaksMP.optionsServerMOTD);
		} else if (ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			widgetGameplay.setEnabled(false);
			serverBT.setVisible(false);
		}
	}
	
	public void updateSettings() {
		
		if (ModLoader.getMinecraftInstance().theWorld == null || !ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("optionsGameplay"))
					try {
						for (int j = 0; j < getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == getBackendName(getAllBooleanSettings().get(j))) {
								myFields[i].set(null, getAllBooleanSettings().get(j).get());
							}
						}
						for (int j = 0; j < getAllMultiSettings().size(); j++) {
							if (myFields[i].getName() == getBackendName(getAllMultiSettings().get(j))) {
								if (myFields[i].getType() == LogoState.class) {
									myFields[i].set(null,
											LogoState.values()[getAllMultiSettings().get(j).get()]);
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
			}
		}
		else if (ModLoader.getMinecraftInstance().theWorld.multiplayerWorld && mod_BetaTweaks.modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled) {
			if (BetaTweaksMP.isOp) {
				List<Integer> options2 = new ArrayList<Integer>();
				Field[] myFields = BetaTweaksMP.class.getFields();
				for (int i = 0; i < myFields.length; i++) {
					if (myFields[i].getName().contains("optionsGameplay") || myFields[i].getName().contains("optionsServer"))
						try {
							for (int j = 0; j < getAllBooleanSettings().size(); j++) {
								if (myFields[i]
										.getName() == getBackendName(getAllBooleanSettings().get(j))) {
									options2.add(getAllBooleanSettings().get(j).get() ? 1 : 0);
									// mod_BetaTweaksMP.options.add(guiapiSettings.getAllBooleanSettings().get(j).get()
									// ? 1 : 0);
									// myFields[i].set(null, guiapiSettings.getAllBooleanSettings().get(j).get());
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
				}
				Boolean settingsChanged = false;
				for (int i = 0; i < options2.size(); i++) {
					if (BetaTweaksMP.options.get(i) != options2.get(i)) {
						settingsChanged = true;
						break;
					}
				}
				if (motd.get() != BetaTweaksMP.optionsServerMOTD) {
					settingsChanged = true;
				}
				if (settingsChanged) {
					BetaTweaksMP.options = options2;
					BetaTweaksMP.updateServerSettings(motd.get());
				}

			}
		}
		Field[] myFieldsClient = mod_BetaTweaks.class.getFields();
		for (int i = 0; i < myFieldsClient.length; i++) {
			if (myFieldsClient[i].getName().contains("optionsClient"))
				try {
					for (int j = 0; j < getAllBooleanSettings().size(); j++) {
						if (myFieldsClient[i].getName() == getBackendName(getAllBooleanSettings().get(j))) {
							myFieldsClient[i].set(null, getAllBooleanSettings().get(j).get());
						}
					}
					for (int j = 0; j < getAllMultiSettings().size(); j++) {
						if (myFieldsClient[i].getName() == getBackendName(getAllMultiSettings().get(j))) {
							if (myFieldsClient[i].getType() == LogoState.class) {
								myFieldsClient[i].set(null,
										LogoState.values()[getAllMultiSettings().get(j).get()]);
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
		}

		mod_BetaTweaks.writeConfig();
	}

}
