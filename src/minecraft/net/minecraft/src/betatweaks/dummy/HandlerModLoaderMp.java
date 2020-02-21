package net.minecraft.src.betatweaks.dummy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.config.SBoolean;
import net.minecraft.src.betatweaks.config.SString;

public abstract class HandlerModLoaderMp {
	public SBoolean punchSheepForWool = new SBoolean("punchSheepForWool", false);
	public SBoolean ladderGaps = new SBoolean("ladderGaps", false);
	public SBoolean lightTNTwithFist = new SBoolean("lightTNTwithFist", false);
	public SBoolean hoeGrassForSeeds = new SBoolean("hoeGrassForSeeds", false);
	public SBoolean minecartBoosters = new SBoolean("minecartBoosters", false);
	public SBoolean boatElevators = new SBoolean("boatElevators", false);
	
	public SBoolean playerListAllowed = new SBoolean("playerList", false, true);
	public SString motd = new SString("motd", "A Minecraft Server", true);

	public boolean serverModInstalled = false;
	public boolean isOp = false;
	
	public List<String> playerList = new ArrayList<String>();

	public final SBase<?>[] options = new SBase[] {
			punchSheepForWool, ladderGaps, lightTNTwithFist, hoeGrassForSeeds, minecartBoosters, boatElevators, playerListAllowed, motd
	};
	
	public final SBase<?>[] opOptions = new SBase[] {
			playerListAllowed, motd
	};

	public abstract void sheepPunched(int entityid);
	public abstract void grassHoed(int x, int y, int z);
	public abstract void longGrassDestroyed(int x, int y, int z);
	public abstract void updateServerSettings(boolean[] newOptions, String newMOTD);
}
