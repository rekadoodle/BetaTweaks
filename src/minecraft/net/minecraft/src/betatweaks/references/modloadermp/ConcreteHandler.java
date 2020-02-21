package net.minecraft.src.betatweaks.references.modloadermp;

import java.util.LinkedList;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.SBoolean;
import net.minecraft.src.betatweaks.dummy.HandlerModLoaderMp;

public class ConcreteHandler extends HandlerModLoaderMp {

	private BaseModMp packetHandler;
	public int maxPlayers;
	
	@SuppressWarnings("unchecked")
	public ConcreteHandler() {
		packetHandler = new PacketHandler(this);
		try {
			((LinkedList<BaseMod>) Utils.getField(ModLoader.class, "modList").get(null)).add(packetHandler);
		} 
		catch (Exception e) { e.printStackTrace(); } 
	}

	//Outgoing packet IDs
	private final int PACKET_OUT_UPDATE_SETTINGS = 0;
	private final int PACKET_OUT_LONGGRASS_DESTROYED = 1;
	private final int PACKET_OUT_SHEEP_PUNCHED = 2;
	private final int PACKET_OUT_GRASS_HOED = 3;

	@Override
	public void sheepPunched(int entityid) {
		sendIntPacket(PACKET_OUT_SHEEP_PUNCHED, entityid);
	}

	@Override
	public void grassHoed(int x, int y, int z) {
		sendIntPacket(PACKET_OUT_GRASS_HOED, x, y, z);
	}

	@Override
	public void longGrassDestroyed(int x, int y, int z) {
		sendIntPacket(PACKET_OUT_LONGGRASS_DESTROYED, x, y, z);
	}

	@Override
	public void updateServerSettings(boolean[] newOptions, String newMOTD) {
		int[] dataInt = new int[newOptions.length];
		for (int i = 0; i < dataInt.length; i++) {
			dataInt[i] = newOptions[i] ? 1 : 0;
		}
		String[] dataString = new String[] { newMOTD };
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = PACKET_OUT_UPDATE_SETTINGS;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		
		ModLoaderMp.SendPacket(packetHandler, packet);
	}
	
	private void sendIntPacket(int packetID, int ... data) {
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = packetID;
		packet.dataInt = data;
		
		ModLoaderMp.SendPacket(packetHandler, packet);
	}
	
	//Incoming packet IDs
	private final int PACKET_IN_LOGIN = 0;
	private final int PACKET_IN_OPPED_OR_DEOPPED = 1;
	private final int PACKET_IN_PLAYERLIST_CHANGED = 2;
	
	public void HandlePacket(Packet230ModLoader packet) {
		switch(packet.packetType)
		{
			case PACKET_IN_LOGIN:
			{
				serverModInstalled = true;
				for (int i = 0; i < packet.dataInt.length - 1; i++) {
					((SBoolean)options[i]).setValue(packet.dataInt[i] == 1);
				}
				motd.setValue(packet.dataString[0]);
				maxPlayers = packet.dataInt[packet.dataInt.length - 1];
				if(Utils.isInstalled(Utils.guiapihandler))
					Utils.guiapihandler.loadSettings();
				break;
			}
			case PACKET_IN_OPPED_OR_DEOPPED:
			{
				isOp = packet.dataInt[0] == 1;
				if(Utils.isInstalled(Utils.guiapihandler))
					Utils.guiapihandler.loadSettings();
				break;
			}
			
			case PACKET_IN_PLAYERLIST_CHANGED:
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

}
