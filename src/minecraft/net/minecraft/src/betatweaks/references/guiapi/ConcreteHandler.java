package net.minecraft.src.betatweaks.references.guiapi;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.DisplayMode;

import net.minecraft.src.*;
import net.minecraft.src.betatweaks.CustomFullscreenRes;
import net.minecraft.src.betatweaks.Graphics;
import net.minecraft.src.betatweaks.Utils;
import net.minecraft.src.betatweaks.config.*;
import net.minecraft.src.betatweaks.dummy.HandlerGuiAPI;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class ConcreteHandler extends HandlerGuiAPI {
	//Code here is suboptimal because this class is designed to work with both v10.4 and v11.0 of GuiAPI
	//Maybe I should have made one class for each version...
	
	private int scrollPos;
	private String[] tooltip;
	private Widget hoveredButton;

	@SuppressWarnings("rawtypes")
	private ArrayList<Setting> settings = new ArrayList<Setting>();
	private ModSettingScreen mainscreen = new ModSettingScreen("Beta Tweaks");
	
	private WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn(new Widget[0]);
	private WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn(new Widget[0]);
	private WidgetSinglecolumn widgetServer = new WidgetSinglecolumn(new Widget[0]);

	private net.minecraft.src.betatweaks.config.Config cfg = net.minecraft.src.betatweaks.config.Config.getInstance();
	
	private boolean oldGuiAPI;

	private Button serverBT;
	private Button clientBT;
	private Button gameplayBT;
	private static List<DisplayMode> modes;
	private static WidgetInt resWidget;
	private static SettingInt resSetting;
	
	public void init(List<DisplayMode> resolutions) {
		modes = resolutions;
		oldGuiAPI = !Utils.classExists("GuiApiHelper");
		
		//MAIN SCREEN
        mainscreen.append(clientBT = createLinkButton("Clientside Settings", widgetClientside));
		mainscreen.append(gameplayBT = createLinkButton("Gameplay Settings", widgetGameplay));
        mainscreen.append(serverBT = createLinkButton("Server Settings", widgetServer));
		
		// CLIENTSIDE
        widgetClientside.add(new Label("GUI/HUD Settings"));
        addSettings(widgetClientside, new SBase<?>[] {
        	cfg.draggingShortcuts, cfg.logoStyle, cfg.mainmenuPanorama, cfg.mainmenuQuitButton, cfg.serverList,
        	cfg.improvedChat, cfg.scrollableControls, cfg.ingameTexurePackButton, cfg.fovSlider, cfg.hideAchievementNotifications
        });
        
        if(modes != null) {
        	widgetClientside.add(new Label("Custom Fullscreen Resolution"));
            resSetting = new SettingInt("customRes", modes.size() - 1, 0, modes.size() - 1);
            if(modes.contains(CustomFullscreenRes.get())) resSetting.set(modes.indexOf(CustomFullscreenRes.get()));
            resWidget = new WidgetInt((SettingInt)resSetting, "Custom Resolution");
            resWidget.addCallback(new ModAction(
            		ConcreteHandler.class, "updateResolutionWidget", "Callback for Resolution Change"));
            resSetting.setTooltipContent(cfg.customFullscreenRes);
            settings.add(resSetting);
            widgetClientside.add(resWidget);
            
            SimpleButtonModel model = new SimpleButtonModel();
    		model.addActionCallback(new ModAction(
            		Utils.class, "toggleFullscreen", "Toggle Fullscreen Button"));
            Button button = new Button(model);
            button.setText("Toggle Custom Fullscreen");
            widgetClientside.add(button);
        }
        
        widgetClientside.add(new Label("Block Settings"));
        addSettings(widgetClientside, new SBase<?>[] {
        	cfg.indevStorageBlocks, cfg.hideLongGrass, cfg.hideDeadBush
        });
		
		// GAMEPLAY
        for(SBase<?> setting : cfg.optionsGameplay) {
			settings.add(createSetting(setting, widgetGameplay));
		}
		
		//SERVER SETTINGS
		if (Utils.isInstalled(Utils.mpHandler)) {
			for(SBase<?> setting : Utils.mpHandler.opOptions) {
				settings.add(createSetting(setting, widgetServer));
			}
		}
		loadSettings();
	}
	
	public static void updateResolutionWidget() {
		if(modes != null) {
			DisplayMode mode = modes.get(resSetting.get());
			resWidget.slider.setFormat(new StringBuilder().append(mode.getWidth()).append('x').append(mode.getHeight()).append('@').append(mode.getFrequency()).toString());
			CustomFullscreenRes.set(mode);
		}
	}

	@Override
	public void handleTooltip(GuiScreen guiscreen, int posX, int posY) {
		if(this.isGuiModScreen(guiscreen))
		this.handleTooltip((GuiModScreen)guiscreen, posX, posY);
	}
	
	private void handleTooltip(GuiModScreen guiscreen, int posX, int posY) {
		if((hoveredButton != null && !hoveredButton.isInside(posX, posY)) || !isWidgetHovered(posX, posY)) {
			hoveredButton = null;
			tooltip = null;
			Utils.resetMouseStillTime();
			return;
		}
		if(!Utils.mouseIsStill(posX, posY) || scrollPos != getScrollPos(guiscreen)) {
			scrollPos = getScrollPos(guiscreen);
			return;
		}
		Graphics.drawMultiLineTooltip(tooltip);
	}
	
	@Override
	public boolean isGuiModScreen(GuiScreen guiscreen) {
		return guiscreen instanceof GuiModScreen;
	}
	
	@Override
	public boolean settingsChanged(GuiScreen guiscreen) {
		return Utils.getParentScreen() != guiscreen && guiscreen instanceof GuiModSelect && Utils.getParentScreen() instanceof GuiModScreen;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isWidgetHovered(int posX, int posY) {
		for(Setting setting : settings) {
        	if(isWidgetHovered(getWidget(setting), posX, posY)) {
        		if(tooltip == null) {
            		tooltip = getSBase(setting).getToolTip();
        		}
        		hoveredButton = getWidget(setting);
        		return true;
        	}
        }
		if(isWidgetHovered(clientBT, posX, posY)) {
			tooltip = new String[] {
					"These settings will work on any world or server."
			};
    		return true;
		}
		if(isWidgetHovered(gameplayBT, posX, posY)) {
			tooltip = new String[] {
					"These settings will work on any singleplayer world.",
					"They will not work on a vanilla server.",
					"If your server has BetaTweaks installed, they may be", 
					"enabled by OPs."
			};
    		return true;
		}
		if(isWidgetHovered(serverBT, posX, posY)) {
			tooltip = new String[] {
					"Server only OP settings."
			};
    		return true;
		}
		tooltip = null;
		return false;
	}
	
	private boolean isWidgetHovered(Widget widget, int posX, int posY) {
		if(widget.isInside(posX, posY) && isWidgetVisible(widget)) {
			hoveredButton = widget;
			return true;
		}
		return false;
	}
	
	private void addSettings(WidgetSinglecolumn parentGui, SBase<?>[] settingsList) {
		for(SBase<?> setting : settingsList) {
			settings.add(createSetting(setting, parentGui));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Setting createSetting(SBase<?> sbase, WidgetSinglecolumn parentGui) {
		Setting setting = null;
		if(sbase instanceof SBoolean) {
			SBoolean settingBool = (SBoolean)sbase;
			setting = new SettingBoolean(settingBool.name, settingBool.defaultValue);
			parentGui.add(new WidgetBoolean((SettingBoolean)setting, settingBool.getDisplayString(), settingBool.trueName, settingBool.falseName));
		}
		else if(sbase instanceof SOrdinal) {
			SOrdinal settingOrdinal = (SOrdinal)sbase;
			setting = new SettingMulti(settingOrdinal.name, 0, settingOrdinal.names);
			parentGui.add(new WidgetMulti((SettingMulti)setting, settingOrdinal.getDisplayString()));
		}
		else if(sbase instanceof SString) {
			SString settingString = (SString)sbase;
			setting = new SettingText(settingString.name, settingString.defaultValue);
			parentGui.add(new WidgetText((SettingText)setting, settingString.getDisplayString()));
		}
		setting.setTooltipContent(sbase);
		return setting;
	}
	
	private Button createLinkButton(String text, WidgetSinglecolumn screen) {
		SimpleButtonModel model = new SimpleButtonModel();
		model.addActionCallback(new ModAction(
        		ConcreteHandler.class, "updateResolutionWidget", "Callback for Resolution Change"));
		model.addActionCallback(
				new ModAction(GuiModScreen.class, "show", new Class[] {Widget.class})
				.setDefaultArguments(new Object[] {new WidgetSimplewindow(screen, text)}));
        Button button = new Button(model);
        button.setText(text);
        return button;
	}
	
	private boolean isWidgetVisible(Widget widget) {
    	return ((ScrollPane)(getWidget((WidgetSimplewindow)((GuiModScreen)Utils.mc.currentScreen).mainwidget))).getContent().getChildIndex(widget) != -1 && widget.isVisible();
    }
	
	private Widget getWidget(WidgetSimplewindow window) {
		if(oldGuiAPI) {
			return window.mainwidget;
		}
		return window.mainWidget;
	}
	
	@SuppressWarnings("rawtypes")
	private WidgetSetting getWidget(Setting setting) {
		if(oldGuiAPI) {
			return setting.gui;
		}
		return setting.displayWidget;
	}
	
	private int getScrollPos(GuiModScreen guiscreen) {
		if(guiscreen.mainwidget instanceof WidgetSimplewindow) {
			return ((ScrollPane)getWidget((WidgetSimplewindow) guiscreen.mainwidget)).getScrollPositionY();
		}
		return -1;
	}
	
	@Override
	public void loadSettings() {
		if (Utils.mc.theWorld == null) {
			widgetGameplay.setEnabled(true);
			for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
			widgetGameplay.getChild(i).setEnabled(true);
			serverBT.setVisible(false);

			for(SBase<?> setting : cfg.options) {
				if(setting.hasGuiAPI())
					loadSetting(setting);
			}
		}
		else if (Utils.mc.theWorld.multiplayerWorld && Utils.isInstalled(Utils.mpHandler)
				&& Utils.mpHandler.serverModInstalled) {
			widgetGameplay.setEnabled(true);
			if (Utils.mpHandler.isOp) {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(true);
				serverBT.setVisible(true);
			} else {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(false);
				serverBT.setVisible(false);
			}

			for(SBase<?> setting : Utils.mpHandler.options) {
				loadSetting(setting);
			}
		} else if (Utils.mc.theWorld.multiplayerWorld) {
			widgetGameplay.setEnabled(false);
			serverBT.setVisible(false);
		}
	}
	
	@Override
	public void updateSettings() {
		if (Utils.mc.theWorld == null || !Utils.mc.theWorld.multiplayerWorld) {
			for(SBase<?> setting : cfg.optionsGameplay) {
				if(setting.hasGuiAPI())
					updateSetting(setting);
			}
		}
		else if (Utils.mc.theWorld.multiplayerWorld && Utils.isInstalled(Utils.mpHandler)
				&& Utils.mpHandler.serverModInstalled && Utils.mpHandler.isOp) {
			SBase<?>[] serverOptions = Utils.mpHandler.options;
			for(SBase<?> setting : serverOptions) {
				if(!setting.getValue().equals(getSetting(setting).get())) {
					boolean[] newSettings = new boolean[serverOptions.length - 1];
					for(int i = 0; i < newSettings.length; i++) {
						newSettings[i] = (Boolean)getSetting(serverOptions[i]).get();
					}
					String newMOTD = (String)getSetting(Utils.mpHandler.motd).get();
					Utils.mpHandler.updateServerSettings(newSettings, newMOTD);
					break;
				}
			}
		}
		
		for(SBase<?> setting : cfg.optionsClient) {
			if(setting.hasGuiAPI())
				updateSetting(setting);
		}
		cfg.writeConfig();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateSetting(SBase setting) {
		setting.setValue(getSetting(setting).get());
	}
	
	@SuppressWarnings({ "rawtypes" })
	private SBase getSBase(Setting setting) {
		return (SBase<?>) setting.getTooltipContent();
	}
	
	@SuppressWarnings("rawtypes")
	private Setting getSetting(SBase setting) {
		for (Setting guiapiSetting : settings) {
			if(setting.name.equals(getSBase(guiapiSetting).name)) {
				return (Setting) guiapiSetting;
			}
        }
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadSetting(SBase setting) {
		try {
			getSetting(setting).set(setting.getValue());
		}
		catch(NullPointerException e) {
			System.out.println(setting.name);
		}
	}

	@Override
	public boolean isGuiModSelectScreen(GuiScreen guiscreen) {
		return guiscreen instanceof GuiModSelect;
	}

}
