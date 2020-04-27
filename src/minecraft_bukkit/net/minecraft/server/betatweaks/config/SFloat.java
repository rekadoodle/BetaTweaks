package net.minecraft.server.betatweaks.config;

public class SFloat extends SBase<Float> {
	
	public SFloat(String name, float defaultValue) {
		super(name, defaultValue);
	}

	public void setValue(String newValue) {
		setValue(Float.parseFloat(newValue));
	}
}
