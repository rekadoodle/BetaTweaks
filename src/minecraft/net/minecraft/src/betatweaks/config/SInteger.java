package net.minecraft.src.betatweaks.config;

public class SInteger extends SBase<Integer> {

	public SInteger(String name, int defaultValue) {
		this(name, defaultValue, false);
	}
	
	public SInteger(String name, int defaultValue, boolean hasGuiAPI) {
		super(name, defaultValue, hasGuiAPI);
	}

	public void setValue(String newValue) {
		setValue(Integer.parseInt((String)newValue));
	}

}
