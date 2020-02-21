package betatweaks;

import net.minecraft.src.NBTTagCompound;

public class ServerData
{

    public ServerData(String name, String ip, Boolean shouldPing)
    {
    	this.name = name;
        this.ip = ip;
        this.shouldPing = shouldPing;
    }

    public NBTTagCompound saveToNBT()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", name);
        nbttagcompound.setString("ip", ip);
        nbttagcompound.setBoolean("shouldPing", shouldPing);
        return nbttagcompound;
    }

    public static ServerData loadFromNBT(NBTTagCompound nbttagcompound)
    {
        return new ServerData(nbttagcompound.getString("name"), nbttagcompound.getString("ip"), nbttagcompound.getBoolean("shouldPing"));
    }

    public String name;
    public String ip;
    public String playerCount;
    public String status;
    public boolean shouldPing;
    public long ping;
    public boolean pinged = false;
}
