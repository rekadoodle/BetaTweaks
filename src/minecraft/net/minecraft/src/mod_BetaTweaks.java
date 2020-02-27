package net.minecraft.src;

import java.nio.FloatBuffer;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.src.betatweaks.*;
import net.minecraft.src.betatweaks.block.*;
import net.minecraft.src.betatweaks.gui.*;

import org.lwjgl.input.Keyboard;


public class mod_BetaTweaks extends BaseMod {
	
	public String Version() {
		return "v1.27 PRE";
	}
	
	//Info for mine_diver's mod menu
	public String Description() {
		return "Beta but better";
	}
	
	public String Name() {
		return "Beta Tweaks";
	}
	
	public String Icon() {
		return Utils.getResource("modMenu1.png");
	}
	
	public static GuiScreen firstGuiScreenAfterHijack;
	
	private World currentWorld = null;

	private final int guiOptionsButtonCount;
	
	public static KeyBinding zoom = new KeyBinding("Zoom", Keyboard.KEY_LCONTROL);

	private HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>> guiOverrides = new HashMap<Class<? extends GuiScreen>, Class<? extends GuiScreen>>();
	public static boolean dontOverride = false;
	public static mod_BetaTweaks instance;
	
	private net.minecraft.src.betatweaks.config.Config cfg = new net.minecraft.src.betatweaks.config.Config();
	
	public mod_BetaTweaks() {
		instance = this;
		Utils.init();
		initSettings();

		ModLoader.SetInGameHook(this, true, false);
		ModLoader.SetInGUIHook(this, true, false);

		//if ((cfg.logoStyle.getValue() != 0 || cfg.mainmenuPanorama.isEnabled()) && Utils.mc.currentScreen == null)
			//Utils.mc.currentScreen = new GuiInitialHijack();
		
		if(!cfg.disableEntityRendererOverride.isEnabled()) {
			if(!Utils.isInstalled(Utils.optifineHandler)) ModLoader.RegisterKey(this, zoom, false);
			Utils.mc.entityRenderer = new EntityRendererProxyFOV();
		}
		int i;
		try {
			i = new Utils.EasyField<EnumOptions[]>(GuiOptions.class, "field_22135_k", "l").get().length;
		}
		catch(NullPointerException e) { i = 5; }
		guiOptionsButtonCount = i;
		CustomFullscreenRes.set(cfg.customFullscreenRes.getValue());
		ModLoader.RegisterKey(this, CustomFullscreenRes.toggleKeybind, false);
	}
	
	@Override
	public void KeyboardEvent(KeyBinding keybinding)
    {
		CustomFullscreenRes.keyboardEvent(keybinding);
    }

	/**
	 * Applies loaded settings to various features
	 */
	public void initSettings() {
		overrideIngameChat = true;
		GuiMainMenuCustom.resetLogo = true;
		Utils.mc.hideQuitButton = !cfg.mainmenuQuitButton.isEnabled();

		if (cfg.lightTNTwithFist.isEnabled() && Block.tnt.getClass() == BlockTNT.class) new BlockTNTPunchable();
		if (cfg.indevStorageBlocks.isEnabled() && Block.blockSteel.getClass() == BlockOreStorage.class) BlockOreStorageIndev.init();
		BlockTallGrassHidden.setVisible(!cfg.hideLongGrass.isEnabled());
		BlockDeadBushHidden.setVisible(!cfg.hideDeadBush.isEnabled());
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
	private int buttonCount = -1;
	private int buttonCount2 = -1;
	private GuiButton texturePackButton;
	private TexturePackBase initialTexturePack;
	private boolean overrideIngameChat = false;
	
	@Override
	public void ModsLoaded() {
		Utils.modsLoaded();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
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
		
		if(guiOverrides.containsKey(guiscreen.getClass()) && !dontOverride) {
			Utils.overrideCurrentScreen(guiOverrides.get(guiscreen.getClass()));
		}
		if (firstGuiScreenAfterHijack != null) {
			Utils.overrideCurrentScreen(firstGuiScreenAfterHijack);
			firstGuiScreenAfterHijack = null;
		} else if (Utils.isInstalled(Utils.guiapihandler) && Utils.guiapihandler.isGuiModScreen(guiscreen)) {
			Utils.guiapihandler.handleTooltip(guiscreen, Utils.cursorX(), Utils.cursorY());
			if (Utils.guiapihandler.settingsChanged(guiscreen)) {
				Boolean temp1 = cfg.indevStorageBlocks.isEnabled();
				Boolean temp2 = cfg.hideLongGrass.isEnabled();
				Boolean temp3 = cfg.hideDeadBush.isEnabled();
				Utils.guiapihandler.updateSettings();
				initSettings();
				if (temp1 != cfg.indevStorageBlocks.isEnabled() || temp2 != cfg.hideLongGrass.isEnabled()
						|| temp3 != cfg.hideDeadBush.isEnabled()) {
					if (mc.theWorld != null)
						mc.renderGlobal.loadRenderers();
				}
			}
		} else if (cfg.ingameTexurePackButton.isEnabled() && guiscreen instanceof GuiIngameMenu) {
			if(buttonCount == -1 || guiscreen.controlList.size() == buttonCount) {
				buttonCount = guiscreen.controlList.size();
				texturePackButton = new GuiButton(137, guiscreen.width / 2 - 100, guiscreen.height / 4 + 72 + (byte)-16, "Mods and Texture Packs");
				texturePackButton.drawButton(mc, Utils.cursorX(), Utils.cursorY());
				guiscreen.controlList.add(texturePackButton);
				
			}
			if(Utils.buttonClicked(texturePackButton)) {
				mc.displayGuiScreen(new GuiTexturePacks(guiscreen));
			}
		} else if (guiscreen instanceof GuiOptions && !cfg.disableEntityRendererOverride.isEnabled()) {
			if(cfg.fovSlider.isEnabled() && (buttonCount2 == -1 || guiscreen.controlList.size() == buttonCount2)) {
				buttonCount2 = guiscreen.controlList.size();
				guiscreen.controlList.add(new GuiSliderBT(guiscreen.width / 2 - 155 + guiOptionsButtonCount % 2 * 160, guiscreen.height / 6 + 24 * (guiOptionsButtonCount >> 1), cfg.fov));
				((GuiButton)guiscreen.controlList.get(buttonCount2)).drawButton(mc, Utils.cursorX(), Utils.cursorY());
			}
			
		} else if (guiscreen instanceof GuiTexturePacks) {
			if(initialTexturePack == null) {
				initialTexturePack = Utils.mc.texturePackList.selectedTexturePack;
			}
			
		}
		Utils.updateParentScreen();
		if (mc.theWorld != currentWorld) {
			if (Utils.isInstalled(Utils.mpHandler) && mc.theWorld == null) {
				Utils.mpHandler.serverModInstalled = false;
			}
			if (Utils.isInstalled(Utils.guiapihandler)
					&& (currentWorld == null || !currentWorld.multiplayerWorld) != (mc.theWorld == null
							|| !mc.theWorld.multiplayerWorld)) {
				Utils.guiapihandler.loadSettings();
			}
			currentWorld = mc.theWorld;
		}
		
		CustomFullscreenRes.onGuiTick(guiscreen);
		
		if (cfg.draggingShortcuts.isEnabled()) {
			DraggingShortcuts.onGuiTick(mc, guiscreen);
		}
		return true;
	}

    //private int debug;
    @Override
	public boolean OnTickInGame(Minecraft mc) {
		
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
			if(initialTexturePack != Utils.mc.texturePackList.selectedTexturePack && mc.theWorld != null) {
				mc.renderGlobal.loadRenderers();
			}	
			initialTexturePack = null;
		}
		
		//Equip armour from hotbar
		if(cfg.draggingShortcuts.isEnabled()) {
			DraggingShortcuts.onTick(mc);
		}
		boolean spWorld = !mc.theWorld.multiplayerWorld;
		boolean serverModEnabled = !spWorld && Utils.isInstalled(Utils.mpHandler) && Utils.mpHandler.serverModInstalled;

		if ((spWorld && cfg.ladderGaps.isEnabled()) || (serverModEnabled && Utils.mpHandler.ladderGaps.isEnabled())) {
			LadderGaps.onTick(mc);
		}
		if ((spWorld && cfg.punchSheepForWool.isEnabled()) || (serverModEnabled && Utils.mpHandler.punchSheepForWool.isEnabled())) {
			PunchSheepForWool.onTick(mc, serverModEnabled);
		}
		if (spWorld && cfg.minecartBoosters.isEnabled()) {
			MinecartBoosters.onTick(mc);
		}
		if (spWorld && cfg.boatElevators.isEnabled()) {
			BoatElevators.onTick(mc);
		}
		if ((spWorld && cfg.hoeGrassForSeeds.isEnabled()) || (serverModEnabled && Utils.mpHandler.hoeGrassForSeeds.isEnabled())) {
			HoeGrassForSeeds.onTick(mc, serverModEnabled);
		}
		if ((cfg.hideLongGrass.isEnabled() || cfg.hideDeadBush.isEnabled())) {
			BlockTallGrassHidden.onTick(mc, spWorld, serverModEnabled);
		}
		return true;
	}
	
	//These methods are used to avoid reflection in classes from the betatweaks package.
	public static void drawRect(int i, int j, int k, int l, int colour) {
		Graphics.gui.drawRect(i, j, k, l, colour);
	}
	
	public static FloatBuffer fogColorBuffer(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBuffer;
	}
	
	public static float fogColorRed(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorRed;
	}
	
	public static float fogColorGreen(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorGreen;
	}
	
	public static float fogColorBlue(EntityRenderer entityrenderer) {
		return entityrenderer.fogColorBlue;
	}
	
	public static void setFallDistance(Entity e, float f) {
		e.fallDistance = f;
	}
	
	public static int xSize(GuiContainer container) {
		return container.xSize;
	}
	
	public static int ySize(GuiContainer container) {
		return container.ySize;
	}
}
