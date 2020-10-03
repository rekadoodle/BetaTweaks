package net.minecraft.server.betatweaks.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EasyWriter implements AutoCloseable {

	private final BufferedWriter bufferedWriter;
	
	public EasyWriter(File file) throws IOException {
		file.getParentFile().mkdirs();
		bufferedWriter = new BufferedWriter(new FileWriter(file));
	}
	
	public void print(String s) {
		if(s == null) return;
		try {
			bufferedWriter.write(s);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void println(String s) {
		print(s);
		println();
	}
	
	public void println() {
		print(System.getProperty("line.separator"));
	}
	
	public void printSettings(SBase<?>[] settings) {
		for(SBase<?> setting : settings) {
			StringBuilder builder = new StringBuilder(setting.name).append('=').append(setting.getValue());
			String comment = SettingInfo.getConfigComment(setting);
			if(comment != null)
				builder.append(' ').append('#').append(comment);
			println(builder.toString());
		}
	}
	
	@Override
	public void close() {
		try {
			if(bufferedWriter != null) bufferedWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }
	}

}
