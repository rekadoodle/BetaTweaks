package betatweaks;

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

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class Utils {

	public static final Minecraft mc = ModLoader.getMinecraftInstance();
	public static final Gui gui = new Gui();
	private static final Field modifiersField = getField(Field.class, "modifiers");
	private static final Field timerField = Utils.getField(Minecraft.class, "timer", "T");
	private static final Field selectedButtonField = Utils.getField(GuiScreen.class, "selectedButton", "a");
	private static Timer timer;
	private static boolean isFullscreen;
	private static DisplayMode customResolution;

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
	public static void loadMod(String modPath) {
		try {
			if(!classExists(modPath)) {
				modPath = "net.minecraft.src." + modPath;
			}
			Class modToLoadClass = ModLoader.class.getClassLoader().loadClass(modPath);
			getMethod(ModLoader.class, new Class<?>[] { Class.class }, "setupProperties").invoke(null, new Object[] { modToLoadClass });
			((LinkedList) getField(ModLoader.class, "modList").get(null)).add(modToLoadClass.newInstance());
		} 
		catch (Exception e) { e.printStackTrace(); }
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
	public static void overrideCurrentScreen(Minecraft mc, GuiScreen guiscreen) {
		mc.displayGuiScreen(guiscreen);
		GL11.glClear(256);
		guiscreen.drawScreen(Utils.cursorY(), Utils.cursorX(), Utils.renderPartialTicks());
	}
		
	public static void overrideCurrentScreen(Minecraft mc, Class<? extends GuiScreen> guiscreenClass) {
		try {
			overrideCurrentScreen(mc, guiscreenClass.newInstance());
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
}
