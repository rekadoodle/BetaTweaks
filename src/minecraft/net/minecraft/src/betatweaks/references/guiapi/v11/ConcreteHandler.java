package net.minecraft.src.betatweaks.references.guiapi.v11;

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
		return ((WidgetInt)setting.displayWidget).slider;
	}

	@Override
	public Label getLabel(WidgetSimplewindow screen) {
		return screen.titleWidget;
	}

	@Override
	public int getScrollPos(GuiModScreen guiscreen) {
		if(guiscreen.mainwidget instanceof WidgetSimplewindow) {
			return ((ScrollPane)((WidgetSimplewindow) guiscreen.mainwidget).mainWidget).getScrollPositionY();
		}
		return 0;
	}

	@Override
	public Widget getDisplayWidget(Setting<?> setting) {
		WidgetSetting widget = setting.displayWidget;
		if(widget instanceof WidgetBoolean) {
			return ((WidgetBoolean)widget).button;
		}
		if(widget instanceof WidgetMulti) {
			return ((WidgetMulti)widget).button;
		}
		if(widget instanceof WidgetInt) {
			return sliderWidgetField.get(((WidgetInt)widget).slider);
		}
		if(widget instanceof WidgetText) {
			return textWidgetField.get(((WidgetText)widget).editField);
		}
		return null;
	}

	

}
