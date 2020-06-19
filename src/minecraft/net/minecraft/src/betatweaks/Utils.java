package net.minecraft.src.betatweaks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.dummy.*;

public class Utils {

	public static final Minecraft MC = ModLoader.getMinecraftInstance();
	private static GuiScreen parentScreen;
	private static final EasyField<GuiButton> selectedButtonField = new EasyField<GuiButton>(GuiScreen.class, "selectedButton", "a");
	private static Timer timer;
	private static int lastMouseX;
    private static int lastMouseY;
    private static long mouseStillTime;
    private static GuiScreen currentScreen;
    
    private static final List<String> loadedResources = new ArrayList<String>();
	private static final List<String> missingResources = new ArrayList<String>();
	public static final String RESOURCES_PATH = "/betatweaks/resources/";

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
	
	public static boolean nmsClassExists(String className) {
		return classExists(className) || classExists("net.minecraft.src." + className);
	}
	
	public static boolean isModLoaded(String modName) {
		return ModLoader.isModLoaded(modName) || ModLoader.isModLoaded("net.minecraft.src." + modName);
	}
	
	public static void setParentScreen(GuiScreen guiscreen) {
		parentScreen = guiscreen;
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
	
	public static void replaceBlock(Block newBlock, Block oldBlock) {
		replaceBlock(newBlock, new EasyField<Block>(oldBlock, Block.class));
	}
	
	public static void replaceBlock(Block newBlock, String ...fields) {
		replaceBlock(newBlock, new EasyField<Block>(Block.class, fields));
	}
	
	public static void replaceBlock(Block newBlock, EasyField<Block> blockField) {
		blockField.removeFinalModifier();
		blockField.set(newBlock);
		Block.blocksList[newBlock.blockID] = newBlock;
	}
	
	public static int cursorX() {
        return (Mouse.getX() * MC.currentScreen.width) / MC.displayWidth;
	}
	
	public static int cursorY() {
        return MC.currentScreen.height - (Mouse.getY() * MC.currentScreen.height) / MC.displayHeight - 1;
	}
	
	//Used to draw a screen on the first frame it is overrided
	public static GuiScreen overrideCurrentScreen(GuiScreen guiscreen) {
		MC.displayGuiScreen(guiscreen);
		GL11.glClear(256);
		if(timer == null) {
			timer = new EasyField<Timer>(Minecraft.class, "timer", "T").get(MC);
		}
		guiscreen.drawScreen(cursorY(), cursorX(), timer.renderPartialTicks);
		return guiscreen;
	}
		
	public static GuiScreen overrideCurrentScreen(Class<? extends GuiScreen> guiscreenClass) {
		try {
			return overrideCurrentScreen(guiscreenClass.newInstance());
		} 
		catch (Exception e) { return null; }
	}
	
	public static boolean buttonClicked(GuiButton button) {
		if(selectedButtonField.get(MC.currentScreen) == button) 
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
		if(Math.abs(cursorX - lastMouseX) > 5 || Math.abs(cursorY - lastMouseY) > 5 || MC.currentScreen != currentScreen)
        {
            lastMouseX = cursorX;
            lastMouseY = cursorY;
            
            resetMouseStillTime();
            currentScreen = MC.currentScreen;
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
	
	public static void logError(String... lines) {
		System.out.println(new StringBuilder().append("BETATWEAKS ERROR: ").append(lines[0]).toString());
		for (String message : lines) {
			if(message == lines[0]) continue;
			System.out.println(new StringBuilder().append('\t').append(message).toString());
		}
	}
	
	public static URL getResourceURL(String resource) {
		return getResourceURLAbsolute(new StringBuilder().append(RESOURCES_PATH).append(resource).toString());
	}
	
	private static URL getResourceURLAbsolute(String resource) {
		return Utils.class.getResource(resource);
	}
	
	public static String getResourceAbsolute(String resource) {
		resourceExistsAbsolute(resource);
		return resource;
	}
	
	public static String getResource(String resource) {
		return getResourceAbsolute(new StringBuilder().append(RESOURCES_PATH).append(resource).toString());
	}
	
	public static boolean resourceExists(String resource) {
		return resourceExistsAbsolute(new StringBuilder().append(RESOURCES_PATH).append(resource).toString());
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
	
	private static String getMissingTexture() {
		BufferedImage missingTexture = new BufferedImage(16, 16, 2);
		Graphics g = missingTexture.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Color.BLACK);
        g.drawString("missingtex", 1, 10);
        g.dispose();
		URL jarURL = mod_BetaTweaks.class.getResource(".");
		if(jarURL.getPath().contains("net/minecraft/src")) {
			try {
				jarURL = jarURL.toURI().resolve("..").resolve("..").resolve("..").toURL();
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
		File outputfile = new File(jarURL.getPath() + RESOURCES_PATH + "missingTexture.png");
		if(outputfile.getParentFile() != null) {
			outputfile.getParentFile().mkdirs();
		}
		try {
			ImageIO.write(missingTexture, "png", outputfile);
		} 
		catch (IOException e) { e.printStackTrace(); }
		return RESOURCES_PATH + "missingTexture.png";
	}
	
	private static int blockMissingTextureIndex = 0;
	
	public static int getBlockTexture(String texturePath) {
		if(resourceExists(texturePath)) {
			return ModLoader.addOverride("/terrain.png", getResource(texturePath));
		}
		if(blockMissingTextureIndex == 0) {
			blockMissingTextureIndex = ModLoader.addOverride("/terrain.png", getMissingTexture());
		}
		return blockMissingTextureIndex;
	}
	
	public static class EasyField<T> {

		private static final EasyField<Integer> modifiersField = new EasyField<Integer>(Field.class, "modifiers");
		public final Field field;
		
		public EasyField(Object value, Class<?> target) {
			this(value, target, null);
		}
		
		public EasyField(Object value, Class<?> target, Object instance) {
			Field correctField = null;
			for (Field field : target.getDeclaredFields()) {
				try {
					if(field.get(instance) == value) {
						field.setAccessible(true);
						correctField = field;
						break;
					}
				} 
				catch (Exception e) { } 
			}
			this.field = correctField;
			if(this.field == null)
			logError("Failed to locate field " + value.getClass().getSimpleName() + " in class " + target.getSimpleName());
		}
		
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
			logError("Failed to locate field " + names[0] + " in class " + target.getSimpleName());
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
