// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;


// Referenced classes of package net.minecraft.src:
//            NBTTagCompound

public class ServerData
{

    public ServerData(String name, String ip)
    {
    	pinged = false;
        this.name = name;
        this.ip = ip;
    }

    public NBTTagCompound func_35789_a()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", name);
        nbttagcompound.setString("ip", ip);
        return nbttagcompound;
    }

    public static ServerData func_35788_a(NBTTagCompound nbttagcompound)
    {
        return new ServerData(nbttagcompound.getString("name"), nbttagcompound.getString("ip"));
    }

    public String name;
    public String ip;
    public String playerCount;
    public String status;
    public long ping;
    public boolean pinged;
}
