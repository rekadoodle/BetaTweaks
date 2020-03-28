package net.minecraft.src.betatweaks.references.guiapi.v10;

import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;
import net.minecraft.src.GuiModScreen;
import net.minecraft.src.Setting;
import net.minecraft.src.SettingInt;
import net.minecraft.src.WidgetBoolean;
import net.minecraft.src.WidgetInt;
import net.minecraft.src.WidgetMulti;
import net.minecraft.src.WidgetSetting;
import net.minecraft.src.WidgetSimplewindow;
import net.minecraft.src.WidgetSlider;
import net.minecraft.src.WidgetText;
import net.minecraft.src.betatweaks.references.guiapi.HandlerVersionSpecificVariables;

public class ConcreteHandler extends HandlerVersionSpecificVariables {

	@Override
	public WidgetSlider getSlider(SettingInt setting) {
		return ((WidgetInt)setting.gui).s;
	}

	@Override
	public Label getLabel(WidgetSimplewindow screen) {
		return screen.TitleWidget;
	}
	
	@Override
	public int getScrollPos(GuiModScreen guiscreen) {
		if(guiscreen.mainwidget instanceof WidgetSimplewindow) {
			return ((ScrollPane)((WidgetSimplewindow) guiscreen.mainwidget).mainwidget).getScrollPositionY();
		}
		return 0;
	}

	@Override
	public Widget getDisplayWidget(Setting<?> setting) {
		WidgetSetting widget = setting.gui;
		if(widget instanceof WidgetBoolean) {
			return ((WidgetBoolean)widget).b;
		}
		if(widget instanceof WidgetMulti) {
			return ((WidgetMulti)widget).b;
		}
		if(widget instanceof WidgetInt) {
			return sliderWidgetField.get(((WidgetInt)widget).s);
		}
		if(widget instanceof WidgetText) {
			return textWidgetField.get(((WidgetText)widget).e);
		}
		return null;
	}
}
