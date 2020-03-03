package net.minecraft.src.betatweaks;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.config.Config;

public class Packet254ServerPing extends Packet
{

    public void readPacketData(DataInputStream datainputstream) {  }

    public void writePacketData(DataOutputStream dataoutputstream) { }

    public void processPacket(NetHandler nethandler)
    {
    	PlayerManager[] playerManagerObj = new Utils.EasyField<PlayerManager[]>(ServerConfigurationManager.class, "playerManagerObj", "d").get(Utils.mc.configManager);
    	String s = (new StringBuilder()).append(Config.INSTANCE.serverMOTD.getValue()).append("\247").append(Utils.mc.configManager.playerEntities.size()).append("\247").append(playerManagerObj).toString();
        ((NetLoginHandler)nethandler).netManager.addToSendQueue(new Packet255KickDisconnect(s));
    	((NetLoginHandler)nethandler).netManager.serverShutdown();
    	//ModLoader.getMinecraftServerInstance().networkServer.func_35505_a(((NetLoginHandler)nethandler).netManager.func_35596_f());
    	((NetLoginHandler)nethandler).finishedProcessing = true;
    }

    public int getPacketSize()
    {
        return 0;
    }
}
