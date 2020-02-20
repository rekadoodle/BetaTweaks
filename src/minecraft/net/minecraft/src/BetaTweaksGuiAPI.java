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
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;
import de.matthiasmann.twl.theme.ThemeManager;

public class BetaTweaksGuiAPI {

	private static ModSettings guiapiSettings = new ModSettings("mod_BetaTweaks");
	private static ModSettingScreen betaTweaksMain = new ModSettingScreen("Beta Tweaks");
	
	private static WidgetSinglecolumn clientSideSettingsBase = new WidgetSinglecolumn();
	private static WidgetSinglecolumn gameplaySettingsBase = new WidgetSinglecolumn();
	private static WidgetSinglecolumn serverSettingsBase = new WidgetSinglecolumn();

	private static SettingText motd;
	
	public static BetaTweaksGuiAPI instance = new BetaTweaksGuiAPI();

	public void init() {

		// CLIENTSIDE
		

		Label label1 = new Label();
		label1.setText("GUI/HUD Settings");
		clientSideSettingsBase.heightOverrideExceptions.put(label1, 0);
		clientSideSettingsBase.add(label1);
		
		SettingBoolean draggingShortcuts = guiapiSettings.addSetting(clientSideSettingsBase, "Inventory Dragging Shortcuts",
				"optionsClientDraggingShortcuts", mod_BetaTweaks.optionsClientDraggingShortcuts, "ON", "OFF");
		((WidgetBoolean) draggingShortcuts.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Inventory Dragging Shortcuts:" + "\r\n" + "(default:ON)"
						+ "\r\n\r\n" + "Adds some shortcuts to help spread, collect or transfer items in the inventory.", false));

		SettingMulti logoState = guiapiSettings.addSetting(clientSideSettingsBase, "Title Screen Logo",
				"optionsClientLogo", 0, "Standard", "Animated", "Custom");
		((WidgetMulti) logoState.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Title Screen Logo: (default:Standard)" + "\r\n\r\n"
						+ "Standard" + "\r\n" + "The normal Minecraft logo." + "\r\n\r\n" + "Animated" + "\r\n"
						+ "The Beta 1.3 animated logo." + "\r\n\r\n" + "Custom" + "\r\n"
						+ "A custom version of the Beta 1.3 animated logo. Go to: " + "\r\n"
						+ ".minecraft/config/OldCustomLogo.cfg" + "\r\n" + " to configure this.", false));

		// x.setText("BAM");

		// logoState.getTheme()
		// ThemeManager.
		// Image y = new Image;
		// y = y.createTintedVersion(new Color(0xFF000000));

		// ((WidgetMulti)logoState.displayWidget).button.setTooltipContent(x);

		SettingBoolean panorama = guiapiSettings.addSetting(clientSideSettingsBase, "Title Screen Background",
				"optionsClientPanoramaEnabled", mod_BetaTweaks.optionsClientPanoramaEnabled, "Panorama", "Standard");
		((WidgetBoolean) panorama.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Title Screen Background:" + "\r\n" + "(default:Standard)"
						+ "\r\n\r\n" + "Standard" + "\r\n" + "The classic dirt background" + "\r\n\r\n" + "Panorama"
						+ "\r\n" + "The animated background added in Beta 1.8", false));

		SettingBoolean quitButton = guiapiSettings.addSetting(clientSideSettingsBase, "Quit Game Button",
				"optionsClientQuitGameButton", mod_BetaTweaks.optionsClientQuitGameButton, "ON", "OFF");
		((WidgetBoolean) quitButton.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Quit Game Button: (default:ON)" + "\r\n\r\n"
						+ "Enables the Quit Game button in the title screen which is usually disabled for some reason.",
				false));

		SettingBoolean multiplayerMenu = guiapiSettings.addSetting(clientSideSettingsBase, "Multiplayer Menu",
				"optionsClientMultiplayerMenu", mod_BetaTweaks.optionsClientMultiplayerMenu, "ON", "OFF");
		((WidgetBoolean) multiplayerMenu.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Multiplayer Menu: (default:ON)" + "\r\n\r\n"
						+ "Uses the Beta 1.8 menu which allows multiple servers to be saved.", false));

		SettingBoolean ctrlsMenu = guiapiSettings.addSetting(clientSideSettingsBase, "Scrollable Controls",
				"optionsClientScrollableControls", mod_BetaTweaks.optionsClientScrollableControls, "ON", "OFF");
		((WidgetBoolean) ctrlsMenu.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Scrollable Controls: (default:ON)" + "\r\n\r\n"
						+ "Improves functionality of the controls menu by introducing a scrollbar and letting you unbind keys with ESC.",
				false));

		SettingBoolean texturepackButton = guiapiSettings.addSetting(clientSideSettingsBase,
				"ESC Menu Texture Pack Button", "optionsClientIngameTexturePackButton",
				mod_BetaTweaks.optionsClientIngameTexturePackButton, "ON", "OFF");
		((WidgetBoolean) texturepackButton.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("ESC Menu Texture Pack Button: (default:OFF)" + "\r\n\r\n"
						+ "Allows you to change texture packs from the ingame menu.", false));

		SettingBoolean chieveNotifications = guiapiSettings.addSetting(clientSideSettingsBase,
				"Hide Achievement Notifications", "optionsClientDisableAchievementNotifications",
				mod_BetaTweaks.optionsClientDisableAchievementNotifications);
		((WidgetBoolean) chieveNotifications.displayWidget).button.setTooltipContent(
				GuiApiHelper.makeTextArea("Hide Achievement Notifications:" + "\r\n" + " (default:false)" + "\r\n\r\n"
						+ "Hides the popup notifications that appear when you get an achievement.", false));

		Label label2 = new Label();
		label2.setText("Block Settings");
		clientSideSettingsBase.heightOverrideExceptions.put(label2, 0);
		clientSideSettingsBase.add(label2);

		SettingBoolean indevStorageTextures = guiapiSettings.addSetting(clientSideSettingsBase,
				"Indev Storage Block Textures", "optionsClientIndevStorageBlocks",
				mod_BetaTweaks.optionsClientIndevStorageBlocks, "ON", "OFF");
		((WidgetBoolean) indevStorageTextures.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Indev Storage Block Textures: (default:OFF)" + "\r\n\r\n"
						+ "Replaces the textures on iron, gold and diamond blocks with their traditional indev counterparts.",
				false));

		SettingBoolean hideLongGrass = guiapiSettings.addSetting(clientSideSettingsBase, "Disable Long Grass",
				"optionsClientHideLongGrass", mod_BetaTweaks.optionsClientHideLongGrass);
		((WidgetBoolean) hideLongGrass.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Disable Long Grass: (default:OFF)" + "\r\n\r\n"
						+ "Hides long grass from the world. Works on vanilla servers though you will notice when you break them.",
				false));

		SettingBoolean hideDeadBush = guiapiSettings.addSetting(clientSideSettingsBase, "Disable Dead Shrubs",
				"optionsClientHideDeadBush", mod_BetaTweaks.optionsClientHideDeadBush);
		((WidgetBoolean) hideDeadBush.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Disable Dead Shrubs: (default:OFF)" + "\r\n\r\n"
						+ "Hides dead shrubs that spawn in the desert. Works on vanilla servers though you will notice when you break them.",
				false));

		betaTweaksMain.append(GuiApiHelper.makeButton("Clientside Settings", "show", GuiModScreen.class, true,
				new Class[] { Widget.class }, new WidgetSimplewindow(clientSideSettingsBase, "Clientside Settings")));

		// GAMEPLAY

		SettingBoolean punchSheep = guiapiSettings.addSetting(gameplaySettingsBase, "Punch Sheep for Wool",
				"optionsGameplayPunchableSheep", mod_BetaTweaks.optionsGameplayPunchableSheep);
		((WidgetBoolean) punchSheep.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Punch Sheep for Wool: (default:true)" + "\r\n\r\n"
						+ "Punching wooly sheep will shear them and provide wool. This was removed in Beta 1.7 in favour of shears.",
				false));

		SettingBoolean ladderGaps = guiapiSettings.addSetting(gameplaySettingsBase, "Allow Gaps in Ladders",
				"optionsGameplayLadderGaps", mod_BetaTweaks.optionsGameplayLadderGaps);
		((WidgetBoolean) ladderGaps.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea(
						"Allow Gaps in Ladders: (default:true)" + "\r\n\r\n"
								+ "You can climb up ladders with 1 block gaps in them. This was removed in Beta 1.5",
						false));

		SettingBoolean punchTNT = guiapiSettings.addSetting(gameplaySettingsBase, "Punch TNT to ignite",
				"optionsGameplayLightTNTwithFist", mod_BetaTweaks.optionsGameplayLightTNTwithFist);
		((WidgetBoolean) punchTNT.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Punch TNT to ignite: (default:true)" + "\r\n\r\n"
						+ "TNT can be primed by punching it. This was removed in Beta 1.7 in favour of the flint & steel or a redstone signal.",
				false));

		SettingBoolean hoeCreatesSeeds = guiapiSettings.addSetting(gameplaySettingsBase, "Hoe Grass for Seeds",
				"optionsGameplayHoeDirtSeeds", mod_BetaTweaks.optionsGameplayHoeDirtSeeds);
		((WidgetBoolean) hoeCreatesSeeds.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Hoe Grass for Seeds: (default:false)" + "\r\n\r\n"
						+ "Seeds can be obtained by tilling grass with a hoe. This was removed in Beta 1.6 in favour of long grass.",
				false));

		SettingBoolean oldMinecartBoosters = guiapiSettings.addSetting(gameplaySettingsBase, "Minecart Boosters",
				"optionsGameplayMinecartBoosters", mod_BetaTweaks.optionsGameplayMinecartBoosters);
		((WidgetBoolean) oldMinecartBoosters.displayWidget).button.setTooltipContent(GuiApiHelper.makeTextArea(
				"Minecart Boosters: (default:true)" + "\r\n\r\n"
						+ "Minecarts can be arranged in a way such that they can accelerate each other. This was removed in Beta 1.5 in favour of powered rails.",
				false));

		SettingBoolean elevatorBoats = guiapiSettings.addSetting(gameplaySettingsBase, "Elevator Boats",
				"optionsGameplayElevatorBoats", mod_BetaTweaks.optionsGameplayElevatorBoats);
		((WidgetBoolean) elevatorBoats.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Elevator Boats: (default:true)" + "\r\n\r\n"
						+ "Submerged boats rise very quickly in water. This was removed in Beta 1.6", false));

		betaTweaksMain.append(GuiApiHelper.makeButton("Gameplay Settings", "show", GuiModScreen.class, true,
				new Class[] { Widget.class }, new WidgetSimplewindow(gameplaySettingsBase, "Gameplay Settings")));
		
		//SERVER SETTINGS
		if (mod_BetaTweaks.modloaderMPinstalled) {
			SettingBoolean playerList = guiapiSettings.addSetting(serverSettingsBase, "Enable Player List",
				"optionsServerAllowPlayerList", BetaTweaksMP.optionsServerAllowPlayerList);
			((WidgetBoolean) playerList.displayWidget).button
				.setTooltipContent(GuiApiHelper.makeTextArea("Enable Player List: (default:true)" + "\r\n\r\n"
						+ "Should players be able to use the player list introduced in Beta 1.8", false));
			
			motd = guiapiSettings.addSetting(serverSettingsBase, "MOTD",
					"optionsServerMOTD", BetaTweaksMP.optionsServerMOTD);
			TextArea motdToolTip = GuiApiHelper.makeTextArea("MOTD: (default:A Minecraft Server)" + "\r\n\r\n"
					+ "The server description displayed in the server browser.", false);
			((WidgetText) motd.displayWidget).displayLabel.setTooltipContent(motdToolTip);
			((WidgetText) motd.displayWidget).editField.setTooltipContent(motdToolTip);
			
			//fixed server setting tooltips
		}
		
		betaTweaksMain.append(GuiApiHelper.makeButton("Server Settings", "show", GuiModScreen.class, true,
				new Class[] { Widget.class }, new WidgetSimplewindow(serverSettingsBase, "Server Settings")));

		// ((WidgetBoolean)elevatorBoats.displayWidget).addCallback(new
		// ModAction(BetaTweaksGuiAPI.class, "checkButtonStatus", "", Setting.class));
		// .setDefaultArguments(panorama));
		// settingCtrlsMenuEnabled.displayWidget.addCallback(new
		// ModAction(InitBetaTweaksGuiAPI.class, "isVanillaCtrlMenuSafe",
		// "BETATWEAKS - Callback for Boolean Scrollable Controls Menu"));
		// optionsGameplayHoeDirtSeeds

		// clientSideSettingsBase.heightOverrideExceptions.put(toolTip, 0);
		// TextArea x = GuiApiHelper.makeTextArea("THING" + "\r\n" + "THING2", false);

		// CUSTOM CON?Fig

		// LOAD

	}

	/*public void checkButtonStatus(Setting setting) {
		if (setting == panorama){
		if ((Boolean) setting.get()) {
			 clientSideSettingsBase.heightOverrideExceptions.put(toolTip, 100);
			 ctrlsMenu.displayWidget.setSize(0, 0);
			 guiapiSettings.Settings.get(3).displayWidget.setEnabled(true);
		} else {
			 guiapiSettings.Settings.get(3).displayWidget.setEnabled(false);
		}
		 }
	}*/

	public void updateSettings() {
		
		if (ModLoader.getMinecraftInstance().theWorld == null || !ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("optionsGameplay"))
					try {
						for (int j = 0; j < guiapiSettings.getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == guiapiSettings.getAllBooleanSettings().get(j).backendName) {
								myFields[i].set(null, guiapiSettings.getAllBooleanSettings().get(j).get());
							}
						}
						for (int j = 0; j < guiapiSettings.getAllMultiSettings().size(); j++) {
							if (myFields[i].getName() == guiapiSettings.getAllMultiSettings().get(j).backendName) {
								if (myFields[i].getType() == LogoState.class) {
									myFields[i].set(null,
											LogoState.values()[guiapiSettings.getAllMultiSettings().get(j).get()]);
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
							for (int j = 0; j < guiapiSettings.getAllBooleanSettings().size(); j++) {
								if (myFields[i]
										.getName() == guiapiSettings.getAllBooleanSettings().get(j).backendName) {
									options2.add(guiapiSettings.getAllBooleanSettings().get(j).get() ? 1 : 0);
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
					for (int j = 0; j < guiapiSettings.getAllBooleanSettings().size(); j++) {
						if (myFieldsClient[i].getName() == guiapiSettings.getAllBooleanSettings().get(j).backendName) {
							myFieldsClient[i].set(null, guiapiSettings.getAllBooleanSettings().get(j).get());
						}
					}
					for (int j = 0; j < guiapiSettings.getAllMultiSettings().size(); j++) {
						if (myFieldsClient[i].getName() == guiapiSettings.getAllMultiSettings().get(j).backendName) {
							if (myFieldsClient[i].getType() == LogoState.class) {
								myFieldsClient[i].set(null,
										LogoState.values()[guiapiSettings.getAllMultiSettings().get(j).get()]);
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
		}

		mod_BetaTweaks.writeConfig();
	}

	public void loadSettings() {

		if (ModLoader.getMinecraftInstance().theWorld == null) {
			gameplaySettingsBase.setEnabled(true);
			betaTweaksMain.widgetColumn.getChild(1).setEnabled(true);
			betaTweaksMain.widgetColumn.getChild(2).setVisible(false);

			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("options")) {
					try {
						for (int j = 0; j < guiapiSettings.getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == guiapiSettings.getAllBooleanSettings().get(j).backendName) {
								guiapiSettings.getAllBooleanSettings().get(j).set((Boolean) myFields[i].get(null));
							}
							//checkButtonStatus(guiapiSettings.getAllBooleanSettings().get(j));
						}
						for (int j = 0; j < guiapiSettings.getAllMultiSettings().size(); j++) {
							if (myFields[i].getName() == guiapiSettings.getAllMultiSettings().get(j).backendName) {
								if (myFields[i].getType() == LogoState.class) {
									guiapiSettings.getAllMultiSettings().get(j)
											.set(LogoState.valueOf(myFields[i].get(null).toString()).ordinal());
								}
							}
							//checkButtonStatus(guiapiSettings.getAllBooleanSettings().get(j));
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}

		else if (ModLoader.getMinecraftInstance().theWorld.multiplayerWorld && mod_BetaTweaks.modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled) { // TODO
			betaTweaksMain.widgetColumn.getChild(1).setEnabled(true);

			if (BetaTweaksMP.isOp) {
				gameplaySettingsBase.setEnabled(true);
				betaTweaksMain.widgetColumn.getChild(2).setVisible(true);
			} else {
				gameplaySettingsBase.setEnabled(false);
				betaTweaksMain.widgetColumn.getChild(2).setVisible(false);
			}

			Field[] myFields = BetaTweaksMP.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("optionsGameplay") || myFields[i].getName().contains("optionsServer")) {
					try {
						for (int j = 0; j < guiapiSettings.getAllBooleanSettings().size(); j++) {
							if (myFields[i].getName() == guiapiSettings.getAllBooleanSettings().get(j).backendName) {
								guiapiSettings.getAllBooleanSettings().get(j).set((Boolean) myFields[i].get(null));
							}
							//checkButtonStatus(guiapiSettings.getAllBooleanSettings().get(j));
						}

					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
			motd.set(BetaTweaksMP.optionsServerMOTD);
		} else if (ModLoader.getMinecraftInstance().theWorld.multiplayerWorld) {
			gameplaySettingsBase.setEnabled(true);
			betaTweaksMain.widgetColumn.getChild(1).setEnabled(false);
			betaTweaksMain.widgetColumn.getChild(2).setVisible(false);
		}
	}

}
