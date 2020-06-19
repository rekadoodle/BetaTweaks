package net.minecraft.src.betatweaks;

import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.dummy.DummyHandlerModLoaderMp;
import net.minecraft.src.betatweaks.dummy.HandlerAether;
import net.minecraft.src.betatweaks.dummy.HandlerForge;
import net.minecraft.src.betatweaks.dummy.HandlerGuiAPI;
import net.minecraft.src.betatweaks.dummy.HandlerHMI;
import net.minecraft.src.betatweaks.dummy.HandlerJSON;
import net.minecraft.src.betatweaks.dummy.HandlerMineColony;
import net.minecraft.src.betatweaks.dummy.HandlerModLoaderMp;
import net.minecraft.src.betatweaks.dummy.HandlerOptifine;
import net.minecraft.src.betatweaks.dummy.HandlerShaders;

public class References {

	public static HandlerGuiAPI guiapihandler;
	public static HandlerAether aetherHandler;
	public static HandlerHMI hmiHandler;
	public static HandlerMineColony minecolonyHandler;
	public static HandlerForge forgeHandler;
	public static HandlerShaders shadersHandler;
	public static HandlerOptifine optifineHandler;
	public static HandlerModLoaderMp mpHandler;
	public static HandlerJSON jsonHandler;
	public static HandlerGuiAPI modoptionsapiHandler;
	
	public static void init() {
		Config cfg = Config.getInstance();
		if(cfg.modloadermp.isEnabled() && Utils.nmsClassExists("ModLoaderMp")) {
			mpHandler = (HandlerModLoaderMp) References.getHandler("modloadermp");
		}
		else {
			mpHandler = new DummyHandlerModLoaderMp();
		}
		if(cfg.guiapi.isEnabled() && Utils.nmsClassExists("ModSettings")) {
			guiapihandler = (HandlerGuiAPI) References.getHandler("guiapi");
			
			//One of the settings in GuiAPI is the custom resolution
			//Loading all the system resolutions takes a couple of seconds so this happens on another thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					guiapihandler.init(CustomFullscreenRes.getResolutions());
				}
			}).start();
		}
		if(Utils.nmsClassExists("modoptionsapi.ModOptionsAPI")) {
			//modoptionsapiHandler = (HandlerGuiAPI) getHandler("modoptionsapi");
			//modoptionsapiHandler.init(null);
		}
		if(cfg.forge.isEnabled() && Utils.nmsClassExists("forge.ForgeHooksClient")) {
			forgeHandler = (HandlerForge) References.getHandler("forge");
		}
		if(cfg.shaders.isEnabled() && Utils.nmsClassExists("Shader")) {
			shadersHandler = (HandlerShaders) References.getHandler("shaders");
		}
		if(cfg.optifine.isEnabled() && Utils.nmsClassExists("GuiDetailSettingsOF")) {
			optifineHandler = (HandlerOptifine) References.getHandler("optifine");
		}
		if(cfg.json.isEnabled() && Utils.classExists("org.json.JSONObject")) {
			jsonHandler = (HandlerJSON) References.getHandler("json");
		}
	}
	
	public static void onModsLoaded() {
		Config cfg = Config.getInstance();
		if(cfg.hmi.isEnabled() && Utils.isModLoaded("mod_HowManyItems")) {
			hmiHandler = (HandlerHMI) References.getHandler("hmi");
		}
		if(cfg.minecolony.isEnabled() && Utils.isModLoaded("mod_MineColony")) {
			minecolonyHandler = (HandlerMineColony) References.getHandler("minecolony");
		}
		if(cfg.aether.isEnabled() && Utils.isModLoaded("mod_Aether")) {
			aetherHandler = (HandlerAether) References.getHandler("aether");
		}
	}
	
	public static Object getHandler(String path) {
		try { 
			return Utils.class.getClassLoader().loadClass(Utils.class.getPackage().getName() + ".references." + path + ".ConcreteHandler").newInstance(); 
		}
		catch (Throwable e) { 
			Utils.logError("Failed to load mod compatibility for: " + path);
			e.printStackTrace();
			return null;
		} 
	}
	
	public static boolean isInstalled(Object handler) {
		return handler != null;
	}

}
