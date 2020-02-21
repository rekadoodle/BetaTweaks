package betatweaks.config;

import net.minecraft.src.BetaTweaksMP;

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
		if(setting == cfg.logoStyle) {
			return "Choose 0 for Standard, 1 for Animated, 2 for Custom";
		}
		if(setting == cfg.improvedChatIndicator) {
			return "Choose 0 for Default, 1 for Vertical, 2 for Both";
		}
		return null;
	}

	public static String[] getInfo(SBase<?> setting) {
		if(setting == cfg.draggingShortcuts) {
			return new String[] { 
				"Inventory Dragging Shortcuts",
				"Adds some shortcuts to help spread, collect or transfer",
				"items in the inventory, here are the main ones:",
				"  Hold LMB to collect or spread items evenly.",
				"  Hold RMB to drop 1 item on each slot.",
				"  Hold Shift + LMB to shift click lots of items quickly.",
				"  Press Shift + LMB on a crafting result to craft a stack.",
				"  Press Q to drop 1 of the item held.", "  Hold Q to quickly drop the item hovered."
			};
		}
		if(setting == cfg.logoStyle) {
			return new String[] { 
				"Title Screen Logo",
				"  Standard - The normal Minecraft logo",
				"  Animated - The Beta 1.3 animated logo",
				"  Custom - A custom version of the Beta 1.3 animated logo",
				"Go to: '.minecraft/config/OldCustomLogo.cfg' to configure." 
			};
		}
		if(setting == cfg.mainmenuPanorama) {
			return new String[] {
				"Title Screen Background",
				"  Standard - The classic dirt background",
				"  Panorama - The animated background added in Beta 1.8"
			};
		}
		if(setting == cfg.mainmenuQuitButton) {
			return new String[] {
				"Quit Game Button",
				"Adds a button to quit the game on the Title Screen."
			};
		}
		if(setting == cfg.serverList) {
			return new String[] {
				"Server List",
				"Uses the Beta 1.8 menu which allows multiple servers to",
				"be saved.",
				"You have the option to ping them but servers with DDOS",
				"protection can give you End Of Stream when you try to",
				"join them. To fix this you may have to restart the client",
				"or wait roughly 5-10 minutes."
		    };
		}
		if(setting == cfg.improvedChat) {
			return new String[] {
				"Improved Chat",
				"Adds new QOL features to the chat, such as:",
				"  Edit font size and chatbox size.",
				"  Scroll to view previous chat messages.",
				"  Copy, cut, paste, undo, etc. (Only in typing area).",
				"  Tab to autocomplete playername (If server allows).",
				"  Access previous inputs with up/down.",
				"Click in the top right with chat open or use '/chatoptions'",
				"to configure."
		    };
		}
		if(setting == cfg.scrollableControls) {
			return new String[] {
				"Scrollable Controls",
				"Improves functionality of the controls menu by",
				"introducing a scrollbar and letting you unbind keys with",
				"ESC."
		    };
		}
		if(setting == cfg.ingameTexurePackButton) {
			return new String[] {
				"ESC Menu Texture Pack Button",
				"Adds a button to the ingame menu that lets you change",
		    	"texture packs without going to the main menu."
		    };
		}
		if(setting == cfg.hideAchievementNotifications) {
			return new String[] {
				"Hide Achievement Notifications",
				"Disables the popup notifications that appear when you get",
				"an achievement."
		    };
		}
		if(setting == cfg.fovSlider) {
			return new String[] {
				"FOV Slider",
				"Adds an fov slider to the options menu.",
		       	"",
		       	"Won't work if ClientDisableEntityRendererOverride",
		       	"in config is set to false."
		    };
		}
		if(setting == cfg.indevStorageBlocks) {
			return new String[] {
				"Indev Storage Block Textures",
				"Replaces the textures on iron, gold and diamond blocks",
				"with their traditional indev counterparts.",
				"(You may need to restart for the textures to load.)"
		    };
		}
		if(setting == cfg.hideLongGrass) {
			return new String[] {
				"Disable Long Grass",
				"Hides long grass from the world. Works on vanilla",
				"servers though you will notice when you break them."
		    };
		}
		if(setting == cfg.hideDeadBush) {
			return new String[] {
				"Disable Dead Shrubs",
				"Hides dead shrubs that spawn in the desert. Works on",
				"vanilla servers though you will notice when you break",
				"them."
		    };
		}
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
		if(setting == BetaTweaksMP.playerListAllowed) {
			return new String[] {
				"Enable Player List",
				"Should players be able to use the player list that was",
				"introduced in Beta 1.8"
			};
		}
		if(setting == BetaTweaksMP.motd) {
			return new String[] {
				"MOTD",
				"The server description displayed in the server browser."
			};
		}
		return null;
	}
}
