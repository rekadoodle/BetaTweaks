package net.minecraft.src.betatweaks.config;

public abstract class SBase<T> {
	
	public SBase(String name, T defaultValue) {
		this(name, defaultValue, false);
	}

	public SBase(String name, T defaultValue, boolean hasGuiAPI) {
		this.name = name;
		this.defaultValue = value = defaultValue;
		this.hasGuiAPI = hasGuiAPI;
	}
	
	public boolean hasGuiAPI() {
		return hasGuiAPI;
	}
	
	public String getDisplayString() {
		return SettingInfo.getNiceName(this);
	}
	
	public String[] getToolTip() {
		return SettingInfo.getTooltip(this);
	}

	public T getValue() {
		return value;
	}
	
	public void setValue(T newValue) {
		value = newValue;
	}
	
	public void setSliderValue(String newValue) {
		setSliderValue(Float.parseFloat(newValue));
	}
	
	public void setSliderValue(float newValue) {
		Config.getInstance().setSliderValue(this, newValue);
	}
	
	public String defaultValue() {
		return defaultValue.toString();
	}
	
	public abstract void setValue(String newValue);
	
	public final String name;
	public final boolean hasGuiAPI;
	public final T defaultValue;
	protected T value;
	
}
