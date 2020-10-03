package net.minecraft.server.betatweaks.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Config {

	private File configFile;
	public static final Config INSTANCE = new Config();
	
	public static Config getInstance() {
		return INSTANCE;
	}
	
	private Config() {
		try {
			configFile = new File(new File(".").getCanonicalPath(), "/config/BetaTweaks.cfg");
		} 
		catch (IOException e) { e.printStackTrace(); }
		if (configFile != null && configFile.exists()) 
			readConfig();
		writeConfig();
	}
	
	public void writeConfig() {
		EasyWriter writer = null;
		try {
			writer = new EasyWriter(configFile);
			writer.println("#Config file for Beta Tweaks");
			writer.printSettings(options);
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
	
	public SBoolean punchSheepForWool = new SBoolean("punchSheepForWool", false);
	public SBoolean ladderGaps = new SBoolean("ladderGaps", true);
	public SBoolean lightTNTwithFist = new SBoolean("lightTNTwithFist", false);
	public SBoolean hoeGrassForSeeds = new SBoolean("hoeDirtSeeds", false);
	public SBoolean minecartBoosters = new SBoolean("minecartBoosters", false);
	public SBoolean boatElevators = new SBoolean("elevatorBoats", false);
	
	public SBoolean allowPlayerList = new SBoolean("allowPlayerList", true);
	public SString serverMOTD = new SString("serverMOTD", "A Minecraft Server");
	public SBoolean disableBukkitChatSpacing = new SBoolean("disableBukkitChatSpacing", true);
	
	public final SBase<?>[] options = {
			punchSheepForWool, ladderGaps, lightTNTwithFist, hoeGrassForSeeds, minecartBoosters, boatElevators,
			allowPlayerList, serverMOTD, disableBukkitChatSpacing
	};
}
