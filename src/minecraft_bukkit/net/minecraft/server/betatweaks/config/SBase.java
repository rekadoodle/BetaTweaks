package net.minecraft.server.betatweaks.config;

public abstract class SBase<T> {

	public SBase(String name, T defaultValue) {
		this.name = name;
		this.defaultValue = value = defaultValue;
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
	
	public String defaultValue() {
		return defaultValue.toString();
	}
	
	public abstract void setValue(String newValue);
	
	public final String name;
	public final T defaultValue;
	protected T value;
	
}
