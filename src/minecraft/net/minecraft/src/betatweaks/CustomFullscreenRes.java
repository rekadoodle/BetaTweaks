package net.minecraft.src.betatweaks;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiControls;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.gui.GuiControlsScrollable;
import net.minecraft.src.betatweaks.references.HandlerGuiAPI;

public class CustomFullscreenRes extends Thread {
	
	@Override
	public void run() {
		List<DisplayMode> modesFiltered = new ArrayList<DisplayMode>();
		try {
			DisplayMode[] allModes = Display.getAvailableDisplayModes();
			outerLoop:
			for(DisplayMode mode : allModes) {
				for(DisplayMode mode2 : modesFiltered) {
					if(mode2.getWidth() == mode.getWidth() && mode2.getHeight() == mode.getHeight()) {
						if(mode.getFrequency() > mode2.getFrequency()) {
							modesFiltered.set(modesFiltered.indexOf(mode2), mode);
						}
						continue outerLoop;
					}
					else if(mode2.getWidth() * mode2.getHeight() > mode.getWidth() * mode.getHeight()) {
						modesFiltered.add(modesFiltered.indexOf(mode2), mode);
						continue outerLoop;
					}
				}
				modesFiltered.add(mode);
			}
		} 
		catch (LWJGLException e) { e.printStackTrace(); }
		finally {
			try {
				Class<?> guiApiHandler = Utils.class.getClassLoader().loadClass("betatweaks.references.guiapi.ConcreteHandlerGuiAPI");
				Utils.guiapihandler = (HandlerGuiAPI) guiApiHandler.getDeclaredConstructor(List.class).newInstance(modesFiltered);
			} 
			catch (Exception e) { e.printStackTrace(); } 
		}
	}

	private static boolean isFullscreen;
	private static DisplayMode customResolution;
	private static int canvasWidth;
	private static int canvasHeight;
	private static final Field fullscreenField = Utils.getField(Minecraft.class, "fullscreen", "Q");
	private static final Field canvasWidthField = Utils.getField(Component.class, "width");
	private static final Field canvasHeightField = Utils.getField(Component.class, "height");
	public static final KeyBinding toggleKeybind = new KeyBinding("Custom Fullscreen", Keyboard.KEY_F8);
	private static boolean fullscreenKeyHeld = false;
	
	public static DisplayMode get() {
		return customResolution;
	}
	
	public static void set(String resolution) {
		if(resolution == null || resolution.length() == 0) return;
		String[] args = resolution.split(",");
		set(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
	}
	
	public static void set(int width, int height, int bitsPerPixel, int refreshRate) {
		Constructor<?> displayconstructor;
		try {
			displayconstructor = DisplayMode.class.getDeclaredConstructor(int.class, int.class, int.class, int.class);
			displayconstructor.setAccessible(true);
			set((DisplayMode) displayconstructor.newInstance(width, height, bitsPerPixel, refreshRate));
		} 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void set(DisplayMode mode) {
		customResolution = mode;
		Config.getInstance().customFullscreenRes.setValue(
				new StringBuilder()
				.append(mode.getWidth()).append(',')
				.append(mode.getHeight()).append(',')
				.append(mode.getBitsPerPixel()).append(',')
				.append(mode.getFrequency()).toString());
	}
	
	public static void setCanvasRes(Minecraft mc, int width, int height) {
		try {
			canvasWidthField.set(mc.mcCanvas, width);
			canvasHeightField.set(mc.mcCanvas, height);
		} 
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void toggle() {
		setFullscreen(!isFullscreen);
	}
	
	public static void setFullscreen(boolean fullscreen) {
		Minecraft mc = Utils.mc;
		isFullscreen = fullscreen;
		try
		{
			fullscreenField.set(mc, isFullscreen);
			if(fullscreen) {
				if(customResolution == null) {
					Display.setDisplayMode(Display.getDesktopDisplayMode());
				}
				else {
					Display.setDisplayMode(customResolution);
				}
				mc.displayWidth = Math.max(Display.getDisplayMode().getWidth(), 1);
				mc.displayHeight = Math.max(Display.getDisplayMode().getHeight(), 1);
				canvasWidth = mc.mcCanvas.getWidth();
				canvasHeight = mc.mcCanvas.getHeight();
				setCanvasRes(mc, mc.displayWidth, mc.displayHeight);
			}
			else {
				Display.setDisplayMode(Display.getDesktopDisplayMode());
				mc.displayWidth = 1;
                mc.displayHeight = 1;
                if(mc.mcCanvas != null)
                {
                	mc.displayWidth = Math.max(mc.mcCanvas.getWidth(), 1);
                	mc.displayHeight = Math.max(mc.mcCanvas.getHeight(), 1);
                }
    			setCanvasRes(mc, canvasWidth, canvasHeight);
            }
            if(mc.currentScreen != null)
            {
            	ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                mc.currentScreen.setWorldAndResolution(mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
            }
			Display.setFullscreen(fullscreen);
            Display.update();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static boolean isFullscreen() {
		return isFullscreen;
	}
	
	public static void onGuiTick(GuiScreen guiscreen) {
		if(Keyboard.isKeyDown(toggleKeybind.keyCode)) {
			if(!fullscreenKeyHeld && !(guiscreen instanceof GuiControls) && !(guiscreen instanceof GuiControlsScrollable)) {
				fullscreenKeyHeld = true;
				toggle();
			}
		}
		else fullscreenKeyHeld = false;
	}
	
	public static void keyboardEvent(KeyBinding keybinding) {
		if(keybinding == toggleKeybind) toggle();
	}
}
