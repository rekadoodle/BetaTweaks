package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BetaTweaksMP extends BaseModMp {

	public static Boolean optionsGameplayPunchableSheep = false;
	public static Boolean optionsGameplayLadderGaps = false;
	public static Boolean optionsGameplayLightTNTwithFist = false;
	public static Boolean optionsGameplayHoeDirtSeeds = false;
	public static Boolean optionsGameplayMinecartBoosters = false;
	public static Boolean optionsGameplayElevatorBoats = false;
	
	public static Boolean optionsServerAllowPlayerList = false;
	public static String optionsServerMOTD = "A Minecraft Server";
	
	public static Boolean isOp = false;
	
	public static Boolean serverModInstalled = false;
	
	
	public static List<String> playerList = new ArrayList<String>();
	public static int maxPlayers;
	
	
	
	public static List<Integer> options = new ArrayList<Integer>();
	
	public String Version() {
		return "v1";
	}
	
	public String Description() {
		//For mine_diver's mod menu
		return "Handles the multiplayer parts of BetaTweaks";
	}
	
	public String Name() {
		//For mine_diver's mod menu
		return "Beta Tweaks MP Handler";
	}
	
	public void HandlePacket(Packet230ModLoader packet)
	{
		switch(packet.packetType)
		{
			case 0:
			{
				serverModInstalled = true;
				Field[] myFields = BetaTweaksMP.class.getFields();
				for (int i = 0; i < packet.dataInt.length - 1; i++) {
						try {
							myFields[i].set(this, packet.dataInt[i] == 1);
							options.add(packet.dataInt[i]);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					
				}
				maxPlayers = packet.dataInt[packet.dataInt.length - 1];
				optionsServerMOTD = packet.dataString[0];
				if (mod_BetaTweaks.guiAPIinstalled) {
					BetaTweaksGuiAPI.instance.loadSettings();
				}
				break;
			}
			case 1:
			{
				isOp = packet.dataInt[0] == 1;
				BetaTweaksGuiAPI.instance.loadSettings();
				break;
			}
			
			case 2:
			{
				playerList.clear();
				for (int q = 0; q < packet.dataString.length; q++) {
					playerList.add(packet.dataString[q]);
				}
				break;
			}
			
		}
		

	}
	
	public static void updateServerSettings(String newMOTD) {
		int[] dataInt = new int[options.size()];
		for (int i = 0; i < options.size(); i++) {
			dataInt[i] = options.get(i);
		}
		String[] dataString = new String[1];
		
		dataString[0] = newMOTD;
		
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		
		ModLoaderMp.SendPacket(ModLoaderMp.GetModInstance(BetaTweaksMP.class), packet);
	}

	public static void longgrassDestroyed(int x, int y, int z) {
			int[] dataInt = new int[3];
			dataInt[0] = x;
			dataInt[1] = y;
			dataInt[2] = z;
			Packet230ModLoader packet = new Packet230ModLoader();
			packet.packetType = 1;
			packet.dataInt = dataInt;
			
			ModLoaderMp.SendPacket(ModLoaderMp.GetModInstance(BetaTweaksMP.class), packet);
	}
	
	public static void sheepPunched(int sheepID) {
		int[] dataInt = new int[1];
		dataInt[0] = sheepID;
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 2;
		packet.dataInt = dataInt;
		
		ModLoaderMp.SendPacket(ModLoaderMp.GetModInstance(BetaTweaksMP.class), packet);
	}


	public static void grassHoed(int x, int y, int z) {
		int[] dataInt = new int[3];
		dataInt[0] = x;
		dataInt[1] = y;
		dataInt[2] = z;
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 3;
		packet.dataInt = dataInt;
		
		ModLoaderMp.SendPacket(ModLoaderMp.GetModInstance(BetaTweaksMP.class), packet);
	}


	
	

}
