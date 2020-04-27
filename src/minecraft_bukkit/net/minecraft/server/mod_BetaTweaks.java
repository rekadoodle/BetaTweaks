package net.minecraft.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class mod_BetaTweaks extends BaseModMp{

	@Override
	public String Version() {
		return "v1";
	}

	mod_BetaTweaks(){
			ClassLoader classloader = (net.minecraft.server.ModLoader.class).getClassLoader();
			Method addmodMethod;
			try {
				addmodMethod = ModLoader.class.getDeclaredMethod("addMod", ClassLoader.class, String.class);
				addmodMethod.setAccessible(true);
				try {
					addmodMethod.invoke(null, new Object[] {
							classloader, "BetaTweaksMP.class"
						});
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	}
	

	public boolean hasClientSide()
	{
		  return false;
	}
}
