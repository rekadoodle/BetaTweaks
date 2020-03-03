package net.minecraft.src.betatweaks.references.modloadermp;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseModMp;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.mod_BetaTweaks;
import net.minecraft.src.betatweaks.Utils;

public class PacketHandler extends BaseModMp {
	
	private KeyBinding playerListKey = new KeyBinding("List Players", Keyboard.KEY_TAB);
	private ConcreteHandler handler;
	
	public PacketHandler(ConcreteHandler handler) {
		this.handler = handler;
		ModLoader.RegisterKey(this, playerListKey, false);
		ModLoader.SetInGameHook(this, true, false);
	}
	
	//used to match the mod id of the server mod
	public String toString() {
		return "BetaTweaksMP " + this.Version();
    }
	
	public String Version() {
		return "v1.28 PRE";
	}

	//Info for mine_diver's mod menu
	public String Description() {
		return "Handles BetaTweaks Packets";
	}
	
	public String Name() {
		return "Beta Tweaks MP Handler";
	}
	
	public String Icon() {
		return Utils.getResource("modMenu2.png");
	}
	
	public void HandlePacket(Packet230ModLoader packet) {
		handler.HandlePacket(packet);
	}
	
	public boolean OnTickInGame(Minecraft mc) {
		if (handler.serverModInstalled && handler.playerListAllowed.isEnabled()
				&& Keyboard.isKeyDown(playerListKey.keyCode) && Utils.mc.currentScreen == null) {
			int kx = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight).getScaledWidth();
			int j3 = handler.maxPlayers;
			int i4 = j3;
			int k4 = 1;
			for (; i4 > 20; i4 = ((j3 + k4) - 1) / k4) {
				k4++;
			}

			int k5 = 300 / k4;
			if (k5 > 150) {
				k5 = 150;
			}
			int j6 = (kx - k4 * k5) / 2;
			byte byte2 = 10;
			mod_BetaTweaks.drawRect(j6 - 1, byte2 - 1, j6 + k5 * k4, byte2 + 9 * i4, 0x80000000);
			for (int k7 = 0; k7 < j3; k7++) {
				int i8 = j6 + (k7 % k4) * k5;
				int l8 = byte2 + (k7 / k4) * 9;
				mod_BetaTweaks.drawRect(i8, l8, (i8 + k5) - 1, l8 + 8, 0x20ffffff);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3008 /* GL_ALPHA_TEST */);
				if (k7 >= handler.playerList.size()) {
					continue;
				}
				Utils.mc.fontRenderer.drawStringWithShadow(handler.playerList.get(k7), i8, l8, 0xffffff);
			}
		}
		return true;
	}
}
