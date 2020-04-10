package net.minecraft.src.betatweaks.references.guiapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.DisplayMode;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;
import net.minecraft.src.GuiModScreen;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ModSettings;
import net.minecraft.src.ModSettingScreen;
import net.minecraft.src.Setting;
import net.minecraft.src.SettingInt;
import net.minecraft.src.WidgetSimplewindow;
import net.minecraft.src.WidgetSinglecolumn;
import net.minecraft.src.WidgetSlider;
import net.minecraft.src.GuiModSelect;
import net.minecraft.src.World;
import net.minecraft.src.betatweaks.BetaTweaks;
import net.minecraft.src.betatweaks.CustomFullscreenRes;
import net.minecraft.src.betatweaks.Graphics;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.config.SBase;
import net.minecraft.src.betatweaks.config.SBoolean;
import net.minecraft.src.betatweaks.config.SOrdinal;
import net.minecraft.src.betatweaks.config.SString;
import net.minecraft.src.betatweaks.dummy.HandlerGuiAPI;
import net.minecraft.src.betatweaks.dummy.HandlerModLoaderMp;

public class ConcreteHandler extends HandlerGuiAPI {

	final HandlerVersionSpecificVariables HANDLER = (HandlerVersionSpecificVariables) Utils.getHandler("guiapi" + (Utils.classExists("GuiApiHelper") ? ".v11" : ".v10"));
	
	private Config cfg = Config.getInstance();
	
	private ModSettings settings = new ModSettings("mod_BetaTweaks");
	private ModSettingScreen mainscreen = new ModSettingScreen("BetaTweaks");
	
	private WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn();
	private WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn();
	private WidgetSinglecolumn widgetServer = new WidgetSinglecolumn();
	
	private Map<SBase<?>, Setting<?>> settingMap = new HashMap<SBase<?>, Setting<?>>();
	private Map<Setting<?>, SBase<?>> reverseSettingMap = new HashMap<Setting<?>, SBase<?>>();
	
	private Button buttonClientside = createLinkButton(new WidgetSimplewindow(widgetClientside, "Clientside Settings"));
	private Button buttonGameplay = createLinkButton(new WidgetSimplewindow(widgetGameplay, "Gameplay Settings"));
	private Button buttonServer = createLinkButton(new WidgetSimplewindow(widgetServer, "Server Settings"));
	
	private SettingInt resSetting;

	private int scrollPos;
	private String[] tooltip;
	private Widget hoveredButton;
	
	@Override
	public void init(final List<DisplayMode> resolutions) {
		mainscreen.append(buttonClientside);
		mainscreen.append(buttonGameplay);
        mainscreen.append(buttonServer);
		
		// CLIENTSIDE
        widgetClientside.add(new Label("GUI/HUD Settings"));
        addSettings(widgetClientside, new SBase<?>[] {
        	cfg.draggingShortcuts, cfg.logoStyle, cfg.mainmenuPanorama, cfg.mainmenuQuitButton, cfg.serverList,
        	cfg.improvedChat, cfg.scrollableControls, cfg.ingameTexurePackButton, cfg.fovSlider, cfg.hideAchievementNotifications
        });
        
        if(resolutions != null) {
        	widgetClientside.add(new Label("Custom Fullscreen Resolution"));
        	
        	final SettingInt resSetting = settings.addSetting(widgetClientside, "Custom Resolution", "customRes", resolutions.size() - 1, 0, resolutions.size() - 1);
        	this.resSetting = resSetting;
        	
        	if(resolutions.contains(CustomFullscreenRes.get())) {
        		resSetting.set(resolutions.indexOf(CustomFullscreenRes.get()));
        	}
        	
        	final WidgetSlider slider = HANDLER.getSlider(resSetting);
        	slider.getModel().addCallback(new Runnable() {

				@Override
				public void run() {
					DisplayMode mode = resolutions.get(resSetting.get());
					slider.setFormat(new StringBuilder().append(mode.getWidth()).append('x').append(mode.getHeight()).append('@').append(mode.getFrequency()).toString());
					CustomFullscreenRes.set(mode);
				}
        		
        	});
        	
        	settingMap.put(cfg.customFullscreenRes, resSetting);
        	reverseSettingMap.put(resSetting, cfg.customFullscreenRes);
            
            widgetClientside.add(createButton("Toggle Custom Fullscreen", new Runnable() {

				@Override
				public void run() {
					CustomFullscreenRes.toggle();
				}
    			
    		}));
        }
        
        widgetClientside.add(new Label("Block Settings"));
        addSettings(widgetClientside, new SBase<?>[] {
        	cfg.indevStorageBlocks, cfg.hideLongGrass, cfg.hideDeadBush
        });
        
        // GAMEPLAY
        for(SBase<?> setting : cfg.optionsGameplay) {
			createSetting(setting, widgetGameplay);
		}
		
		//SERVER SETTINGS
		if (Utils.isInstalled(Utils.mpHandler)) {
			for(SBase<?> setting : Utils.mpHandler.opOptions) {
				createSetting(setting, widgetServer);
			}
		}
		
		loadSettingsToGUI();
	}

	private void addSettings(WidgetSinglecolumn parentGui, SBase<?>[] settingsList) {
		for(SBase<?> setting : settingsList) {
			createSetting(setting, parentGui);
		}
	}
	
	private Setting<?> createSetting(SBase<?> sbase, WidgetSinglecolumn parentGui) {
		Setting<?> setting = null;
		if(sbase instanceof SBoolean) {
			SBoolean sbool = (SBoolean)sbase;
			setting = settings.addSetting(parentGui, sbool.getDisplayString(), sbool.name, sbool.defaultValue, sbool.trueName, sbool.falseName);
		}
		else if(sbase instanceof SOrdinal) {
			SOrdinal sord = (SOrdinal)sbase;
			setting = settings.addSetting(parentGui, sord.getDisplayString(), sord.name, sord.defaultValue, sord.names);
		}
		else if(sbase instanceof SString) {
			SString sstring = (SString)sbase;
			setting = settings.addSetting(parentGui, sstring.getDisplayString(), sstring.name, sstring.defaultValue);
		}
		settingMap.put(sbase, setting);
		reverseSettingMap.put(setting, sbase);
		return setting;
	}
	
	private Button createLinkButton(final WidgetSimplewindow screen) {
		return createButton(HANDLER.getLabel(screen).getText(), new Runnable() {

			@Override
			public void run() {
				if(resSetting != null) {
					DisplayMode mode = CustomFullscreenRes.getResolutions().get(resSetting.get());
					WidgetSlider slider = HANDLER.getSlider(resSetting);
					slider.setFormat(new StringBuilder().append(mode.getWidth()).append('x').append(mode.getHeight()).append('@').append(mode.getFrequency()).toString());
				}
				GuiModScreen.show(screen);
			}
			
		});
	}
	
	private Button createButton(String text, final Runnable action) {
		SimpleButtonModel model = new SimpleButtonModel();
		model.addActionCallback(new Runnable() {

			@Override
			public void run() {
				action.run();
				GuiModScreen.clicksound();
			}
			
		});
        Button button = new Button(model);
        button.setText(text);
        return button;
	}
	
	@Override
	public void handleTooltip(GuiScreen guiscreen) {
		if(!(guiscreen instanceof GuiModScreen)) {
			return;
		}
		int posX = Utils.cursorX();
		int posY = Utils.cursorY();
		GuiModScreen guimodscreen = (GuiModScreen) guiscreen;
		if((hoveredButton == null && !isWidgetHovered(guimodscreen, posX, posY)) || !hoveredButton.isInside(posX, posY)) {
			hoveredButton = null;
			tooltip = null;
			Utils.resetMouseStillTime();
			return;
		}
		if(!Utils.mouseIsStill(posX, posY) || scrollPos != HANDLER.getScrollPos(guimodscreen)) {
			scrollPos = HANDLER.getScrollPos(guimodscreen);
			return;
		}
		Graphics.drawMultiLineTooltip(tooltip);
	}
	
	private boolean isWidgetHovered(GuiModScreen guiscreen, int posX, int posY) {
		Widget hoveredWidget = guiscreen.mainwidget.getWidgetAt(posX, posY);
		for(Setting<?> setting : settings.Settings) {
			if(HANDLER.getDisplayWidget(setting) == hoveredWidget) {
				tooltip = reverseSettingMap.get(setting).getToolTip();
				hoveredButton = hoveredWidget;
				return true;
			}
        }

		boolean flag = false;
		if(hoveredWidget instanceof Button) {
			Button button = (Button)hoveredWidget;
			if(button == buttonClientside) {
				tooltip = new String[] {
						"These settings will work on any world or server."
				};
	    		flag = true;
			}
			else if(button == buttonGameplay) {
				tooltip = new String[] {
						"These settings will work on any singleplayer world.",
						"They will not work on a vanilla server.",
						"If your server has BetaTweaks installed, they may be", 
						"enabled by OPs."
				};
	    		flag = true;
			}
			else if(button == buttonServer) {
				tooltip = new String[] {
						"Server only OP settings."
				};
	    		flag = true;
			}
		}
		if(flag) {
			hoveredButton = hoveredWidget;
		}
		return flag;
	}

	@Override
	public void onGuiScreenChanged(GuiScreen guiscreen) {
		if(guiscreen instanceof GuiModSelect) {
			if(Utils.getParentScreen() instanceof GuiModScreen) {
				boolean temp1 = cfg.indevStorageBlocks.isEnabled();
				boolean temp2 = cfg.hideLongGrass.isEnabled();
				boolean temp3 = cfg.hideDeadBush.isEnabled();
				loadSettingsFromGUI();
				BetaTweaks.INSTANCE.initSettings();
				if (temp1 != cfg.indevStorageBlocks.isEnabled() || temp2 != cfg.hideLongGrass.isEnabled()
						|| temp3 != cfg.hideDeadBush.isEnabled()) {
					if (Utils.MC.theWorld != null)
						Utils.MC.renderGlobal.loadRenderers();
				}
			}
			else {
				Utils.mpHandler.checkIfOp();
			}
		}
	}

	@Override
	public void loadSettingsToGUI() {
		for(SBase<?> setting : cfg.optionsClient) {
			if(setting.hasGuiAPI()) {
				loadSettingToGUI(setting);
			}
		}
		
		World world = Utils.MC.theWorld;
		boolean clientside = world == null || !world.multiplayerWorld;
		boolean serverMod = Utils.mpHandler.serverModInstalled;
		widgetGameplay.setEnabled(clientside || serverMod);
		if(clientside) {
			for(int i = 0; i < widgetGameplay.getNumChildren(); i++) {
				widgetGameplay.getChild(i).setEnabled(true);
			}
			buttonServer.setVisible(false);
			for(SBase<?> setting : cfg.optionsGameplay) {
				if(setting.hasGuiAPI()) {
					loadSettingToGUI(setting);
				}
			}
		}
		else {
			if (serverMod) {
				setServerSettingsVisibility(Utils.mpHandler.isOp);
				for(SBase<?> setting : Utils.mpHandler.options) {
					loadSettingToGUI(setting);
				}
			} 
			else {
				buttonServer.setVisible(false);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadSettingToGUI(SBase<?> setting) {
		((Setting)settingMap.get(setting)).set(setting.getValue());
	}
	
	private void setServerSettingsVisibility(boolean enabled) {
		for(int i = 0; i < widgetGameplay.getNumChildren(); i++) {
			widgetGameplay.getChild(i).setEnabled(enabled);
		}
		buttonServer.setVisible(enabled);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadSettingFromGUI(SBase<?> setting) {
		((SBase)setting).setValue(settingMap.get(setting).get());
	}
	
	@Override
	public void loadSettingsFromGUI() {
		for(SBase<?> setting : cfg.optionsClient) {
			if(setting.hasGuiAPI()) {
				loadSettingFromGUI(setting);
			}
		}
		
		World world = Utils.MC.theWorld;
		boolean clientside = world == null || !world.multiplayerWorld;
		HandlerModLoaderMp mpHandler = Utils.mpHandler;
		if (clientside) {
			for(SBase<?> setting : cfg.optionsGameplay) {
				if(setting.hasGuiAPI())
					loadSettingFromGUI(setting);
			}
		}
		else if (mpHandler.serverModInstalled && mpHandler.isOp) {
			SBase<?>[] serverOptions = mpHandler.options;
			for(SBase<?> setting : serverOptions) {
				if(!setting.getValue().equals(settingMap.get(setting).get())) {
					boolean[] newSettings = new boolean[serverOptions.length - 1];
					for(int i = 0; i < newSettings.length; i++) {
						newSettings[i] = (Boolean) settingMap.get(serverOptions[i]).get();
					}
					String newMOTD = (String) settingMap.get(mpHandler.motd).get();
					mpHandler.updateServerSettings(newSettings, newMOTD);
					break;
				}
			}
		}
		cfg.writeConfig();
		
	}

}
