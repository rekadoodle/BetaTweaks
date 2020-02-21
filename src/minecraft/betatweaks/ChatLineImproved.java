package betatweaks;

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
		Minecraft mc = Utils.mc;
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
    		lines.add(0, s.substring(0, kk));
            //addChatMessage(s.substring(0, i));
    		//drawLine(offset, s.substring(0, kk), j6);
        	s = s.substring(kk);
    		//offset++;
    	}
	}

	public ArrayList<String> lines = new ArrayList<String>();
	public boolean initialised = false;
}
