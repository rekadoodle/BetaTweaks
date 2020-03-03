package net.minecraft.src.betatweaks.config;

public class SettingInfo {
	
	private static final Config cfg = Config.getInstance();
	
	public static String getNiceName(SBase<?> setting) {
		return getInfo(setting)[0];
	}
	
	public static String[] getTooltip(SBase<?> setting) {
		String[] tooltip = getInfo(setting);
		tooltip[0] += " - (default : " + setting.defaultValue() + ")";
		return tooltip;
	}
	
	public static String getConfigComment(SBase<?> setting) {
		return null;
	}

	public static String[] getInfo(SBase<?> setting) {
		
		if(setting == cfg.punchSheepForWool) {
			return new String[] {
				"Punch Sheep for Wool",
				"Punching wooly sheep will shear them and provide wool.",
				"This was removed in Beta 1.7 in favour of shears."
		    };
		}
		if(setting == cfg.ladderGaps) {
			return new String[] {
				"Allow Gaps in Ladders",
				"You can climb up ladders with 1 block gaps in them.",
				"This was removed in Beta 1.5"
		    };
		}
		if(setting == cfg.lightTNTwithFist) {
			return new String[] {
				"Punch TNT to ignite",
				"TNT can be primed by punching it. This was removed",
				"in Beta 1.7 in favour of the flint & steel or a",
				"redstone signal."
		    };
		}
		if(setting == cfg.hoeGrassForSeeds) {
			return new String[] {
				"Hoe Grass for Seeds",
				"Seeds can be obtained by tilling grass with a hoe.",
				"This was removed in Beta 1.6 in favour of long grass."
		    };
		}
		if(setting == cfg.minecartBoosters) {
			return new String[] {
				"Minecart Boosters",
				"Minecarts can be arranged in a way such that they can",
				"accelerate each other. This was removed in Beta 1.5",
				"in favour of powered rails."
		    };
		}
		if(setting == cfg.boatElevators) {
			return new String[] {
				"Elevator Boats",
				"Submerged boats rise very quickly in water.",
				"This was removed in Beta 1.6"
		    };
		}
		return null;
	}
}
