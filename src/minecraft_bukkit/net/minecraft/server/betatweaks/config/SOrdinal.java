package net.minecraft.server.betatweaks.config;

public class SOrdinal extends SInteger {
	
	public final String[] names;
	
	public SOrdinal(String name, String ...names) {
		this(name, 0, names);
	}
	
	public SOrdinal(String name, int defaultValue, String ...names) {
		super(name, defaultValue);
		this.names = names;
	}

	public String toString() {
		return names[value];
	}
	
	public String defaultValue() {
		return names[defaultValue];
	}
	
	public void increment() {
		if(value < names.length - 1)
			setValue(value + 1);
		else
			setValue(0);
	}
	
}
