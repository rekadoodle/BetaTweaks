// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, GameSettings, GuiSmallButton, 
//            GuiButton

public class GuiInitialHijack extends GuiScreen
{

    public GuiInitialHijack(GuiScreen guiscreen, GameSettings gamesettings)
    {
        screenTitle = "Controls";
        buttonId = -1;
        parentScreen = guiscreen;
        options = gamesettings;
    }
    
    private void checkGLError(String s)
    {
        int i = GL11.glGetError();
        if(i != 0)
        {
            String s1 = GLU.gluErrorString(i);
            System.out.println("########## GL ERROR ##########");
            System.out.println((new StringBuilder()).append("@ ").append(s).toString());
            System.out.println((new StringBuilder()).append(i).append(": ").append(s1).toString());
        }
    }

    MinecraftApplet mcApplet = null;
    Timer timer = null;
    int ticksRan = 0;
    Boolean fullscreen = null;
    Long prevFrameTime;
    
    public void onGuiClosed()
    {
    	
    	//super.onGuiClosed();
    	GuiScreen guiscreen = new GuiMainMenuCustom();
    	//ModLoader.getMinecraftInstance().currentScreen = new GuiControls(new GuiMainMenu(), ModLoader.getMinecraftInstance().gameSettings);
    	//ModLoader.getMinecraftInstance().currentScreen = null;
    	ModLoader.getMinecraftInstance().currentScreen = guiscreen;
    	
        //ModLoader.getMinecraftInstance().displayGuiScreen(new GuiOptions(new GuiMainMenu(), ModLoader.getMinecraftInstance().gameSettings));
        
    	
        
        
        
       ScaledResolution scaledresolution = new ScaledResolution(ModLoader.getMinecraftInstance().gameSettings, ModLoader.getMinecraftInstance().displayWidth, ModLoader.getMinecraftInstance().displayHeight);        
       int hhh = scaledresolution.getScaledWidth();
        int jjj = scaledresolution.getScaledHeight();
        guiscreen.setWorldAndResolution(ModLoader.getMinecraftInstance(), hhh, jjj);
        
        
        Field field = Minecraft.class.getDeclaredFields()[35];
		field.setAccessible(true);
		try {
			mcApplet = (MinecraftApplet)field.get(ModLoader.getMinecraftInstance());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
        
		
        field = Minecraft.class.getDeclaredFields()[8];
		field.setAccessible(true);
		try {
			timer = (Timer)field.get(ModLoader.getMinecraftInstance());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		
        field = Minecraft.class.getDeclaredFields()[25];
		field.setAccessible(true);
		try {
			ticksRan = field.getInt(ModLoader.getMinecraftInstance());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	
		
        field = Minecraft.class.getDeclaredFields()[3];
		field.setAccessible(true);
		try {
			fullscreen = field.getBoolean(ModLoader.getMinecraftInstance());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		
        field = Minecraft.class.getDeclaredFields()[54];
		field.setAccessible(true);
		try {
			prevFrameTime = field.getLong(ModLoader.getMinecraftInstance());
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
        
        try
        {
            long l = System.currentTimeMillis();
            int i = 0;
            do
            {
                if(!ModLoader.getMinecraftInstance().running)
                {
                    break;
                }
                try
                {
                    if(mcApplet != null && !mcApplet.isActive())
                    {
                        break;
                    }
                    AxisAlignedBB.clearBoundingBoxPool();
                    Vec3D.initialize();
                    if(ModLoader.getMinecraftInstance().mcCanvas == null && Display.isCloseRequested())
                    {
                    	ModLoader.getMinecraftInstance().shutdown();
                    }
                    if(ModLoader.getMinecraftInstance().isGamePaused && ModLoader.getMinecraftInstance().theWorld != null)
                    {
                        float f = timer.renderPartialTicks;
                        timer.updateTimer();
                        timer.renderPartialTicks = f;
                    } else
                    {
                        timer.updateTimer();
                    }
                    long l1 = System.nanoTime();
                    for(int j = 0; j < timer.elapsedTicks; j++)
                    {
                    	ticksRan++;
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

                    long l2 = System.nanoTime() - l1;
                    checkGLError("Pre render");
                    RenderBlocks.fancyGrass = ModLoader.getMinecraftInstance().gameSettings.fancyGraphics;
                    ModLoader.getMinecraftInstance().sndManager.func_338_a(ModLoader.getMinecraftInstance().thePlayer, timer.renderPartialTicks);
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                    if(ModLoader.getMinecraftInstance().theWorld != null)
                    {
                    	ModLoader.getMinecraftInstance().theWorld.updatingLighting();
                    }
                    if(!Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(ModLoader.getMinecraftInstance().thePlayer != null && ModLoader.getMinecraftInstance().thePlayer.isEntityInsideOpaqueBlock())
                    {
                    	ModLoader.getMinecraftInstance().gameSettings.thirdPersonView = false;
                    }
                    if(!ModLoader.getMinecraftInstance().skipRenderWorld)
                    {
                        if(ModLoader.getMinecraftInstance().playerController != null)
                        {
                        	ModLoader.getMinecraftInstance().playerController.setPartialTime(timer.renderPartialTicks);
                        }
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
                    if(ModLoader.getMinecraftInstance().gameSettings.showDebugInfo)
                    {
                    	displayDebugInfo(l2);
                    } else
                    {
                    	prevFrameTime = System.nanoTime();
                    }
                    ModLoader.getMinecraftInstance().guiAchievement.updateAchievementWindow();
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
                    checkGLError("Post render");
                    i++;
                    ModLoader.getMinecraftInstance().isGamePaused = !ModLoader.getMinecraftInstance().isMultiplayerWorld() && ModLoader.getMinecraftInstance().currentScreen != null && ModLoader.getMinecraftInstance().currentScreen.doesGuiPauseGame();
                    while(System.currentTimeMillis() >= l + 1000L) 
                    {
                    	ModLoader.getMinecraftInstance().debug = (new StringBuilder()).append(i).append(" fps, ").append(WorldRenderer.chunksUpdated).append(" chunk updates").toString();
                        WorldRenderer.chunksUpdated = 0;
                        l += 1000L;
                        i = 0;
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
        	//ModLoader.getMinecraftInstance().shutdownMinecraftApplet();
        }
//        
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

    
    
    
   
    
    
    private void displayDebugInfo(Long i) {
    	Long x;
        Method method = Minecraft.class.getDeclaredMethods()[17];
		method.setAccessible(true);
		try {
				method.invoke(ModLoader.getMinecraftInstance(), i);
		
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
    
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
    	
    	
    }
  

    private GuiScreen parentScreen;
    protected String screenTitle;
    private GameSettings options;
    private int buttonId;
}
