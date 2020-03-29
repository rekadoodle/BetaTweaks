package net.minecraft.src.betatweaks;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class ChatLineImproved extends ChatLine {

	public ChatLineImproved(String s, float sf, int width) {
		super(s);
		updateLines(sf, width);
	}
	
	public void updateLines(float sf, int width) {
		initialised = true;
		lines.clear();
		int kk;
		Minecraft mc = Utils.MC;
		String s = message;
    	while(mc.fontRenderer.getStringWidth(s) > 0) {
    		int lastSpace = -1;
    		for(kk = 1; kk < s.length() && mc.fontRenderer.getStringWidth(s.substring(0, kk + 1)) <= width / sf; kk++) {
    			if(Character.isWhitespace(s.charAt(kk))) {
    				lastSpace = kk;
    			}
    		}
    		if(lastSpace > -1 && kk < s.length()) {
        		kk = lastSpace;
    		}
    		lines.add(0, new StringBuilder(lastLineColour()).append(s.substring(0, kk)).toString());
            //addChatMessage(s.substring(0, i));
    		//drawLine(offset, s.substring(0, kk), j6);
        	s = s.substring(kk);
    		//offset++;
    	}
	}
	
	private String lastLineColour() {
		if(!lines.isEmpty()) {
			int i;
			if((i = lines.get(0).lastIndexOf('§')) >= 0) {
				if(lines.get(0).length() > i) {
					return lines.get((0)).substring(i, i+2);
				}
			}
		}
		return "§f";
	}

	public ArrayList<String> lines = new ArrayList<String>();
	public boolean initialised = false;
}
