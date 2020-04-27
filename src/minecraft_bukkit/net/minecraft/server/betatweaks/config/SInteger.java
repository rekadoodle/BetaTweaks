package net.minecraft.server.betatweaks.config;

public class SInteger extends SBase<Integer> {

	public SInteger(String name, int defaultValue) {
		super(name, defaultValue);
	}

	public void setValue(String newValue) {
		setValue(Integer.parseInt((String)newValue));
	}

}
