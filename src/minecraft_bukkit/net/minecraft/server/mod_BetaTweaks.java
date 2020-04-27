package net.minecraft.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.betatweaks.*;

public class mod_BetaTweaks extends BaseModMp{

	@Override
	public String Version() {
		return BetaTweaks.INSTANCE.version();
	}
	
	@Override
	public String toString() {
		return BetaTweaks.INSTANCE.getModID();
	}

	public mod_BetaTweaks(){
		BetaTweaks.INSTANCE.init(this);
		Packet.a(254, false, true, Packet254ServerPing.class);
		
		ModLoader.SetInGameHook(this, true, false);
	}
	
	@Override
	public void OnTickInGame(MinecraftServer mc) {
		BetaTweaks.INSTANCE.onTickInGame(mc);
	}
	
	@Override
	public void HandleLogin(EntityPlayer player) {
		BetaTweaks.INSTANCE.handleLogin(player);
	}
	
	@Override
	public void HandlePacket(Packet230ModLoader packet, EntityPlayer player) {
		BetaTweaks.INSTANCE.handlePacket(packet, player);
	}
	
	public static void setFallDistance(Entity entity, float f) {
		entity.fallDistance = f;
	}
}
