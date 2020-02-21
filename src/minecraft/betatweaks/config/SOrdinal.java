package betatweaks.config;

public class SOrdinal extends SInteger {
	
	public final String[] names;
	
	public SOrdinal(String name, String ...names) {
		this(name, false, names);
	}
	
	public SOrdinal(String name, boolean hasGuiAPI, String ...names) {
		this(name, hasGuiAPI, 0, names);
	}
	
	public SOrdinal(String name, boolean hasGuiAPI, int defaultValue, String ...names) {
		super(name, defaultValue, hasGuiAPI);
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
