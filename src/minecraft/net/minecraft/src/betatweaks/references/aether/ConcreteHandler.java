package net.minecraft.src.betatweaks.references.aether;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.dummy.HandlerAether;
import net.minecraft.src.betatweaks.gui.GuiServerList;
import paulscode.sound.SoundSystem;

public class ConcreteHandler extends HandlerAether {
	
	private final boolean sheepuffExists;
	int musicID;
	
	public ConcreteHandler() {
		this.sheepuffExists = Utils.classExists("EntitySheepuff");
		try {
			GuiMainMenu.renderOption = mod_Aether.worldMenu;
		}
		catch(NoSuchFieldError e) { }
	}

	@Override
	public void shearSheepuff(Entity entity) {
		if(!this.sheepuffExists) {
			return;
		}
		if(entity instanceof EntitySheepuff) {
			EntitySheepuff sheep = (EntitySheepuff) entity;
			
			if (!Utils.MC.theWorld.multiplayerWorld && sheep.beenAttacked) {
				if (!sheep.getSheared()) {

					sheep.setSheared(true);
					Random rand = new Random();
					int i = 2 + rand.nextInt(3);
					for (int j = 0; j < i; j++) {
						EntityItem wool = sheep.entityDropItem(
								new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
						wool.motionY += rand.nextFloat() * 0.05F;
						wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}
				}
			}
		}
		
		
	}

	@Override
	public boolean simulatedWorldMenu() {
		try {
			return GuiMainMenu.mmactive;
		}
		catch(NoSuchFieldError e) { return false; }
	}

	@Override
	public void registerGuiOverrides(HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>> map) {
		map.put(GuiMultiplayerAether.class, GuiServerList.class);
	}

	@Override
	public void preGuiScreenOverride(GuiScreen guiscreen) {
		if(guiscreen instanceof GuiMultiplayerAether) {
			Utils.EasyField<Boolean> mainMenuField = new Utils.EasyField<Boolean>(GuiMultiplayerAether.class, "mainMenu");
			mainMenuField.set(guiscreen, true);
			musicID = new Utils.EasyField<Integer>(GuiMultiplayerAether.class, "musicID").get(guiscreen);
		}
	}

	@Override
	public void postGuiScreenOverride(GuiScreen guiscreen) {
		if(guiscreen instanceof GuiServerList) {
			GuiServerList serverList = (GuiServerList) guiscreen;
			serverList.aetherMainMenuMusicInt = musicID;
		}
	}

	@Override
	public void onServerListClosed(int musicID) {
		Minecraft mc = Utils.MC;
		mc.theWorld = null;
        mc.thePlayer = null;
        GuiMainMenu.mmactive = false;
        mc.ingameGUI = new GuiIngame(mc);

        try {
            SoundSystem sound = (SoundSystem)ModLoader.getPrivateValue(SoundManager.class, (Object)null, 0);
            sound.stop("sound_" + musicID);
            ModLoader.setPrivateValue(SoundManager.class, mc.sndManager, "i", 6000);
        } catch (Exception var4) {
            if (var4 instanceof NoSuchFieldException) {
                try {
                    ModLoader.setPrivateValue(SoundManager.class, mc.sndManager, "ticksBeforeMusic", 6000);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
            } else {
                var4.printStackTrace();
            }
        }

        GuiMainMenu.loadingWorld = true;
        GuiMainMenu.musicId = -1;
	}

	@Override
	public void displayAetherMultiplayer(GuiScreen guiscreen, int musicId) {
		Utils.MC.displayGuiScreen(new GuiMultiplayerAether(guiscreen, musicId));
		
	}

}
