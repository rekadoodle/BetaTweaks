package net.minecraft.src.betatweaks.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.src.betatweaks.Utils;

public class Config {

	private final File configFile = new File((Minecraft.getMinecraftDir()) + "/config/BetaTweaks.cfg");
	private static Config instance;
	
	public static Config getInstance() {
		return instance;
	}
	
	public Config() {
		if(instance == null) instance = this;
		if (configFile.exists()) 
			readConfig();
		writeConfig();
	}
	
	public void writeConfig() {
		EasyWriter writer = null;
		try {
			writer = new EasyWriter(configFile);
			writer.println("#Config file for Beta Tweaks");
			writer.println();
			writer.println("#Clientside Settings");
			writer.printSettings(optionsClient);
			writer.println();
			writer.println("#Singleplayer Only Settings");
			writer.printSettings(optionsGameplay);
		} 
		catch (IOException e) { e.printStackTrace(); }
		finally {
			if(writer != null) {
				writer.close();
			}
		}
	}

	public void readConfig() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(configFile));
			String line;
			while ((line = reader.readLine()) != null) {
				//Delete comments from input
				if (line.contains("#")) {
					line = line.substring(0, line.indexOf('#'));
				}

				if (line.contains("=")) {
					String as[] = line.split("=");
					SBase<?> setting = getSetting(as[0].trim());
					if(as.length > 1) {
						if(setting != null) {
							setting.setValue(as[1].trim());
						}
						else {
							OldConfigConverter.tryParse(as[0].trim(), as[1].trim());
						}
					}
				}
			}
		} 
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try {
				reader.close();
			} 
			catch (Exception e) { }
		}
	}
	
	public SBase<?> getSetting(String name){
		for(SBase<?> setting : options) {
			if(setting.name.equalsIgnoreCase(name)) {
				return setting;
			}
		}
		return null;
	}
	
	public void setSliderValue(SBase<?> setting, float sliderValue) {
		if(setting == improvedChatFontScale) {
			if(sliderValue < 0.5F) {
				improvedChatFontScale.setValue(Utils.round2dp(2f * sliderValue));
			}
			else {
				improvedChatFontScale.setValue(Utils.round1dp(8f * sliderValue - 3f));
			}
		}
		else if(setting == improvedChatIngameHeightOffset) {
			improvedChatIngameHeightOffset.setValue(Math.round((sliderValue - 0.5f) * 200));
		}
		else if(setting == improvedChatHorizontalGap) {
			improvedChatHorizontalGap.setValue(Math.round((sliderValue * 20)));
		}
		else if(setting == improvedChatIngameHeightPercentage) {
			improvedChatIngameHeightPercentage.setValue((sliderValue * 11F) / 10F);
		}
		else if(setting == improvedChatMaxScrollableMessages) {
			improvedChatMaxScrollableMessages.setValue(50 + Math.round(sliderValue * 950));
		}
		else if(setting == fov) {
			fov.setValue(70.0F + sliderValue * 40.0F);
		}
		else {
			((SFloat)setting).setValue(sliderValue);
		}
	}
	
	public float getSliderValue(SBase<?> setting) {
		if(setting == improvedChatFontScale) {
			float value = (Float) setting.getValue();
			if(value >= 1.0f) {
				return (3f + value) / 8f;
			}
			return value / 2f;
		}
		if(setting == improvedChatIngameHeightOffset) {
			return ((Integer)setting.getValue() + 100) / 200F;
		}
		if(setting == improvedChatHorizontalGap) {
			return (Integer)setting.getValue() / 20F;
		}
		if(setting == improvedChatIngameHeightPercentage) {
			return ((Float)setting.getValue() * 10F) / 11F;
		}
		if(setting == improvedChatMaxScrollableMessages) {
			return ((Integer)setting.getValue() - 50) / 950F;
		}
		if(setting == fov) {
			return ((Float) setting.getValue() - 70F) / 40F;
		}
		return ((Float)setting.getValue());
	}
	
	public String getSliderText(SBase<?> setting) {
		if(setting == improvedChatFontScale) {
			return new StringBuffer("Font Scale: ").append(setting.getValue()).append("x").toString();
		}
		if(setting == improvedChatWidthPercentage) {
			return new StringBuffer("Width: ").append(Utils.toPercentage(setting.getValue())).append("%").toString();
		}
		if(setting == improvedChatIngameHeightPercentage) {
			return new StringBuffer("Ingame Max Height: ").append(Utils.toPercentage(setting.getValue())).append("%").toString();
		}
		if(setting == improvedChatIngameHeightOffset) {
			if((Integer)setting.getValue() == 0) {
				return "Height Offset: Default";
			}
			return new StringBuffer("Height Offset: ").append(setting.getValue()).append("px").toString();
		}
		if(setting == improvedChatHorizontalGap) {
			if((Integer)setting.getValue() == 2) {
				return "Horizontal Gap: Default";
			}
			return new StringBuffer("Horizontal Gap: ").append(setting.getValue()).append("px").toString();
		}
		if(setting == improvedChatMaxScrollableMessages) {
			return new StringBuffer("Scrollable Message Limit: ").append(setting.getValue()).toString();
		}
		if(setting == fov) {
			int value = Math.round(instance.fov.getValue());
			if(value == 70) {
	    		return "FOV: Normal";
	    	}
	    	if(value == 110) {
	    		return "FOV: Quake Pro";
	    	}
			return new StringBuffer("FOV: ").append(value).toString();
		}
		return null;
	}
	
	public SBoolean draggingShortcuts = new SBoolean("draggingShortcuts", true, true);
	public SOrdinal logoStyle = new SOrdinal("logoStyle", true, "Standard", "Animated", "Custom");
	public SBoolean mainmenuPanorama = new SBoolean("mainmenuPanorama", false, true, "Panorama", "Standard");
	public SBoolean mainmenuQuitButton = new SBoolean("mainmenuQuitButton", true, true);
	public SBoolean serverList = new SBoolean("serverList", true, true);
	public SBoolean improvedChat = new SBoolean("improvedChat", true, true);
	public SFloat improvedChatFontScale = new SFloat("improvedChatFontScale", 1.0F);
	public SFloat improvedChatWidthPercentage = new SFloat("improvedChatWidthPercentage", 0.5F);
	public SInteger improvedChatIngameHeightOffset = new SInteger("improvedChatIngameHeightOffset", 0);
	public SInteger improvedChatHorizontalGap = new SInteger("improvedChatHorizontalGap", 2);
	public SFloat improvedChatIngameHeightPercentage = new SFloat("improvedChatIngameHeightPercentage", 0.5F);
	public SInteger improvedChatMaxScrollableMessages = new SInteger("improvedChatMaxScrollableMessages", 100);
	public SOrdinal improvedChatIndicator = new SOrdinal("improvedChatIndicator", "Vanilla", "Vertical", "Both");
	public SBoolean improvedChatInvisibleToggleButton = new SBoolean("improvedChatInvisibleToggleButton", true);
	public SBoolean scrollableControls = new SBoolean("scrollableControls", true, true);
	public SBoolean ingameTexurePackButton = new SBoolean("ingameTexurePackButton", false, true);
	public SBoolean hideAchievementNotifications = new SBoolean("hideAchievementNotifications", false, true);
	public SBoolean disableEntityRendererOverride = new SBoolean("disableEntityRendererOverride", false);
	public SBoolean fovSlider = new SBoolean("fovSlider", true, true);
	public SFloat fov = new SFloat("fov", 70F);
	public SBoolean indevStorageBlocks = new SBoolean("indevStorageBlocks", false, true);
	public SBoolean hideLongGrass = new SBoolean("hideLongGrass", false, true);
	public SBoolean hideDeadBush = new SBoolean("hideDeadBush", false, true);
	public SString customFullscreenRes = new SString("customFullscreenRes", "");
	
	public SBoolean modloadermp = new SBoolean("enableModLoaderMpCompatibility", true);
	public SBoolean guiapi = new SBoolean("enableGuiAPICompatibility", true);
	public SBoolean forge = new SBoolean("enableForgeCompatibility", true);
	public SBoolean shaders = new SBoolean("enableShadersCompatibility", true);
	public SBoolean optifine = new SBoolean("enableOptifineCompatibility", true);
	public SBoolean json = new SBoolean("enableJSONCompatibility", true);
	public SBoolean hmi = new SBoolean("enableHowManyItemsCompatibility", true);
	public SBoolean minecolony = new SBoolean("enableMineColonyCompatibility", true);
	public SBoolean aether = new SBoolean("enableAetherCompatibility", true);
	public SBoolean oapi = new SBoolean("enableOAPICompatibility", true);
	
	public SBoolean punchSheepForWool = new SBoolean("punchSheepForWool", true, true);
	public SBoolean ladderGaps = new SBoolean("ladderGaps", true, true);
	public SBoolean lightTNTwithFist = new SBoolean("lightTNTwithFist", true, true);
	public SBoolean hoeGrassForSeeds = new SBoolean("hoeGrassForSeeds", true, true);
	public SBoolean minecartBoosters = new SBoolean("minecartBoosters", true, true);
	public SBoolean boatElevators = new SBoolean("boatElevators", true, true);
	
	public final SBase<?>[] optionsClient = {
			draggingShortcuts, logoStyle, mainmenuPanorama, mainmenuQuitButton, serverList, improvedChat, improvedChatFontScale,
			improvedChatWidthPercentage, improvedChatIngameHeightOffset, improvedChatHorizontalGap, improvedChatIngameHeightPercentage,
			improvedChatMaxScrollableMessages, improvedChatIndicator, improvedChatInvisibleToggleButton, scrollableControls, ingameTexurePackButton,
			hideAchievementNotifications, disableEntityRendererOverride, fovSlider, fov, indevStorageBlocks, hideLongGrass, hideDeadBush,
			customFullscreenRes,
			modloadermp, guiapi, forge, shaders, optifine, json, hmi, minecolony, aether, oapi
	};
	
	public final SBase<?>[] optionsGameplay = {
			punchSheepForWool, ladderGaps, lightTNTwithFist, hoeGrassForSeeds, minecartBoosters, boatElevators
	};
	
	public final SBase<?>[] options = Utils.mergeSettingArrays(optionsClient, optionsGameplay);
}
