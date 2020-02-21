package betatweaks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import betatweaks.config.SBase;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class Utils {

	public static final Minecraft mc = ModLoader.getMinecraftInstance();
	public static final Gui gui = new Gui();
	private static ArrayList<String> installedMods = new ArrayList<String>();
	private static GuiScreen parentScreen;
	private static BufferedWriter bufferedWriter;
	private static final Field modifiersField = getField(Field.class, "modifiers");
	private static final Field timerField = Utils.getField(Minecraft.class, "timer", "T");
	private static final Field selectedButtonField = Utils.getField(GuiScreen.class, "selectedButton", "a");
	private static Timer timer;
	private static boolean isFullscreen;
	private static DisplayMode customResolution;
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
	
	// Used to group fields as you cannot do this without reflection *shakes fist at cloud*
	public static final ArrayList<Field> getFieldsStartingWith(Class<?> target, String text) {
		ArrayList<Field> fieldList = new ArrayList<Field>();
		for (Field field : target.getDeclaredFields()) {
			if (field.getName().startsWith(text)) {
				fieldList.add(field);
			}
		}
		return fieldList;
	}

	// Used for easy reflection with obfuscated or regular methods
	public static final Method getMethod(Class<?> target, Class<?> types[], String ...names) {
		for (String name : names) {
			try {
				Method method = target.getDeclaredMethod(name, types);
				method.setAccessible(true);
				return method;
			} 
			catch (NoSuchMethodException e) {/* Do nothing */}
		}
		return null;
	}

	// Used to load a mod without it being called mod_XXX
	@SuppressWarnings("unchecked")
	public static BaseMod loadMod(String modPath) {
		try {
			if(!classExists(modPath)) {
				modPath = "net.minecraft.src." + modPath;
			}
			Class<?> modToLoadClass = ModLoader.class.getClassLoader().loadClass(modPath);
			getMethod(ModLoader.class, new Class<?>[] { Class.class }, "setupProperties").invoke(null, new Object[] { modToLoadClass });
			BaseMod mod = (BaseMod) modToLoadClass.newInstance();
			((LinkedList<BaseMod>) getField(ModLoader.class, "modList").get(null)).add(mod);
			return mod;
		} 
		catch (Exception e) { e.printStackTrace(); return null; }
	}
	
	public static boolean modInstalled(String name) {
		return installedMods.contains(name);
	}
	
	public static boolean checkModInstalled(String name, boolean modLoaded) {
		if(modLoaded) {
			installedMods.add(name);
			return true;
		}
		return false;
	}
	
	public static boolean checkModInstalled(String name, String classToCheck) {
		if(classExists(classToCheck)) {
			installedMods.add(name);
			return true;
		}
		return false;
	}
	
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} 
		catch (ClassNotFoundException e) {
			return false; 
		}
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
		guiscreen.drawScreen(Utils.cursorY(), Utils.cursorX(), Utils.renderPartialTicks());
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
	
	public static SBase[] mergeSettingArrays(Object[] input1, Object[] input2) {
		SBase[] output = new SBase[input1.length + input2.length];
		System.arraycopy(input1, 0, output, 0, input1.length);
	    System.arraycopy(input2, 0, output, input1.length, input2.length);
	    return output;
	}
	
	public static void openFile(File file) {
		try {
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			bufferedWriter = new BufferedWriter(new FileWriter(file));
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void print(String text) {
		try {
			if(bufferedWriter != null) {
				bufferedWriter.write(text);
			}
			else {
				System.out.print(text);
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void printLn() {
		printLn("");
	}
	
	public static void printLn(String text) {
		try {
			if(bufferedWriter != null) {
				bufferedWriter.write(text);
				bufferedWriter.write(System.getProperty("line.separator"));
			}
			else {
				System.out.println(text);
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void closeFile() {
		try {
			if(bufferedWriter != null) {
				bufferedWriter.close();
				bufferedWriter = null;
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void setCustomRes(String resolution) {
		String[] resProps = resolution.split(",");
		setCustomRes(resProps[0], resProps[1], resProps[2], resProps[3]);
	}
	
	public static void setCustomRes(String width, String height, String bitsPerPixel, String refreshRate) {
		setCustomRes(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(bitsPerPixel), Integer.parseInt(refreshRate));
	}
	
	public static void setCustomRes(int width, int height, int bitsPerPixel, int refreshRate) {
		Constructor<?> displayconstructor;
		try {
			displayconstructor = DisplayMode.class.getDeclaredConstructor(int.class, int.class, int.class, int.class);
			displayconstructor.setAccessible(true);
			customResolution = (DisplayMode) displayconstructor.newInstance(width, height, bitsPerPixel, refreshRate);
		} 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void setFullscreen(boolean fullscreen) {
		if(isFullscreen != fullscreen) {
			isFullscreen = fullscreen;
			try
			{
				if(fullscreen) {
					if(customResolution != null) {
						Display.setDisplayMode(customResolution);
					}
					else {
						Display.setDisplayMode(Display.getDesktopDisplayMode());
					}
					mc.displayWidth = Math.max(Display.getDisplayMode().getWidth(), 1);
					mc.displayHeight = Math.max(Display.getDisplayMode().getHeight(), 1);
				}
				else {
	                if(mc.mcCanvas != null)
	                {
	                	mc.displayWidth = Math.max(mc.mcCanvas.getWidth(), 1);
	                	mc.displayHeight = Math.max(mc.mcCanvas.getHeight(), 1);
	                } else
	                {
	                	mc.displayWidth = 1;
	                    mc.displayHeight = 1;
	                }
	            }
	            if(mc.currentScreen != null)
	            {
	            	ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	                mc.currentScreen.setWorldAndResolution(mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
	            }
				Display.setFullscreen(fullscreen);
	            Display.update();
				
			}
			catch(Exception exception)
	        {
	            exception.printStackTrace();
	        }
		}
	}
	
	public static void toggleFullscreen() {
		setFullscreen(!isFullscreen);
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
}
