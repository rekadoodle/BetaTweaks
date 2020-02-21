package net.minecraft.src;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.src.betatweaks.JsonServer;

public class mod_BetaTweaks extends BaseModMp{

	@Override
	public String Version() {
		return "v1";
	}

	mod_BetaTweaks(){
		
		//JsonServer.main();
			ClassLoader classloader = (net.minecraft.src.ModLoader.class).getClassLoader();
			Method addmodMethod;
			try {
				addmodMethod = ModLoader.class.getDeclaredMethod("addMod", ClassLoader.class, String.class);
				addmodMethod.setAccessible(true);
				try {
					addmodMethod.invoke(null, new Object[] {
							classloader, "BetaTweaksMP.class"
						});
				} 
				catch (IllegalAccessException e) { e.printStackTrace(); } 
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				catch (InvocationTargetException e) { e.printStackTrace(); }
			} 
			catch (NoSuchMethodException e1) { e1.printStackTrace(); } 
			catch (SecurityException e1) { e1.printStackTrace(); }
			
	}
	

	public boolean hasClientSide()
	{
		  return false;
	}
}
