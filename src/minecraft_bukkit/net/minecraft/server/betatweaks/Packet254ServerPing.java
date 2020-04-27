package net.minecraft.server.betatweaks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.minecraft.server.*;
import net.minecraft.server.betatweaks.config.Config;

public class Packet254ServerPing extends Packet
{

    public void a(DataInputStream datainputstream) { }

    public void a(DataOutputStream dataoutputstream) { }

    public void a(NetHandler nethandler)
    {
    	String s = (new StringBuilder()).append(Config.INSTANCE.serverMOTD.getValue()).append("\247").append(Utils.mc.serverConfigurationManager.players.size()).append("\247").append(Utils.mc.serverConfigurationManager.maxPlayers).toString();
    	((NetLoginHandler)nethandler).networkManager.queue(new Packet255KickDisconnect(s));
    	((NetLoginHandler)nethandler).networkManager.d();
    	//ModLoader.getMinecraftServerInstance().networkServer.func_35505_a(((NetLoginHandler)nethandler).netManager.func_35596_f());
    	((NetLoginHandler)nethandler).c = true;
    }
    

    public int a()
    {
        return 0;
    }
}
