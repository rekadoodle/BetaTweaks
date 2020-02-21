package betatweaks;

import java.util.ArrayList;
import betatweaks.config.*;
import net.minecraft.src.*;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class GuiAPIHandler {
	//Code here is suboptimal because this class is designed to work with both v10.4 and v11.0 of GuiAPI
	//Maybe I should have made one class for each version...
	
	private int scrollPos;
	private String[] tooltip;
	private Widget hoveredButton;

	private ArrayList<Setting> settings = new ArrayList<Setting>();
	private ModSettingScreen mainscreen = new ModSettingScreen("Beta Tweaks");
	
	private WidgetSinglecolumn widgetClientside = new WidgetSinglecolumn(new Widget[0]);
	private WidgetSinglecolumn widgetGameplay = new WidgetSinglecolumn(new Widget[0]);
	private WidgetSinglecolumn widgetServer = new WidgetSinglecolumn(new Widget[0]);

	private betatweaks.config.Config cfg = betatweaks.config.Config.getInstance();
	
	private boolean oldGuiAPI;
	
	public static GuiAPIHandler instance = new GuiAPIHandler();

	private Button serverBT;
	private Button clientBT;
	private Button gameplayBT;
	
	public void init() {
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
        widgetClientside.add(new Label("Block Settings"));
        addSettings(widgetClientside, new SBase<?>[] {
        	cfg.indevStorageBlocks, cfg.hideLongGrass, cfg.hideDeadBush
        });
		
		// GAMEPLAY
        for(SBase<?> setting : cfg.optionsGameplay) {
			settings.add(createSetting(setting, widgetGameplay));
		}
		
		//SERVER SETTINGS
		if (Utils.modInstalled("modloadermp")) {
			for(SBase<?> setting : BetaTweaksMP.opOptions) {
				settings.add(createSetting(setting, widgetServer));
			}
		}
	}

	public void handleTooltip(GuiModScreen guiscreen, int posX, int posY) {
		if((hoveredButton != null && !hoveredButton.isInside(posX, posY)) || !isWidgetHovered(posX, posY)) {
			hoveredButton = null;
			Utils.resetMouseStillTime();
			return;
		}
		if(!Utils.mouseIsStill(posX, posY) || scrollPos != getScrollPos(guiscreen)) {
			scrollPos = getScrollPos(guiscreen);
			return;
		}
		Graphics.drawMultiLineTooltip(tooltip);
	}
	
	public boolean isWidgetHovered(int posX, int posY) {
		for(Setting setting : settings) {
        	if(isWidgetHovered(getWidget(setting), posX, posY)) {
        		tooltip = getSBase(setting).getToolTip();
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
		return false;
	}
	
	public boolean isWidgetHovered(Widget widget, int posX, int posY) {
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
		else if (Utils.mc.theWorld.multiplayerWorld && Utils.modInstalled("modloadermp")
				&& BetaTweaksMP.serverModInstalled) {
			widgetGameplay.setEnabled(true);
			if (BetaTweaksMP.isOp) {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(true);
				serverBT.setVisible(true);
			} else {
				for(int i = 0; i < widgetGameplay.getNumChildren(); i++)
					widgetGameplay.getChild(i).setEnabled(false);
				serverBT.setVisible(false);
			}

			for(SBase<?> setting : BetaTweaksMP.options) {
				loadSetting(setting);
			}
		} else if (Utils.mc.theWorld.multiplayerWorld) {
			widgetGameplay.setEnabled(false);
			serverBT.setVisible(false);
		}
	}
	
	public void updateSettings() {
		if (Utils.mc.theWorld == null || !Utils.mc.theWorld.multiplayerWorld) {
			for(SBase<?> setting : cfg.optionsGameplay) {
				if(setting.hasGuiAPI())
					updateSetting(setting);
			}
		}
		else if (Utils.mc.theWorld.multiplayerWorld && Utils.modInstalled("modloadermp")
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.isOp) {
			for(SBase<?> setting : BetaTweaksMP.options) {
				if(!setting.getValue().equals(getSetting(setting).get())) {
					boolean[] newSettings = new boolean[BetaTweaksMP.options.length - 1];
					for(int i = 0; i < newSettings.length; i++) {
						newSettings[i] = (Boolean)getSetting(BetaTweaksMP.options[i]).get();
					}
					String newMOTD = (String)getSetting(BetaTweaksMP.motd).get();
					BetaTweaksMP.updateServerSettings(newSettings, newMOTD);
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
			Utils.printLn(setting.name);
		}
	}

}
