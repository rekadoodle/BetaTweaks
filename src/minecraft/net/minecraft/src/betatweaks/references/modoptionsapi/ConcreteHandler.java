package net.minecraft.src.betatweaks.references.modoptionsapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.DisplayMode;

import modoptionsapi.*;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Setting;
import net.minecraft.src.WidgetSinglecolumn;
import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.config.SBoolean;
import net.minecraft.src.betatweaks.config.SOrdinal;
import net.minecraft.src.betatweaks.config.SString;
import net.minecraft.src.betatweaks.dummy.HandlerGuiAPI;

public class ConcreteHandler extends HandlerGuiAPI {
	
	private Config cfg = Config.getInstance();
	
	private Map<SBase<?>, ModOption<?>> settingMap = new HashMap<SBase<?>, ModOption<?>>();
	private Map<ModOption<?>, SBase<?>> reverseSettingMap = new HashMap<ModOption<?>, SBase<?>>();

	@Override
	public void init(List<DisplayMode> resolutions) {
		ModOptions mainscreen = new ModOptions("BetaTweaks");
		ModOptionsAPI.addMod(mainscreen);
		ModOptions clientside = new ModOptions("Clientside Settings", mainscreen);
		mainscreen.addSubOptions(clientside);
		addSettings(clientside, new SBase<?>[] {
        	cfg.draggingShortcuts, cfg.logoStyle, cfg.mainmenuPanorama, cfg.mainmenuQuitButton, cfg.serverList,
        	cfg.improvedChat, cfg.scrollableControls, cfg.ingameTexurePackButton, cfg.fovSlider, cfg.hideAchievementNotifications
        });
	}
	
	private void addSettings(ModOptions parentGui, SBase<?>[] settingsList) {
		for(SBase<?> setting : settingsList) {
			createSetting(setting, parentGui);
		}
	}
	
	private ModOption<?> createSetting(SBase<?> sbase, ModOptions parentGui) {
		ModOption<?> setting = null;
		if(sbase instanceof SBoolean) {
			SBoolean sbool = (SBoolean)sbase;
			setting = new ModBooleanOption(sbool.getDisplayString(), sbool.trueName, sbool.falseName);
			parentGui.addOption(setting);
		}
		else if(sbase instanceof SOrdinal) {
			SOrdinal sord = (SOrdinal)sbase;
			ModMappedMultiOption settingMulti = new ModMappedMultiOption(sord.getDisplayString());
			for(int i = 0; i < sord.names.length; i++) {
				settingMulti.addValue(i, sord.names[i]);
			}
			parentGui.addOption(settingMulti);
			setting = settingMulti;
		}
		else if(sbase instanceof SString) {
			SString sstring = (SString)sbase;
			setting = parentGui.addTextOption(sstring.getDisplayString());
		}
		settingMap.put(sbase, setting);
		reverseSettingMap.put(setting, sbase);
		parentGui.setWideOption(setting);
		return setting;
	}

	@Override
	public void handleTooltip(GuiScreen guiscreen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGuiScreenChanged(GuiScreen guiscreen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadSettingsToGUI() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadSettingsFromGUI() {
		// TODO Auto-generated method stub
		
	}

}
