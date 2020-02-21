package net.minecraft.src.betatweaks;

import net.minecraft.src.NBTTagCompound;

public class ServerData
{
	//Ping types
	public static final int DONT_PING = 0;
	public static final int PING_DIRECT = 1;
	public static final int PING_VIA_JOHNY_MUFFIN = 2;
	
	//Complete connection types
	public static final long ONLINE = 0L;
	public static final long OFFLINE = -1L;
	public static final long POLLING = -2L;
	
	public ServerData(NBTTagCompound nbt) {
    	this(nbt.getString("name"), nbt.getString("ip"), getPingType(nbt), nbt.getString("proxyName"));
	}
	
	public ServerData(ServerData server) {
		this(server.name, server.ip, server.pingType, server.proxyName);
	}
	
	private static int getPingType(NBTTagCompound nbt) {
		int pingType = nbt.getInteger("pingType");
    	if (pingType == 0) {
    		pingType = nbt.getBoolean("shouldPing") ? PING_DIRECT : DONT_PING;
    	}
		return pingType;
	}

	public ServerData(String name, String ip, int pingType)
    {
    	this(name, ip, pingType, null);
    }
	
    public ServerData(String name, String ip, int pingType, String proxyName)
    {
    	this.name = name;
        this.ip = ip;
        this.pingType = pingType;
        this.proxyName = proxyName;
    }

    public NBTTagCompound saveToNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", name);
        nbt.setString("ip", ip);
        nbt.setInteger("pingType", pingType);
        if(proxyName != null) {
            nbt.setString("proxyName", proxyName);
        }
        return nbt;
    }
    
    public void setConnectionFailed(String status) {
    	responseTime = OFFLINE;
    	this.status = new StringBuilder("\u00a74").append(status).toString();
    }

    public String name;
    public String ip;
    public int pingType;
    public String proxyName;
    
    public String playerCount;
    public String status;
    public long responseTime;
    public boolean responded = false;
}
