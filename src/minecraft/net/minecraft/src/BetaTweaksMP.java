package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import betatweaks.GuiAPIHandler;
import betatweaks.Utils;

public class BetaTweaksMP extends BaseModMp {

	public static boolean gameplayPunchableSheep = false;
	public static boolean gameplayLadderGaps = false;
	public static boolean gameplayLightTNTwithFist = false;
	public static boolean gameplayHoeDirtSeeds = false;
	public static boolean gameplayMinecartBoosters = false;
	public static boolean gameplayElevatorBoats = false;
	public static boolean gameplayAllowPlayerList = false;
	
	public static String motd = "A Minecraft Server";
	
	public static boolean isOp = false;
	public static boolean serverModInstalled = false;
	
	public static List<String> playerList = new ArrayList<String>();
	public static int maxPlayers;
	
	public static List<Integer> options1 = new ArrayList<Integer>();
	
	public static final ArrayList<Field> options = Utils.getFieldsStartingWith(BetaTweaksMP.class, "gameplay");
	
	public String Version() {
		return "v1";
	}
	
	//Info for mine_diver's mod menu
	public String Description() {
		return "Handles the multiplayer parts of BetaTweaks";
	}
	
	public String Name() {
		return "Beta Tweaks MP Handler";
	}
	
	public String Icon() {
		return mod_BetaTweaks.resources + "/modMenu2";
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
							options1.add(packet.dataInt[i]);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					
				}
				maxPlayers = packet.dataInt[packet.dataInt.length - 1];
				motd = packet.dataString[0];
				if (mod_BetaTweaks.guiAPIinstalled) {
					GuiAPIHandler.instance.loadSettings();
				}
				break;
			}
			case 1:
			{
				isOp = packet.dataInt[0] == 1;
				if (mod_BetaTweaks.guiAPIinstalled) {
					GuiAPIHandler.instance.loadSettings();
				}
				break;
			}
			
			case 2:
			{
				outerLoop:
				for(int i = 0; i < playerList.size(); i++) {
					for (int q = 0; q < packet.dataString.length; q++) {
						if(playerList.get(i).equals(packet.dataString[q])) {
							continue outerLoop;
						}
					}
					playerList.remove(i);
					i--;
				}
				for (int q = 0; q < packet.dataString.length; q++) {
					if(!playerList.contains(packet.dataString[q]))
					playerList.add(packet.dataString[q]);
				}
				break;
			}
			
		}
		

	}
	
	public static void updateServerSettings(String newMOTD) {
		int[] dataInt = new int[options1.size()];
		for (int i = 0; i < options1.size(); i++) {
			dataInt[i] = options1.get(i);
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
