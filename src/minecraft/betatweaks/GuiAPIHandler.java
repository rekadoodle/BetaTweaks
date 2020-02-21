package betatweaks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.*;

import betatweaks.Config.LogoState;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextWidget;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class GuiAPIHandler {

	private static ModSettings settings = new ModSettings("mod_BetaTweaks");
	private static ModSettingScreen screen = new ModSettingScreen("Beta Tweaks");
	
	private static WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetServer = new WidgetSinglecolumn(new Widget[0]);

	private static SettingText motd;
	private WidgetText motdWidget;
	
	public static GuiAPIHandler instance = new GuiAPIHandler();

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
        
        SettingBoolean draggingShortcuts = new SettingBoolean("clientDraggingShortcuts", Config.clientDraggingShortcuts);
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
		
		SettingMulti logoState = new SettingMulti("clientLogo", 0, new String[] {"Standard", "Animated", "Custom"});
		settings.append(logoState);
		WidgetMulti logoStateWidget = new WidgetMulti(logoState, "Title Screen Logo");
		logoStateWidget.setTooltipContent(new String[] {
				"  Standard - The normal Minecraft logo",
				"  Animated - The Beta 1.3 animated logo",
				"  Custom - A custom version of the Beta 1.3 animated logo",
				"Go to: '.minecraft/config/OldCustomLogo.cfg' to configure."
	        });
		widgetClientside.add(logoStateWidget);

		SettingBoolean panorama = new SettingBoolean("clientPanoramaEnabled", Config.clientPanoramaEnabled);
		settings.append(panorama);
		WidgetBoolean panoramaWidget = new WidgetBoolean(panorama, "Title Screen Background", "Panorama", "Standard");
		panoramaWidget.setTooltipContent(new String[] {
				"  Standard - The classic dirt background",
				"  Panorama - The animated background added in Beta 1.8"
        	});
		widgetClientside.add(panoramaWidget);

		SettingBoolean quitButton = new SettingBoolean("clientQuitGameButton", Config.clientQuitGameButton);
		settings.append(quitButton);
		WidgetBoolean quitButtonWidget = new WidgetBoolean(quitButton, "Quit Game Button", "ON", "OFF");
		quitButtonWidget.setTooltipContent(new String[] {
				"Adds a button to quit the game on the Title Screen."
        	});
		widgetClientside.add(quitButtonWidget);
		
		SettingBoolean multiplayerMenu = new SettingBoolean("clientMultiplayerMenu", Config.clientMultiplayerMenu);
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
		
		SettingBoolean improvedChat = new SettingBoolean("clientImprovedChat", Config.clientImprovedChat);
		settings.append(improvedChat);
		WidgetBoolean improvedChatWidget = new WidgetBoolean(improvedChat, "Improved Chat", "ON", "OFF");
		improvedChatWidget.setTooltipContent(new String[] {
				"Adds new QOL features to the chat, such as:",
				"  Edit font size and chatbox size.",
				"  Scroll to view previous chat messages.",
				"  Copy, cut, paste, undo, etc. (Only in typing area).",
				"  Tab to autocomplete playername (If server allows).",
				"  Access previous inputs with up/down.",
				"Click in the top right with chat open",
				"or use '/chatoptions' to configure."
        	});
		widgetClientside.add(improvedChatWidget);
		
		SettingBoolean ctrlsMenu = new SettingBoolean("clientScrollableControls", Config.clientScrollableControls);
		settings.append(ctrlsMenu);
		WidgetBoolean ctrlsMenuWidget = new WidgetBoolean(ctrlsMenu, "Scrollable Controls", "ON", "OFF");
		ctrlsMenuWidget.setTooltipContent(new String[] {
				"Improves functionality of the controls menu by",
				"introducing a scrollbar and letting you unbind keys with",
				"ESC."
        	});
		widgetClientside.add(ctrlsMenuWidget);
		
		SettingBoolean texturepackButton = new SettingBoolean("clientIngameTexturePackButton", Config.clientIngameTexturePackButton);
		settings.append(texturepackButton);
		WidgetBoolean texturepackButtonWidget = new WidgetBoolean(texturepackButton, "ESC Menu Texture Pack Button", "ON", "OFF");
		texturepackButtonWidget.setTooltipContent(new String[] {
        		"Adds a button to the ingame menu that lets you change",
        		"texture packs without going to the main menu."
        	});
		widgetClientside.add(texturepackButtonWidget);

		SettingBoolean fovSlider = new SettingBoolean("clientFovSliderVisible", Config.clientFovSliderVisible);
		settings.append(fovSlider);
		WidgetBoolean fovSliderWidget = new WidgetBoolean(fovSlider, "FOV Slider", "ON", "OFF");
		fovSliderWidget.setTooltipContent(new String[] {
	       		"Adds an fov slider to the options menu.",
	       		"",
	       		"Won't work if ClientDisableEntityRendererOverride",
	       		"in config is set to false."
	       	});
		widgetClientside.add(fovSliderWidget);
		
		SettingBoolean chieveNotifications = new SettingBoolean("clientDisableAchievementNotifications", Config.clientDisableAchievementNotifications);
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

		SettingBoolean indevStorageTextures = new SettingBoolean("clientIndevStorageBlocks", Config.clientIndevStorageBlocks);
		settings.append(indevStorageTextures);
		WidgetBoolean indevStorageTexturesWidget = new WidgetBoolean(indevStorageTextures, "Indev Storage Block Textures", "ON", "OFF");
		indevStorageTexturesWidget.setTooltipContent(new String[] {
        		"Replaces the textures on iron, gold and diamond blocks",
				"with their traditional indev counterparts.",
				"(You may need to restart for the textures to load.)"
        	});
		widgetClientside.add(indevStorageTexturesWidget);
		
		SettingBoolean hideLongGrass = new SettingBoolean("clientHideLongGrass", Config.clientHideLongGrass);
		settings.append(hideLongGrass);
		WidgetBoolean hideLongGrassWidget = new WidgetBoolean(hideLongGrass, "Disable Long Grass");
		hideLongGrassWidget.setTooltipContent(new String[] {
				"Hides long grass from the world. Works on vanilla",
				"servers though you will notice when you break them."
        	});
		widgetClientside.add(hideLongGrassWidget);
		
		SettingBoolean hideDeadBush = new SettingBoolean("clientHideDeadBush", Config.clientHideDeadBush);
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
		
        SettingBoolean punchSheep = new SettingBoolean("gameplayPunchSheepForWool", Config.gameplayPunchSheepForWool);
		settings.append(punchSheep);
		WidgetBoolean punchSheepWidget = new WidgetBoolean(punchSheep, "Punch Sheep for Wool");
		punchSheepWidget.setTooltipContent(new String[] {
				"Punching wooly sheep will shear them and provide wool.",
				"This was removed in Beta 1.7 in favour of shears."
        	});
		widgetGameplay.add(punchSheepWidget);
		
		SettingBoolean ladderGaps = new SettingBoolean("gameplayLadderGaps", Config.gameplayLadderGaps);
		settings.append(ladderGaps);
		WidgetBoolean ladderGapsWidget = new WidgetBoolean(ladderGaps, "Allow Gaps in Ladders");
		ladderGapsWidget.setTooltipContent(new String[] {
				"You can climb up ladders with 1 block gaps in them.",
				"This was removed in Beta 1.5"
        	});
		widgetGameplay.add(ladderGapsWidget);
		
		SettingBoolean punchTNT = new SettingBoolean("gameplayLightTNTwithFist", Config.gameplayLightTNTwithFist);
		settings.append(punchTNT);
		WidgetBoolean punchTNTWidget = new WidgetBoolean(punchTNT, "Punch TNT to ignite");
		punchTNTWidget.setTooltipContent(new String[] {
				"TNT can be primed by punching it. This was removed",
				"in Beta 1.7 in favour of the flint & steel or a",
				"redstone signal."
        	});
		widgetGameplay.add(punchTNTWidget);
		
		SettingBoolean hoeCreatesSeeds = new SettingBoolean("gameplayHoeDirtForSeeds", Config.gameplayHoeDirtForSeeds);
		settings.append(hoeCreatesSeeds);
		WidgetBoolean hoeCreatesSeedsWidget = new WidgetBoolean(hoeCreatesSeeds, "Hoe Grass for Seeds");
		hoeCreatesSeedsWidget.setTooltipContent(new String[] {
				"Seeds can be obtained by tilling grass with a hoe.",
				"This was removed in Beta 1.6 in favour of long grass."
        	});
		widgetGameplay.add(hoeCreatesSeedsWidget);
		
		SettingBoolean oldMinecartBoosters = new SettingBoolean("gameplayMinecartBoosters", Config.gameplayMinecartBoosters);
		settings.append(oldMinecartBoosters);
		WidgetBoolean oldMinecartBoostersWidget = new WidgetBoolean(oldMinecartBoosters, "Minecart Boosters");
		oldMinecartBoostersWidget.setTooltipContent(new String[] {
				"Minecarts can be arranged in a way such that they can",
				"accelerate each other. This was removed in Beta 1.5",
				"in favour of powered rails."
        	});
		widgetGameplay.add(oldMinecartBoostersWidget);

		SettingBoolean elevatorBoats = new SettingBoolean("gameplayBoatElevators", Config.gameplayBoatElevators);
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
			SettingBoolean playerList = new SettingBoolean("serverAllowPlayerList", BetaTweaksMP.gameplayAllowPlayerList);
			settings.append(playerList);
			WidgetBoolean playerListWidget = new WidgetBoolean(playerList, "Enable Player List");
			playerListWidget.setTooltipContent(new String[] {
					"Should players be able to use the player list that was",
					"introduced in Beta 1.8"
	        	});
			widgetServer.add(playerListWidget);
			
			motd = new SettingText("serverMOTD", BetaTweaksMP.motd);
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
	
	public void handleTooltip(GuiModScreen guiscreen, int posX, int posY) {
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
            
            Graphics.drawRect(i, j, j1, k1, 0xe0000000);
            Utils.mc.fontRenderer.drawStringWithShadow(getTooltipHeader(setting), i + 5, j + 5, 0xdddddd);
            for(int l1 = 0; l1 < tooltip.length; l1++)
            {
                String line = tooltip[l1];
                Utils.mc.fontRenderer.drawStringWithShadow(line, i + 5, j + 5 + (l1 + 1) * 11, 0xdddddd);
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
		try {
			Field backendNameField = Utils.getField(Setting.class, "backendName", "backendname");
			return (String)backendNameField.get(setting);
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); }
		return null;
	}
	
	private int getScrollPos(GuiModScreen guiscreen) {
		try {
			Field mainWidgetField = Utils.getField(WidgetSimplewindow.class, "mainWidget", "mainwidget");
			return ((ScrollPane)mainWidgetField.get((WidgetSimplewindow)guiscreen.mainwidget)).getScrollPositionY();
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); }
		return -1;
	}
	
	private String getTooltipHeader(WidgetSetting widget) {
		try {
			Field niceNameReference = Utils.getField(WidgetSetting.class, "niceName", "nicename");
			String name = (String)niceNameReference.get(widget);
			
			Setting setting = null;
			String defaultValue = null;
	        if(widget instanceof WidgetBoolean) {
	        	Field settingField = Utils.getField(WidgetBoolean.class, "settingReference", "value");
				setting = (Setting)settingField.get(widget);
	        }
	        else if(widget instanceof WidgetMulti) {
	        	setting = ((WidgetMulti)widget).value;
	        }
	        else if(widget instanceof WidgetText) {
	        	Field settingField = Utils.getField(WidgetText.class, "settingReference", "value");
				setting = (Setting)settingField.get(widget);
	        }
	        
	        Field defaultValueField = Utils.getField(Setting.class, "defaultValue", "defvalue");
			defaultValue = (String)defaultValueField.get(setting).toString();
			if(setting instanceof SettingMulti) {
				Field labelsField = Utils.getField(SettingMulti.class, "labelValues", "labels");
		       	defaultValue = ((String[])labelsField.get(setting))[Integer.parseInt(defaultValueField.get(setting).toString())];
			}
			
	        return name + " - (default : " + defaultValue + ")";
		} 
		catch (IllegalAccessException e) { e.printStackTrace(); }
		return null;
	}
	
	public ArrayList<SettingBoolean> getAllBooleanSettings()
    {
        ArrayList<SettingBoolean> booleansettings = new ArrayList<SettingBoolean>();
        for (Setting setting : settings.Settings)
        {
            if (!SettingBoolean.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            booleansettings.add((SettingBoolean) setting);
        }
        return booleansettings;
    }
	
	public ArrayList<SettingMulti> getAllMultiSettings()
    {
        ArrayList<SettingMulti> multisettings = new ArrayList<SettingMulti>();
        for (Setting setting : settings.Settings)
        {
            if (!SettingMulti.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            multisettings.add((SettingMulti) setting);
        }
        return multisettings;
    }
	
	

	public void loadSettings() {
		try {
			if (Utils.mc.theWorld == null) {
				widgetGameplay.setEnabled(true);
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
				widgetGameplay.getChild(i).setEnabled(true);
				serverBT.setVisible(false);

				for(Field option : Config.options) {
					for (int j = 0; j < getAllBooleanSettings().size(); j++) {
						if (option.getName() == getBackendName(getAllBooleanSettings().get(j))) {
							getAllBooleanSettings().get(j).set((Boolean) option.get(null));
							break;
						}
					}
					for (int j = 0; j < getAllMultiSettings().size(); j++) {
						if (option.getName() == getBackendName(getAllMultiSettings().get(j))) {
							if (option.getType() == LogoState.class) {
								getAllMultiSettings().get(j).set(LogoState.valueOf(option.get(null).toString()).ordinal());
								break;
							}
						}
					}
				}
			}

			else if (Utils.mc.theWorld.multiplayerWorld && mod_BetaTweaks.modloaderMPinstalled
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

				for(Field options : BetaTweaksMP.options) {
					for (int j = 0; j < getAllBooleanSettings().size(); j++) {
						if (options.getName() == getBackendName(getAllBooleanSettings().get(j))) {
							getAllBooleanSettings().get(j).set((Boolean) options.get(null));
							break;
						}
					}
				}
				motd.set(BetaTweaksMP.motd);
			} else if (Utils.mc.theWorld.multiplayerWorld) {
				widgetGameplay.setEnabled(false);
				serverBT.setVisible(false);
			}
		} 
		catch (Exception exception) { exception.printStackTrace(); }
	}
	
	public void updateSettings() {
		try {
			if (Utils.mc.theWorld == null || !Utils.mc.theWorld.multiplayerWorld) {
				for(Field option : Config.optionsGameplay) {
					for (int j = 0; j < getAllBooleanSettings().size(); j++) {
						if (option.getName() == getBackendName(getAllBooleanSettings().get(j))) {
							option.set(null, getAllBooleanSettings().get(j).get());
							break;
						}
					}
					for (int j = 0; j < getAllMultiSettings().size(); j++) {
						if (option.getName() == getBackendName(getAllMultiSettings().get(j))) {
							if (option.getType() == LogoState.class) {
								option.set(null, LogoState.values()[getAllMultiSettings().get(j).get()]);
								break;
							}
						}
					}
				}
			}
			else if (Utils.mc.theWorld.multiplayerWorld && mod_BetaTweaks.modloaderMPinstalled
					&& BetaTweaksMP.serverModInstalled) {
				if (BetaTweaksMP.isOp) {
					List<Integer> options2 = new ArrayList<Integer>();
					
					for(Field option : BetaTweaksMP.options) {
						for (int j = 0; j < getAllBooleanSettings().size(); j++) {
							if (option.getName() == getBackendName(getAllBooleanSettings().get(j))) {
								options2.add(getAllBooleanSettings().get(j).get() ? 1 : 0);
								break;
							}
						}
					}
					Boolean settingsChanged = false;
					for (int i = 0; i < options2.size(); i++) {
						if (BetaTweaksMP.options1.get(i) != options2.get(i)) {
							settingsChanged = true;
							break;
						}
					}
					if (motd.get() != BetaTweaksMP.motd) {
						settingsChanged = true;
					}
					if (settingsChanged) {
						BetaTweaksMP.options1 = options2;
						BetaTweaksMP.updateServerSettings(motd.get());
					}

				}
			}
			
			for(Field option : Config.optionsClient) {
				for (int j = 0; j < getAllBooleanSettings().size(); j++) {
					if (option.getName() == getBackendName(getAllBooleanSettings().get(j))) {
						option.set(null, getAllBooleanSettings().get(j).get());
						break;
					}
				}
				for (int j = 0; j < getAllMultiSettings().size(); j++) {
					if (option.getName() == getBackendName(getAllMultiSettings().get(j))) {
						if (option.getType() == LogoState.class) {
							option.set(null, LogoState.values()[getAllMultiSettings().get(j).get()]);
							break;
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		Config.writeConfig();
	}

}
