package net.minecraft.src.betatweaks.references.oapi;

import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.dummy.HandlerOAPI;
import net.minecraft.src.overrideapi.OverrideAPI;

public class ConcreteHandler extends HandlerOAPI {
	
	@Override
	public void updateCameraAndRender(float f) {
	    if (Utils.MC.currentScreen != null)
	        OverrideAPI.GUI_HANDLER.beforeDrawScreen();
        OverrideAPI.GUI_HANDLER.onTick();
	}

}
