package net.minecraft.src.betatweaks;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.block.*;
import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.gui.*;

public class BetaTweaks {
	
	public static final BetaTweaks INSTANCE = new BetaTweaks();
	
	final Config cfg = new Config();
	private int guiOptionsButtonCount;
	public static KeyBinding zoom = new KeyBinding("Zoom", Keyboard.KEY_LCONTROL);
	
	private int buttonCount = -1;
	private int buttonCount2 = -1;
	private GuiButton texturePackButton;
	private TexturePackBase initialTexturePack;
	private boolean overrideIngameChat = false;
	
	private World currentWorld = null;
	private HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>> guiOverrides = new HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>>();
	public static boolean dontOverride = false;
	
	public void modsLoaded() {
		References.onModsLoaded();
	}
	
	public void init(mod_BetaTweaks basemod) {
		References.init();
		initSettings();
		
		ModLoader.SetInGameHook(basemod, true, false);
		ModLoader.SetInGUIHook(basemod, true, false);
		
		if(!cfg.disableEntityRendererOverride.isEnabled()) {
			if(!References.isInstalled(References.optifineHandler)) ModLoader.RegisterKey(basemod, zoom, false);
			Utils.MC.entityRenderer = new EntityRendererProxyFOV();
		}
		try {
			guiOptionsButtonCount = new Utils.EasyField<EnumOptions[]>(GuiOptions.class, "field_22135_k", "l").get().length;
		}
		catch(NullPointerException e) { guiOptionsButtonCount = 5; }
		
		CustomFullscreenRes.set(cfg.customFullscreenRes.getValue());
		ModLoader.RegisterKey(basemod, CustomFullscreenRes.toggleKeybind, false);
	}
	
	/**
	 * Applies loaded settings to various features
	 */
	public void initSettings() {
		overrideIngameChat = true;
		GuiMainMenuCustom.resetLogo = true;
		Utils.MC.hideQuitButton = !cfg.mainmenuQuitButton.isEnabled();

		if (cfg.lightTNTwithFist.isEnabled() && Block.tnt.getClass() == BlockTNT.class) new BlockTNTPunchable();
		if (cfg.indevStorageBlocks.isEnabled() && Block.blockSteel.getClass() == BlockOreStorage.class) BlockOreStorageIndev.init();
		if (cfg.hideLongGrass.isEnabled() || Block.tallGrass.getClass() != BlockTallGrass.class) HideBlocks.setLongGrassVisible(!cfg.hideLongGrass.isEnabled());
		if (cfg.hideDeadBush.isEnabled() || Block.deadBush.getClass() != BlockDeadBush.class) HideBlocks.setDeadBushVisible(!cfg.hideDeadBush.isEnabled());
		GuiAchievementNull.setVisible(!cfg.hideAchievementNotifications.isEnabled());
		
		guiOverrides.clear();
		if(cfg.improvedChat.isEnabled()) guiOverrides.put(GuiChat.class, GuiImprovedChat.class);
		if(cfg.scrollableControls.isEnabled()) guiOverrides.put(GuiControls.class, GuiControlsScrollable.class);
		if(cfg.serverList.isEnabled()) guiOverrides.put(GuiMultiplayer.class, GuiServerList.class);
		
		if (cfg.logoStyle.getValue() != 0 || cfg.mainmenuPanorama.isEnabled()) {
			guiOverrides.put(GuiMainMenu.class, GuiMainMenuCustom.class);
		}
		else {
			guiOverrides.put(GuiMainMenuCustom.class, GuiMainMenu.class);
		}
	}
	
	int key;
	
	public void onTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		
		@SuppressWarnings("unchecked")
		List<GuiButton> controlList = (List<GuiButton>) mod_BetaTweaks.controlList(guiscreen);
		if(overrideIngameChat || guiscreen instanceof GuiConnecting) {
			if(cfg.improvedChat.isEnabled()) {
				if(!(mc.ingameGUI instanceof GuiIngameImprovedChat)) {
					mc.ingameGUI = new GuiIngameImprovedChat(mc);
				}
			}
			else {
				if(mc.ingameGUI instanceof GuiIngameImprovedChat) {
					mc.ingameGUI = new GuiIngame(mc);
				}
			}
			overrideIngameChat = false;
		}
		
		
		if (References.isInstalled(References.guiapihandler)) {
			References.guiapihandler.handleTooltip(guiscreen);
		} 
		else if (cfg.ingameTexurePackButton.isEnabled() && guiscreen instanceof GuiIngameMenu) {
			if(buttonCount == -1 || controlList.size() == buttonCount) {
				buttonCount = controlList.size();
				texturePackButton = new GuiButton(137, guiscreen.width / 2 - 100, guiscreen.height / 4 + 72 + (byte)-16, "Mods and Texture Packs");
				texturePackButton.drawButton(mc, Utils.cursorX(), Utils.cursorY());
				controlList.add(texturePackButton);
			}
			if(Utils.buttonClicked(texturePackButton)) {
				mc.displayGuiScreen(new GuiTexturePacks(guiscreen));
			}
		} 
		else if (guiscreen instanceof GuiOptions && !cfg.disableEntityRendererOverride.isEnabled()) {
			if(cfg.fovSlider.isEnabled() && (buttonCount2 == -1 || controlList.size() == buttonCount2)) {
				buttonCount2 = controlList.size();
				controlList.add(new GuiSliderBT(guiscreen.width / 2 - 155 + guiOptionsButtonCount % 2 * 160, guiscreen.height / 6 + 24 * (guiOptionsButtonCount >> 1), cfg.fov));
				((GuiButton)controlList.get(buttonCount2)).drawButton(mc, Utils.cursorX(), Utils.cursorY());
			}
			
			
		}
		if(guiscreen != Utils.getParentScreen()) {
			this.onGuiScreenChanged(mc, guiscreen);
		}
		if (mc.theWorld != currentWorld) {
			if (References.isInstalled(References.mpHandler) && mc.theWorld == null) {
				References.mpHandler.serverModInstalled = false;
			}
			if (References.isInstalled(References.guiapihandler)
					&& (currentWorld == null || !currentWorld.multiplayerWorld) != (mc.theWorld == null
							|| !mc.theWorld.multiplayerWorld)) {
				References.guiapihandler.loadSettingsToGUI();
			}
			currentWorld = mc.theWorld;
		}
		
		CustomFullscreenRes.onGuiTick(guiscreen);
		
		if (cfg.draggingShortcuts.isEnabled()) {
			DraggingShortcuts.onGuiTick(mc, guiscreen);
		}
	}
	
	public void onGuiScreenChanged(Minecraft mc, GuiScreen guiscreen) {
		if(guiOverrides.containsKey(guiscreen.getClass()) && !dontOverride) {
			guiscreen = Utils.overrideCurrentScreen(guiOverrides.get(guiscreen.getClass()));
		}
		if (guiscreen instanceof GuiTexturePacks) {
			initialTexturePack = Utils.MC.texturePackList.selectedTexturePack;
		}
		if (References.isInstalled(References.guiapihandler)) {
			References.guiapihandler.onGuiScreenChanged(guiscreen);
		}
		Utils.setParentScreen(guiscreen);
	}
	
	public void onTickInGame(Minecraft mc) {

		//TODO
		//SCROLL TEST
		//if(Keyboard.isKeyDown(Keyboard.KEY_K)) {
		//	minecraft.ingameGUI.addChatMessage("§4message" + debug++);
		//}
		
		//Clear button override 'memory'
		if((buttonCount != -1 || buttonCount2 != -1) && !(mc.currentScreen instanceof GuiIngameMenu || mc.currentScreen instanceof GuiOptions)) {
			buttonCount = buttonCount2 = -1;
		}
		
		//Reload world if texture pack changed in game
		if(initialTexturePack != null && !(mc.currentScreen instanceof GuiTexturePacks)) {
			if(initialTexturePack != Utils.MC.texturePackList.selectedTexturePack && mc.theWorld != null) {
				mc.renderGlobal.loadRenderers();
			}	
			initialTexturePack = null;
		}
		
		//Equip armour from hotbar
		if(cfg.draggingShortcuts.isEnabled()) {
			DraggingShortcuts.onTick(mc);
		}
		boolean spWorld = !mc.theWorld.multiplayerWorld;
		boolean serverModEnabled = !spWorld && References.isInstalled(References.mpHandler) && References.mpHandler.serverModInstalled;

		if ((spWorld && cfg.ladderGaps.isEnabled()) || (serverModEnabled && References.mpHandler.ladderGaps.isEnabled())) {
			LadderGaps.onTick(mc);
		}
		if ((spWorld && cfg.punchSheepForWool.isEnabled()) || (serverModEnabled && References.mpHandler.punchSheepForWool.isEnabled())) {
			PunchSheepForWool.onTick(mc, serverModEnabled);
		}
		if (spWorld && cfg.minecartBoosters.isEnabled()) {
			MinecartBoosters.onTick(mc);
		}
		if (spWorld && cfg.boatElevators.isEnabled()) {
			BoatElevators.onTick(mc);
		}
		if ((spWorld && cfg.hoeGrassForSeeds.isEnabled()) || (serverModEnabled && References.mpHandler.hoeGrassForSeeds.isEnabled())) {
			HoeGrassForSeeds.onTick(mc, serverModEnabled);
		}
		if ((cfg.hideLongGrass.isEnabled() || cfg.hideDeadBush.isEnabled())) {
			HideBlocks.onTick(mc, spWorld, serverModEnabled);
		}
	}
	
	public void keyboardEvent(KeyBinding keybinding)
    {
		CustomFullscreenRes.keyboardEvent(keybinding);
    }

}
