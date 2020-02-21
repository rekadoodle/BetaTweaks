package net.minecraft.src.betatweaks.config;

public class SString extends SBase<String> {
	
	public SString(String name, String defaultValue) {
		this(name, defaultValue, false);
	}
	
	public SString(String name, String defaultValue, boolean hasGuiAPI) {
		super(name, defaultValue, hasGuiAPI);
	}
	
	public void setValue(String newValue){
		value = newValue;
	}

}
