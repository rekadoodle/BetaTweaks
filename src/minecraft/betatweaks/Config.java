package betatweaks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;

import betatweaks.gui.GuiImprovedChat;
import net.minecraft.client.Minecraft;

public class Config {
	public enum LogoState {
		STANDARD, ANIMATED, CUSTOM
	};

	public static boolean clientDraggingShortcuts = true;
	public static LogoState clientLogo = LogoState.STANDARD;
	public static boolean clientPanoramaEnabled = false;
	public static boolean clientQuitGameButton = true;
	public static boolean clientMultiplayerMenu = true;
	public static boolean clientImprovedChat = true;
	public static float clientImprovedChatFontScaleValue = 0.5F;
	public static float clientImprovedChatWidthValue = 0.5F;
	public static float clientImprovedChatIngameHeightOffset = 0.5F;
	public static float clientImprovedChatIngameMaxHeight = 0.5F;
	public static float clientImprovedChatMaxMessagesSize = 0.053F;
	public static int clientImprovedChatIndicator = 0;
	public static boolean clientScrollableControls = true;
	public static boolean clientIngameTexturePackButton = false;
	public static boolean clientDisableAchievementNotifications = false;
	public static boolean clientDisableEntityRendererOverride = false;
	public static boolean clientFovSliderVisible = true;
	public static float clientFovSliderValue = 0F;
	public static float clientFovMultiplier = 1.0F;
	public static boolean clientIndevStorageBlocks = false;
	public static boolean clientHideLongGrass = false;
	public static boolean clientHideDeadBush = false;
	public static String clientCustomFullscreenResolution = "";
	public static boolean clientShowAllResolutionsInConsole = false;

	public static boolean gameplayPunchSheepForWool = true;
	public static boolean gameplayLadderGaps = true;
	public static boolean gameplayLightTNTwithFist = true;
	public static boolean gameplayHoeDirtForSeeds = false;
	public static boolean gameplayMinecartBoosters = true;
	public static boolean gameplayBoatElevators = true;
	
	public final static ArrayList<Field> optionsClient = Utils.getFieldsStartingWith(Config.class, "client");
	public final static ArrayList<Field> optionsGameplay = Utils.getFieldsStartingWith(Config.class, "gameplay");
	public final static ArrayList<Field> options = new ArrayList<Field>();
	
	private static File configFile = new File((Minecraft.getMinecraftDir()) + "/config/BetaTweaks.cfg");

	public static void init() {
		if (!configFile.exists()) writeConfig();
		readConfig();
	}
	
	public static void writeConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for Beta Tweaks");
			for(Field option : options) {
				configWriter.write(System.getProperty("line.separator"));
				configWriter.write(option.getName() + "=" + option.get(null).toString());
			}
			configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void readConfig() {
		try {
			BufferedReader configReader = new BufferedReader(new FileReader(configFile));
			String s;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue;
				} // Ignore comments

				if (s.contains("=")) {
					String as[] = s.split("=");
					Field option = getField(as[0]);
				
					if(option == null) {
						continue;
					} 
					else if (option.getType() == int.class) {
						option.set(null, Integer.parseInt(as[1]));
					} 
					else if (option.getType() == boolean.class) {
						option.set(null, Boolean.parseBoolean(as[1]));
					} 
					else if (option.getType() == LogoState.class) {
						option.set(null, LogoState.valueOf(as[1].toUpperCase()));
					} 
					else if (option.getType() == float.class) {
						option.set(null, Float.parseFloat(as[1]));
					}
					else if (option.getType() == String.class) {
						if(as.length > 1)
						option.set(null, String.valueOf(as[1]));
					}
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static Field getField(String name) {
		for(Field option : options) {
			if(option.getName().equalsIgnoreCase(name)) return option;
		}
		return null;
	}
	
	public static void setFloatValue(Field field, float newValue) {
		try {
			field.set(null, newValue);
			GuiImprovedChat.onChatSettingChanged();
		} 
		catch (Exception e) {e.printStackTrace(); }
	}

	public static float getFloatValue(Field field) {
		try {
			return field.getFloat(null);
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
			return 0.5F;
		}
	}
	
	public static String getSliderText(Field field) {
		if(field.getName().equals("clientImprovedChatFontScaleValue")) {
			return new StringBuffer("Font Scale: ").append(GuiImprovedChat.getFontScaleFactor()).append("x").toString();
		}
		if(field.getName().equals("clientImprovedChatWidthValue")) {
			int value = (int) (Utils.round2dp(clientImprovedChatWidthValue) * 100f);
			return new StringBuffer("Width: ").append(value).append("%").toString();
		}
		if(field.getName().equals("clientFovSliderValue")) {
			if(clientFovSliderValue == 0.0F) {
	    		return "FOV: Normal";
	    	}
	    	if(clientFovSliderValue == 1.0F) {
	    		return "FOV: Quake Pro";
	    	}
			return new StringBuffer("FOV: ").append((int)(70.0F + clientFovSliderValue * 40.0F)).toString();
		}
		if(field.getName().equals("clientImprovedChatIngameHeightOffset")) {
			int value = Math.round((clientImprovedChatIngameHeightOffset - 0.5f) * 200);
			return new StringBuffer("Height Offset: ").append(value).append("px").toString();
		}
		if(field.getName().equals("clientImprovedChatIngameMaxHeight")) {
			int value = Math.round(clientImprovedChatIngameMaxHeight * 100);
			return new StringBuffer("Ingame Max Height: ").append(value).append("%").toString();
		}
		if(field.getName().equals("clientImprovedChatMaxMessagesSize")) {
			int value = 50 + (int) (clientImprovedChatMaxMessagesSize * 950);
			return new StringBuffer("Scrollable Message Limit: ").append(value).toString();
		}
		return null;
	}
	
	static {
		options.addAll(optionsClient);
		options.addAll(optionsGameplay);
	}
}
