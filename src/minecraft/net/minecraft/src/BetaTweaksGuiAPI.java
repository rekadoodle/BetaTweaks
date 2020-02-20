package net.minecraft.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.mod_BetaTweaks.LogoState;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;
import de.matthiasmann.twl.theme.ThemeManager;

public class BetaTweaksGuiAPI {

	private static ModSettings settings = new ModSettings("mod_BetaTweaks");
	private static ModSettingScreen screen = new ModSettingScreen("Beta Tweaks");
	
	private static WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn(new Widget[0]);
	private static WidgetSinglecolumn widgetServer = new WidgetSinglecolumn(new Widget[0]);

	private static SettingText motd;
	
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
        widgetClientside.add(new WidgetBoolean(draggingShortcuts, "Inventory Dragging Shortcuts", "ON", "OFF"));
		
		SettingMulti logoState = new SettingMulti("optionsClientLogo", 0, new String[] {"Standard", "Animated", "Custom"});
		settings.append(logoState);
		widgetClientside.add(new WidgetMulti(logoState, "Title Screen Logo"));

		SettingBoolean panorama = new SettingBoolean("optionsClientPanoramaEnabled", mod_BetaTweaks.optionsClientPanoramaEnabled);
		settings.append(panorama);
		widgetClientside.add(new WidgetBoolean(panorama, "Title Screen Background", "Panorama", "Standard"));

		SettingBoolean quitButton = new SettingBoolean("optionsClientQuitGameButton", mod_BetaTweaks.optionsClientQuitGameButton);
		settings.append(quitButton);
		widgetClientside.add(new WidgetBoolean(quitButton, "Quit Game Button", "ON", "OFF"));
		
		SettingBoolean multiplayerMenu = new SettingBoolean("optionsClientMultiplayerMenu", mod_BetaTweaks.optionsClientMultiplayerMenu);
		settings.append(multiplayerMenu);
		widgetClientside.add(new WidgetBoolean(multiplayerMenu, "Multiplayer Menu", "ON", "OFF"));
		
		SettingBoolean ctrlsMenu = new SettingBoolean("optionsClientScrollableControls", mod_BetaTweaks.optionsClientScrollableControls);
		settings.append(ctrlsMenu);
		widgetClientside.add(new WidgetBoolean(ctrlsMenu, "Scrollable Controls", "ON", "OFF"));
		
		SettingBoolean texturepackButton = new SettingBoolean("optionsClientIngameTexturePackButton", mod_BetaTweaks.optionsClientIngameTexturePackButton);
		settings.append(texturepackButton);
		widgetClientside.add(new WidgetBoolean(texturepackButton, "ESC Menu Texture Pack Button", "ON", "OFF"));

		SettingBoolean chieveNotifications = new SettingBoolean("optionsClientDisableAchievementNotifications", mod_BetaTweaks.optionsClientDisableAchievementNotifications);
		settings.append(chieveNotifications);
		widgetClientside.add(new WidgetBoolean(chieveNotifications, "Hide Achievement Notifications"));
		
		Label blockLB = new Label();
		blockLB.setText("Block Settings");
		widgetClientside.add(blockLB);

		SettingBoolean indevStorageTextures = new SettingBoolean("optionsClientIndevStorageBlocks", mod_BetaTweaks.optionsClientIndevStorageBlocks);
		settings.append(indevStorageTextures);
		widgetClientside.add(new WidgetBoolean(indevStorageTextures, "Indev Storage Block Textures", "ON", "OFF"));
		
		SettingBoolean hideLongGrass = new SettingBoolean("optionsClientHideLongGrass", mod_BetaTweaks.optionsClientHideLongGrass);
		settings.append(hideLongGrass);
		widgetClientside.add(new WidgetBoolean(hideLongGrass, "Disable Long Grass"));
		
		SettingBoolean hideDeadBush = new SettingBoolean("optionsClientHideDeadBush", mod_BetaTweaks.optionsClientHideDeadBush);
		settings.append(hideDeadBush);
		widgetClientside.add(new WidgetBoolean(hideDeadBush, "Disable Dead Shrubs"));

		// GAMEPLAY

		SimpleButtonModel gameplayBM = new SimpleButtonModel();
		gameplayBM.addActionCallback(new ModAction(this, "gameplay", new Class[0]));
        Button gameplayBT = new Button(gameplayBM);
        gameplayBT.setText("Gameplay Settings");
        screen.append(gameplayBT);
		
        SettingBoolean punchSheep = new SettingBoolean("optionsGameplayPunchableSheep", mod_BetaTweaks.optionsGameplayPunchableSheep);
		settings.append(punchSheep);
		widgetGameplay.add(new WidgetBoolean(punchSheep, "Punch Sheep for Wool"));
		
		SettingBoolean ladderGaps = new SettingBoolean("optionsGameplayLadderGaps", mod_BetaTweaks.optionsGameplayLadderGaps);
		settings.append(ladderGaps);
		widgetGameplay.add(new WidgetBoolean(ladderGaps, "Allow Gaps in Ladders"));
		
		SettingBoolean punchTNT = new SettingBoolean("optionsGameplayLightTNTwithFist", mod_BetaTweaks.optionsGameplayLightTNTwithFist);
		settings.append(punchTNT);
		widgetGameplay.add(new WidgetBoolean(punchTNT, "Punch TNT to ignite"));
		
		SettingBoolean hoeCreatesSeeds = new SettingBoolean("optionsGameplayHoeDirtSeeds", mod_BetaTweaks.optionsGameplayHoeDirtSeeds);
		settings.append(hoeCreatesSeeds);
		widgetGameplay.add(new WidgetBoolean(hoeCreatesSeeds, "Hoe Grass for Seeds"));
		
		SettingBoolean oldMinecartBoosters = new SettingBoolean("optionsGameplayMinecartBoosters", mod_BetaTweaks.optionsGameplayMinecartBoosters);
		settings.append(oldMinecartBoosters);
		widgetGameplay.add(new WidgetBoolean(oldMinecartBoosters, "Minecart Boosters"));

		SettingBoolean elevatorBoats = new SettingBoolean("optionsGameplayElevatorBoats", mod_BetaTweaks.optionsGameplayElevatorBoats);
		settings.append(elevatorBoats);
		widgetGameplay.add(new WidgetBoolean(elevatorBoats, "Elevator Boats"));

		//SERVER SETTINGS
		
		SimpleButtonModel serverBM = new SimpleButtonModel();
		serverBM.addActionCallback(new ModAction(this, "server", new Class[0]));
        serverBT = new Button(serverBM);
        serverBT.setText("Server Settings");
        screen.append(serverBT);
		
		if (mod_BetaTweaks.modloaderMPinstalled) {
			SettingBoolean playerList = new SettingBoolean("optionsServerAllowPlayerList", BetaTweaksMP.optionsServerAllowPlayerList);
			settings.append(playerList);
			widgetServer.add(new WidgetBoolean(playerList, "Enable Player List"));
			
			motd = new SettingText("optionsServerMOTD", BetaTweaksMP.optionsServerMOTD);
			settings.append(motd);
			widgetServer.add(new WidgetText(motd, "MOTD"));
		}
		
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
	
	private String getBackendName(Setting setting) {
		Field backendName = null;
		try {
			backendName = Setting.class.getField("backendName");
		} catch (NoSuchFieldException e) {
			try {
				backendName = Setting.class.getField("backendname");
			} catch (NoSuchFieldException e2) {
				e.printStackTrace();
				e2.printStackTrace();
			}
		}
		if(backendName != null) {
			try {
				return (String)backendName.get(setting);
			} 
			catch (IllegalAccessException e) { e.printStackTrace(); }
		}
		return null;
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
	
	

}
