package net.minecraft.src.betatweaks.config;

public class SBoolean extends SBase<Boolean> {
	
	public SBoolean(String name, boolean defaultValue) {
		super(name, defaultValue);
	}
	
	public boolean isEnabled() {
		return value;
	}
	
	public void setValue(String newValue) {
		setValue(Boolean.parseBoolean((String)newValue));
	}
}
