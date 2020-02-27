package net.minecraft.src.betatweaks;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.dummy.*;

public class Utils {

	public static final Minecraft mc = ModLoader.getMinecraftInstance();
	private static GuiScreen parentScreen;
	private static final EasyField<Timer> timerField = new EasyField<Timer>(Minecraft.class, "timer", "T");
	private static final EasyField<GuiButton> selectedButtonField = new EasyField<GuiButton>(GuiScreen.class, "selectedButton", "a");
	private static Timer timer;
	private static int lastMouseX;
    private static int lastMouseY;
    private static long mouseStillTime;
    private static GuiScreen currentScreen;
    
    private static final List<String> loadedResources = new ArrayList<String>();
	private static final List<String> missingResources = new ArrayList<String>();
	public static final String resourcesFolder = "/betatweaks/resources/";

	// Used for easy reflection with obfuscated or regular methods
	public static final Method getMethod(Class<?> target, Class<?> types[], String ...names) {
		for (String name : names) {
			try {
				Method method = target.getDeclaredMethod(name, types);
				method.setAccessible(true);
				return method;
			} 
			catch (NoSuchMethodException e) { continue; }
		}
		return null;
	}
	
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
		} 
		catch (ClassNotFoundException e) {
			return false; 
		}
		return true;
	}
	
	public static void updateParentScreen() {
		parentScreen = mc.currentScreen;
	}
	
	public static GuiScreen getParentScreen() {
		return parentScreen;
	}
	
	public static int clearBlockID(Block block) {
		return clearBlockID(block.blockID);
	}
	
	public static int clearBlockID(int id) {
		Block.blocksList[id] = null;
		return id;
	}
	
	public static void replaceBlock(Block newBlock, String ...fields) {
		EasyField<Block> blockField = new EasyField<Block>(Block.class, fields);
		blockField.removeFinalModifier();
		blockField.set(newBlock);
		Block.blocksList[newBlock.blockID] = newBlock;
	}
	
	public static int cursorX() {
        return (Mouse.getX() * mc.currentScreen.width) / mc.displayWidth;
	}
	
	public static int cursorY() {
        return mc.currentScreen.height - (Mouse.getY() * mc.currentScreen.height) / Utils.mc.displayHeight - 1;
	}
	
	public static float renderPartialTicks() {
		if(timer == null) {
			timer = timerField.get(Utils.mc);
		}
		return timer.renderPartialTicks;
	}
	
	//Used to draw a screen on the first frame it is overrided
	public static void overrideCurrentScreen(GuiScreen guiscreen) {
		mc.displayGuiScreen(guiscreen);
		GL11.glClear(256);
		guiscreen.drawScreen(cursorY(), cursorX(), renderPartialTicks());
	}
		
	public static void overrideCurrentScreen(Class<? extends GuiScreen> guiscreenClass) {
		try {
			overrideCurrentScreen(guiscreenClass.newInstance());
		} 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static boolean buttonClicked(GuiButton button) {
		if(selectedButtonField.get(mc.currentScreen) == button) 
			return true;
		return false;
	}
	
	public static float round1dp(float f) {
		return ((int)(f * 10f)) / 10f;
	}
	
	public static float round2dp(float f) {
		return ((int)(f * 100f)) / 100f;
	}
	
	public static int toPercentage(Object object) {
		return toPercentage(((Float)object).floatValue());
	}
	
	public static int toPercentage(float f) {
		return Math.round(f * 100f);
	}
	
	public static SBase<?>[] mergeSettingArrays(Object[] input1, Object[] input2) {
		SBase<?>[] output = new SBase[input1.length + input2.length];
		System.arraycopy(input1, 0, output, 0, input1.length);
	    System.arraycopy(input2, 0, output, input1.length, input2.length);
	    return output;
	}
	
	public static boolean mouseIsStill(int cursorX, int cursorY) {
		if(Math.abs(cursorX - lastMouseX) > 5 || Math.abs(cursorY - lastMouseY) > 5 || mc.currentScreen != currentScreen)
        {
            lastMouseX = cursorX;
            lastMouseY = cursorY;
            
            resetMouseStillTime();
            currentScreen = mc.currentScreen;
            return false;
        }
        if(System.currentTimeMillis() < mouseStillTime + (long)700)
        {
            return false;
        }
        return true;
	}
	
	public static void resetMouseStillTime() {
		mouseStillTime = System.currentTimeMillis();
	}
	
	public static boolean isInstalled(Object handler) {
		return handler != null;
	}
	
	public static HandlerGuiAPI guiapihandler;
	public static HandlerAether aetherHandler;
	public static HandlerHMI hmiHandler;
	public static HandlerMineColony minecolonyHandler;
	public static HandlerForge forgeHandler;
	public static HandlerShaders shadersHandler;
	public static HandlerOptifine optifineHandler;
	public static HandlerModLoaderMp mpHandler;
	public static HandlerJSON jsonHandler;
	
	public static void init() {
		if(classExists("ModSettings")) {
			guiapihandler = (HandlerGuiAPI) getHandler("guiapi");
			
			//One of the settings in GuiAPI is the custom resolution
			//Loading all the system resolutions takes a couple of seconds so this happens on another thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					guiapihandler.init(CustomFullscreenRes.getResolutions());
				}
			}).start();
		}
		if(classExists("forge.ForgeHooksClient")) {
			forgeHandler = (HandlerForge) getHandler("forge");
		}
		if(classExists("Shader")) {
			shadersHandler = (HandlerShaders) getHandler("shaders");
		}
		if(classExists("GuiDetailSettingsOF")) {
			optifineHandler = (HandlerOptifine) getHandler("optifine");
		}
		if(classExists("ModLoaderMp")) {
			mpHandler = (HandlerModLoaderMp) getHandler("modloadermp");
		}
		if(classExists("org.json.JSONObject")) {
			jsonHandler = (HandlerJSON) getHandler("json");
		}
	}
	
	public static void modsLoaded() {
		String modpackage = mod_BetaTweaks.class.getPackage().getName() + '.';
		if(ModLoader.isModLoaded(modpackage + "mod_HowManyItems")) {
			hmiHandler = (HandlerHMI) getHandler("hmi");
		}
		if(ModLoader.isModLoaded(modpackage + "mod_MineColony")) {
			minecolonyHandler = (HandlerMineColony) getHandler("minecolony");
		}
		if(ModLoader.isModLoaded(modpackage + "mod_Aether")) {
			aetherHandler = (HandlerAether) getHandler("aether");
		}
	}
	
	private static Object getHandler(String path) {
		try { 
			return Utils.class.getClassLoader().loadClass(Utils.class.getPackage().getName() + ".references." + path + ".ConcreteHandler").newInstance(); 
		}
		catch (Throwable e) { e.printStackTrace(); return null; } 
	}
	
	public static void logError(String... lines) {
		System.out.println(new StringBuilder().append("BETATWEAKS ERROR: ").append(lines[0]).toString());
		for (String message : lines) {
			if(message == lines[0]) continue;
			System.out.println(new StringBuilder().append('\t').append(message).toString());
		}
	}
	
	public static URL getResourceURL(String resource) {
		return getResourceURLAbsolute(new StringBuilder().append(resourcesFolder).append(resource).toString());
	}
	
	private static URL getResourceURLAbsolute(String resource) {
		return Utils.class.getResource(resource);
	}
	
	public static String getResourceAbsolute(String resource) {
		resourceExistsAbsolute(resource);
		return resource;
	}
	
	public static String getResource(String resource) {
		return getResourceAbsolute(new StringBuilder().append(resourcesFolder).append(resource).toString());
	}
	
	public static boolean resourceExists(String resource) {
		return resourceExistsAbsolute(new StringBuilder().append(resourcesFolder).append(resource).toString());
	}
	
	public static boolean resourceExistsAbsolute(String resource) {
		if(loadedResources.contains(resource)) {
			return true;
		}
		if(missingResources.contains(resource)) {
			return false;
		}
		if(getResourceURLAbsolute(resource) != null) {
			loadedResources.add(resource);
			return true;
		}
		else  {
			missingResources.add(resource);
			String error = "Missing file " + new File("bin\\minecraft.jar").getAbsolutePath().replace("\\", "/") + resource;
			if(System.getProperty("java.class.path").toLowerCase().contains("eclipse"))
	        {
				logError(new String[] {error, "Alternate Location " + mod_BetaTweaks.class.getClassLoader().getResource("").getPath().replaceFirst("/*$", "") + resource});
	        }
			else {
				logError(error);
			}
			return false;
		}
	}
	
	public static class EasyField<T> {

		private static final EasyField<Integer> modifiersField = new EasyField<Integer>(Field.class, "modifiers");
		public final Field field;
		
		public EasyField(Class<?> target, String... names) {
			for (Field field : target.getDeclaredFields()) {
				for (String name : names) {
					if (field.getName() == name) {
						field.setAccessible(true);
						this.field = field;
						return;
					}
				}
			}
			this.field = null;
			logError("Failed to located field " + names[0] + " in class " + target.getSimpleName());
		}
		
		public boolean exists() {
			return field != null;
		}
		
		@SuppressWarnings("unchecked")
		public T get(Object instance) {
			try {
				return (T) field.get(instance);
			}
			catch (Exception e) { e.printStackTrace(); }
			return null;
		}
		
		public T get() {
			return this.get(null);
		}
		
		public void set(Object instance, T value) {
			try {
				field.set(instance, value);
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public void set(T value) {
			this.set(null, value);
		}
		
		public void removeFinalModifier() {
			modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
		}
		
	}
}
