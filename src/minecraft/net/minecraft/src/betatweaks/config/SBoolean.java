package net.minecraft.src.betatweaks.config;

public class SBoolean extends SBase<Boolean> {
	
	public final String trueName;
	public final String falseName;
	
	public SBoolean(String name, boolean defaultValue) {
		this(name, defaultValue, false);
	}

	public SBoolean(String name, boolean defaultValue, boolean hasGuiAPI) {
		this(name, defaultValue, hasGuiAPI, "ON", "OFF");
	}
	
	public SBoolean(String name, boolean defaultValue, boolean hasGuiAPI, String trueName, String falseName) {
		super(name, defaultValue, hasGuiAPI);
		this.trueName = trueName;
		this.falseName = falseName;
	}
	
	public boolean isEnabled() {
		return value;
	}
	
	public String defaultValue() {
		if(defaultValue) {
			return trueName;
		}
		return falseName;
	}
	
	public void setValue(String newValue) {
		setValue(Boolean.parseBoolean((String)newValue));
	}

	
}
