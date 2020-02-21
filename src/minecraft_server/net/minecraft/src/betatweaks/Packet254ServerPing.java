// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src.betatweaks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;

import net.minecraft.src.ModLoader;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet255KickDisconnect;
import net.minecraft.src.ServerConfigurationManager;

// Referenced classes of package net.minecraft.src:
//            Packet, NetHandler

public class Packet254ServerPing extends Packet
{

    public Packet254ServerPing()
    {
    }

    public void readPacketData(DataInputStream datainputstream)
    {
    }

    public void writePacketData(DataOutputStream dataoutputstream)
    {
    }

    public void processPacket(NetHandler nethandler)
    {
    	thing(nethandler);
    }
    
    public static void thing(NetHandler nethandler) {

        try
        {
        	Field x = ServerConfigurationManager.class.getDeclaredFields()[4];
    		x.setAccessible(true);
        	
        	
            String s = (new StringBuilder()).append(BetaTweaksMP.optionsServerMOTD).append("\247").append(ModLoader.getMinecraftServerInstance().configManager.playerEntities.size()).append("\247").append(x.get(ModLoader.getMinecraftServerInstance().configManager)).toString();
            ((NetLoginHandler)nethandler).netManager.addToSendQueue(new Packet255KickDisconnect(s));
        	((NetLoginHandler)nethandler).netManager.serverShutdown();
        	//ModLoader.getMinecraftServerInstance().networkServer.func_35505_a(((NetLoginHandler)nethandler).netManager.func_35596_f());
        	((NetLoginHandler)nethandler).finishedProcessing = true;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    

    public int getPacketSize()
    {
        return 0;
    }
}
