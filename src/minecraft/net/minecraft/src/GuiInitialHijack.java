// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;

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
    	ModLoader.getMinecraftInstance().currentScreen = guiscreen;
    	ScaledResolution scaledresolution = new ScaledResolution(ModLoader.getMinecraftInstance().gameSettings, ModLoader.getMinecraftInstance().displayWidth, ModLoader.getMinecraftInstance().displayHeight);        
    	guiscreen.setWorldAndResolution(ModLoader.getMinecraftInstance(), scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        try
        {
            do
            {
            	if(!ModLoader.getMinecraftInstance().running)
                {
                    break;
                }
            	Field field = Minecraft.class.getDeclaredFields()[8];
        		field.setAccessible(true);
        		try {
        			timer = (Timer)field.get(ModLoader.getMinecraftInstance());
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
                        	ModLoader.getMinecraftInstance().runTick();
                        	
                            continue;
                        }
                        catch(MinecraftException minecraftexception1)
                        {
                        	ModLoader.getMinecraftInstance().theWorld = null;
                        }
                        ModLoader.getMinecraftInstance().changeWorld1(null);
                        ModLoader.getMinecraftInstance().displayGuiScreen(new GuiConflictWarning());
                    }
                    
                    field = Minecraft.class.getDeclaredFields()[3];
            		field.setAccessible(true);
            		try {
            			fullscreen = field.getBoolean(ModLoader.getMinecraftInstance());
            		} catch (IllegalAccessException e1) {
            			e1.printStackTrace();
            		}
                    if(!Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(!ModLoader.getMinecraftInstance().skipRenderWorld)
                    {
                        ModLoader.getMinecraftInstance().entityRenderer.updateCameraAndRender(timer.renderPartialTicks);
                    }
                    if(!Display.isActive())
                    {
                        if(fullscreen)
                        {
                        	ModLoader.getMinecraftInstance().toggleFullscreen();
                        }
                        Thread.sleep(10L);
                    }
                    Thread.yield();
                    if(Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(ModLoader.getMinecraftInstance().mcCanvas != null && !fullscreen && (ModLoader.getMinecraftInstance().mcCanvas.getWidth() != ModLoader.getMinecraftInstance().displayWidth || ModLoader.getMinecraftInstance().mcCanvas.getHeight() != ModLoader.getMinecraftInstance().displayHeight))
                    {
                    	int displayWidth = ModLoader.getMinecraftInstance().mcCanvas.getWidth();
                        int displayHeight = ModLoader.getMinecraftInstance().mcCanvas.getHeight();
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
                	ModLoader.getMinecraftInstance().theWorld = null;
                	ModLoader.getMinecraftInstance().changeWorld1(null);
                	ModLoader.getMinecraftInstance().displayGuiScreen(new GuiConflictWarning());
                }
                catch(OutOfMemoryError outofmemoryerror)
                {
                	ModLoader.getMinecraftInstance().func_28002_e();
                	ModLoader.getMinecraftInstance().displayGuiScreen(new GuiErrorScreen());
                    System.gc();
                }
                
            } while((ModLoader.getMinecraftInstance().currentScreen instanceof GuiMainMenuCustom));
        }
        catch(MinecraftError minecrafterror) { }
        catch(Throwable throwable)
        {
        	ModLoader.getMinecraftInstance().func_28002_e();
            throwable.printStackTrace();
            ModLoader.getMinecraftInstance().onMinecraftCrash(new UnexpectedThrowable("Unexpected error", throwable));
        }
        finally
        {
        	mod_BetaTweaks.firstGuiScreenAfterHijack = ModLoader.getMinecraftInstance().currentScreen;
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
        ModLoader.getMinecraftInstance().displayWidth = i;
        ModLoader.getMinecraftInstance().displayHeight = j;
        if(ModLoader.getMinecraftInstance().currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(ModLoader.getMinecraftInstance().gameSettings, i, j);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            ModLoader.getMinecraftInstance().currentScreen.setWorldAndResolution(ModLoader.getMinecraftInstance(), k, l);
        }
    }

}
