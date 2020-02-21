// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package betatweaks.gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import betatweaks.Utils;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiConflictWarning;
import net.minecraft.src.GuiErrorScreen;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.MinecraftError;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Timer;
import net.minecraft.src.UnexpectedThrowable;
import net.minecraft.src.mod_BetaTweaks;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, GameSettings, GuiSmallButton, 
//            GuiButton

public class GuiInitialHijack extends GuiScreen
{

    public GuiInitialHijack()
    {
    }

    public void onGuiClosed()
    {
    	Timer timer = null;
        Boolean fullscreen = null;
    	GuiScreen guiscreen = new GuiMainMenuCustom();
    	Utils.mc.currentScreen = guiscreen;
    	ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);        
    	guiscreen.setWorldAndResolution(Utils.mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        try
        {
            do
            {
            	if(!Utils.mc.running)
                {
                    break;
                }
            	Field field = Minecraft.class.getDeclaredFields()[8];
        		field.setAccessible(true);
        		try {
        			timer = (Timer)field.get(Utils.mc);
        		} catch (IllegalAccessException e1) {
        			e1.printStackTrace();
        		}
                try
                {
                	timer.updateTimer();
                    for(int j = 0; j < timer.elapsedTicks; j++)
                    {
                        try
                        {
                        	Utils.mc.runTick();
                        	
                            continue;
                        }
                        catch(MinecraftException minecraftexception1)
                        {
                        	Utils.mc.theWorld = null;
                        }
                        Utils.mc.changeWorld1(null);
                        Utils.mc.displayGuiScreen(new GuiConflictWarning());
                    }
                    
                    field = Minecraft.class.getDeclaredFields()[3];
            		field.setAccessible(true);
            		try {
            			fullscreen = field.getBoolean(Utils.mc);
            		} catch (IllegalAccessException e1) {
            			e1.printStackTrace();
            		}
                    if(!Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(!Utils.mc.skipRenderWorld)
                    {
                    	Utils.mc.entityRenderer.updateCameraAndRender(timer.renderPartialTicks);
                    }
                    if(!Display.isActive())
                    {
                        if(fullscreen)
                        {
                        	Utils.mc.toggleFullscreen();
                        }
                        Thread.sleep(10L);
                    }
                    Thread.yield();
                    if(Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(Utils.mc.mcCanvas != null && !fullscreen && (Utils.mc.mcCanvas.getWidth() != Utils.mc.displayWidth || Utils.mc.mcCanvas.getHeight() != Utils.mc.displayHeight))
                    {
                    	int displayWidth = Utils.mc.mcCanvas.getWidth();
                        int displayHeight = Utils.mc.mcCanvas.getHeight();
                        if(displayWidth <= 0)
                        {
                            displayWidth = 1;
                        }
                        if(displayHeight <= 0)
                        {
                            displayHeight = 1;
                        }
                        resize(displayWidth, displayHeight);
                    }
                }
                catch(MinecraftException minecraftexception)
                {
                	Utils.mc.theWorld = null;
                	Utils.mc.changeWorld1(null);
                	Utils.mc.displayGuiScreen(new GuiConflictWarning());
                }
                catch(OutOfMemoryError outofmemoryerror)
                {
                	Utils.mc.func_28002_e();
                	Utils.mc.displayGuiScreen(new GuiErrorScreen());
                    System.gc();
                }
                
            } while((Utils.mc.currentScreen instanceof GuiMainMenuCustom));
        }
        catch(MinecraftError minecrafterror) { }
        catch(Throwable throwable)
        {
        	Utils.mc.func_28002_e();
            throwable.printStackTrace();
            Utils.mc.onMinecraftCrash(new UnexpectedThrowable("Unexpected error", throwable));
        }
        finally
        {
        	//mod_BetaTweaks.dontOverride = true;
        	//Utils.overrideCurrentScreen(Utils.mc, Utils.mc.currentScreen);
        	mod_BetaTweaks.firstGuiScreenAfterHijack = Utils.mc.currentScreen;
        }
    }
    
    
    private void resize(int i, int j)
    {
        if(i <= 0)
        {
            i = 1;
        }
        if(j <= 0)
        {
            j = 1;
        }
        Utils.mc.displayWidth = i;
        Utils.mc.displayHeight = j;
        if(Utils.mc.currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, i, j);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            Utils.mc.currentScreen.setWorldAndResolution(Utils.mc, k, l);
        }
    }

}
