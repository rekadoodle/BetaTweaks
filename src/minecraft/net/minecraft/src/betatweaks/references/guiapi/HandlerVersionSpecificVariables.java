package net.minecraft.src.betatweaks.references.guiapi;

import de.matthiasmann.twl.DraggableButton;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ValueAdjuster;
import de.matthiasmann.twl.Widget;
import net.minecraft.src.GuiModScreen;
import net.minecraft.src.Setting;
import net.minecraft.src.SettingInt;
import net.minecraft.src.WidgetSimplewindow;
import net.minecraft.src.WidgetSlider;
import net.minecraft.src.betatweaks.Utils;

public abstract class HandlerVersionSpecificVariables {
	
	protected final Utils.EasyField<DraggableButton> sliderWidgetField = new Utils.EasyField<DraggableButton>(ValueAdjuster.class, "label");
	protected final Utils.EasyField<Widget> textWidgetField = new Utils.EasyField<Widget>(EditField.class, "textRenderer");

	public abstract WidgetSlider getSlider(SettingInt setting);
	
	public abstract Label getLabel(WidgetSimplewindow screen);
	
	public abstract int getScrollPos(GuiModScreen screen);
	
	public abstract Widget getDisplayWidget(Setting<?> setting);
}
