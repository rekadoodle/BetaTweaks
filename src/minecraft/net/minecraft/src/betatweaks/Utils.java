package net.minecraft.src.betatweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.references.HandlerAether;
import net.minecraft.src.betatweaks.references.HandlerForge;
import net.minecraft.src.betatweaks.references.HandlerGuiAPI;
import net.minecraft.src.betatweaks.references.HandlerHMI;
import net.minecraft.src.betatweaks.references.HandlerJSON;
import net.minecraft.src.betatweaks.references.HandlerMineColony;
import net.minecraft.src.betatweaks.references.HandlerModLoaderMp;
import net.minecraft.src.betatweaks.references.HandlerOptifine;
import net.minecraft.src.betatweaks.references.HandlerShaders;

public class Utils {

	public static final Minecraft mc = ModLoader.getMinecraftInstance();
	private static GuiScreen parentScreen;
	private static final Field modifiersField = getField(Field.class, "modifiers");
	private static final Field timerField = Utils.getField(Minecraft.class, "timer", "T");
	private static final Field selectedButtonField = Utils.getField(GuiScreen.class, "selectedButton", "a");
	private static Timer timer;
	private static int lastMouseX;
    private static int lastMouseY;
    private static long mouseStillTime;
    private static GuiScreen currentScreen;

	// Used for easy reflection with obfuscated or regular fields
	public static final Field getField(Class<?> target, String ...names) {
		for (Field field : target.getDeclaredFields()) {
			for (String name : names) {
				if (field.getName() == name) {
					field.setAccessible(true);
					return field;
				}
			}
		}
		return null;
	}
	
	// Used to get a value of a hidden field
	public static Object getStaticFieldValue(Class<?> target, String ...names) {
		final Field field = getField(target, names);
		try {
			return field.get(null);
		}
		catch(Exception e) { return null; }
	}

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
        try {
    		Field blockField = getField(Block.class, fields);
			modifiersField.setInt(blockField, blockField.getModifiers() & ~Modifier.FINAL);
			blockField.set(null, newBlock);
			Block.blocksList[newBlock.blockID] = newBlock;
        } 
        catch (Exception e) { e.printStackTrace(); }
	}
	
	public static int cursorX() {
        return (Mouse.getX() * mc.currentScreen.width) / mc.displayWidth;
	}
	
	public static int cursorY() {
        return mc.currentScreen.height - (Mouse.getY() * mc.currentScreen.height) / Utils.mc.displayHeight - 1;
	}
	
	public static float renderPartialTicks() {
		if(timer == null) {
			try {
				timer = (Timer) timerField.get(Utils.mc);
			}
			catch (Exception e) { e.printStackTrace(); } 
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
		try {
			if(selectedButtonField.get(mc.currentScreen) == button) return true;
		} 
		catch (Exception e) { e.printStackTrace(); }
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
			//One of the settings in GuiAPI is the custom resolution
			//Loading all the system resolutions takes a couple of seconds so this happens on another thread
			//This thread then attempts to load the GuiAPI handler
			(new CustomFullscreenRes()).start();
		}
		if(classExists("EntitySheepuff")) {
			aetherHandler = (HandlerAether) getHandler("aether.ConcreteHandlerAether");
		}
		if(classExists("forge.ForgeHooksClient")) {
			forgeHandler = (HandlerForge) getHandler("forge.ConcreteHandlerForge");
		}
		if(classExists("Shader")) {
			shadersHandler = (HandlerShaders) getHandler("shaders.ConcreteHandlerShaders");
		}
		if(classExists("GuiDetailSettingsOF")) {
			optifineHandler = (HandlerOptifine) getHandler("optifine.ConcreteHandlerOptifine");
		}
		if(classExists("ModLoaderMp")) {
			mpHandler = (HandlerModLoaderMp) getHandler("modloadermp.ConcreteHandlerModLoaderMp");
		}
		if(classExists("org.json.JSONObject")) {
			jsonHandler = (HandlerJSON) getHandler("json.ConcreteHandlerJSON");
		}
	}
	
	public static void modsLoaded() {
		if(ModLoader.isModLoaded("mod_HowManyItems")) {
			hmiHandler = (HandlerHMI) getHandler("hmi.ConcreteHandlerHMI");
		}
		if(ModLoader.isModLoaded("mod_MineColony")) {
			minecolonyHandler = (HandlerMineColony) getHandler("minecolony.ConcreteHandlerMineColony");
		}
	}
	
	private static Object getHandler(String path) {
		try { 
			return Utils.class.getClassLoader().loadClass("betatweaks.references." + path).newInstance(); 
		}
		catch (Throwable e) { e.printStackTrace(); return null; } 
	}
}
