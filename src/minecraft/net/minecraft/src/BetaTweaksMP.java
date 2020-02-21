package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import betatweaks.GuiAPIHandler;
import betatweaks.Utils;
import betatweaks.config.*;

public class BetaTweaksMP extends BaseModMp {

	public static SBoolean punchSheepForWool = new SBoolean("punchSheepForWool", false);
	public static SBoolean ladderGaps = new SBoolean("ladderGaps", false);
	public static SBoolean lightTNTwithFist = new SBoolean("lightTNTwithFist", false);
	public static SBoolean hoeGrassForSeeds = new SBoolean("hoeGrassForSeeds", false);
	public static SBoolean minecartBoosters = new SBoolean("minecartBoosters", false);
	public static SBoolean boatElevators = new SBoolean("boatElevators", false);
	
	public static SBoolean playerListAllowed = new SBoolean("playerList", false, true);
	public static SString motd = new SString("motd", "A Minecraft Server", true);
	
	
	public static boolean isOp = false;
	public static boolean serverModInstalled = false;
	
	public static List<String> playerList = new ArrayList<String>();
	public static int maxPlayers;
	
	public static final SBase<?>[] options = new SBase[] {
			punchSheepForWool, ladderGaps, lightTNTwithFist, hoeGrassForSeeds, minecartBoosters, boatElevators, playerListAllowed, motd
	};
	
	public static final SBase<?>[] opOptions = new SBase[] {
			playerListAllowed, motd
	};
	
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
				for (int i = 0; i < packet.dataInt.length - 1; i++) {
					((SBoolean)options[i]).setValue(packet.dataInt[i] == 1);
				}
				motd.setValue(packet.dataString[0]);
				maxPlayers = packet.dataInt[packet.dataInt.length - 1];
				if (Utils.modInstalled("guiapi")) {
					GuiAPIHandler.instance.loadSettings();
				}
				break;
			}
			case 1:
			{
				isOp = packet.dataInt[0] == 1;
				if (Utils.modInstalled("guiapi")) {
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
	
	public static void updateServerSettings(boolean[] newOptions, String newMOTD) {
		int[] dataInt = new int[newOptions.length];
		for (int i = 0; i < dataInt.length; i++) {
			dataInt[i] = newOptions[i] ? 1 : 0;
		}
		String[] dataString = new String[] { newMOTD };
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		
		ModLoaderMp.SendPacket(getThis(), packet);
	}

	public static void longgrassDestroyed(int x, int y, int z) {
			int[] dataInt = new int[3];
			dataInt[0] = x;
			dataInt[1] = y;
			dataInt[2] = z;
			Packet230ModLoader packet = new Packet230ModLoader();
			packet.packetType = 1;
			packet.dataInt = dataInt;
			
			ModLoaderMp.SendPacket(getThis(), packet);
	}
	
	public static void sheepPunched(int sheepID) {
		int[] dataInt = new int[1];
		dataInt[0] = sheepID;
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 2;
		packet.dataInt = dataInt;
		
		ModLoaderMp.SendPacket(getThis(), packet);
	}


	public static void grassHoed(int x, int y, int z) {
		int[] dataInt = new int[3];
		dataInt[0] = x;
		dataInt[1] = y;
		dataInt[2] = z;
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 3;
		packet.dataInt = dataInt;
		
		ModLoaderMp.SendPacket(getThis(), packet);
	}

	private static BaseModMp getThis() {
		return ModLoaderMp.GetModInstance(BetaTweaksMP.class);
	}
	
	

}
