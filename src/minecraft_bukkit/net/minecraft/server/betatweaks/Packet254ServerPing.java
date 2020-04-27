// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.server.betatweaks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;

import net.minecraft.server.ModLoader;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet255KickDisconnect;

// Referenced classes of package net.minecraft.src:
//            Packet, NetHandler

public class Packet254ServerPing extends Packet
{

    public Packet254ServerPing()
    {
    }

    public void a(DataInputStream datainputstream)
    {
    }

    public void a(DataOutputStream dataoutputstream)
    {
    }

    public void a(NetHandler nethandler)
    {
    	
        try
        {
        	 String s = (new StringBuilder()).append(BetaTweaksMP.optionsServerMOTD).append("\247").append(ModLoader.getMinecraftServerInstance().serverConfigurationManager.players.size()).append("\247").append(ModLoader.getMinecraftServerInstance().serverConfigurationManager.maxPlayers).toString();
            ((NetLoginHandler)nethandler).networkManager.queue(new Packet255KickDisconnect(s));
        	((NetLoginHandler)nethandler).networkManager.d();
        	//ModLoader.getMinecraftServerInstance().networkServer.func_35505_a(((NetLoginHandler)nethandler).netManager.func_35596_f());
        	((NetLoginHandler)nethandler).c = true;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }
    

    public int a()
    {
        return 0;
    }
}
