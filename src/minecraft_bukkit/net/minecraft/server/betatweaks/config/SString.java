package net.minecraft.server.betatweaks.config;

public class SString extends SBase<String> {
	
	public SString(String name, String defaultValue) {
		super(name, defaultValue);
	}
	
	public void setValue(String newValue){
		value = newValue;
	}

}
