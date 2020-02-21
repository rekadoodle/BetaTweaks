package net.minecraft.src.betatweaks.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.betatweaks.ChatLineImproved;
import net.minecraft.src.betatweaks.config.Config;

public class GuiIngameImprovedChat extends GuiIngame {

	public GuiIngameImprovedChat(Minecraft minecraft) {
		super(minecraft);
		mc = minecraft;
		chatMessageList = new ArrayList<ChatLineImproved>();
		notifications = new ArrayList<ChatLine>();
		cfg = Config.getInstance();
		init();
	}
	
	private final Config cfg;
	
	public void init() {
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        width = scaledresolution.getScaledWidth();
        height = scaledresolution.getScaledHeight();
        
        boolean scrollAtTop = false;
        if(scroll == totalChatLines - chatHeight && scroll != 0) {
        	scrollAtTop = true;
        }
        
        sf = cfg.improvedChatFontScale.getValue();
		chatHeightOffset = Math.min(-cfg.improvedChatIngameHeightOffset.getValue(), (int) (46 / sf) - 21);
		chatWidthOffset = cfg.improvedChatHorizontalGap.getValue();
		chatHeight = Math.round((height + chatHeightOffset - 48 - 6 * sf) / (sf * 9)) + 1; //full height
		chatWidth = (int) (cfg.improvedChatWidthPercentage.getValue() * width);
		ingameChatHeight = (int) (cfg.improvedChatIngameHeightPercentage.getValue() * chatHeight);
		maxMessageListSize = cfg.improvedChatMaxScrollableMessages.getValue();
        
		for(; chatMessageList.size() > maxMessageListSize; chatMessageList.remove(chatMessageList.size() - 1)) { }
		totalChatLines = chatMessageList.size();
		for(ChatLineImproved chatLine : chatMessageList) {
			chatLine.initialised = false;
		}
		
		if(scrollAtTop) {
			for(ChatLineImproved chatLine : chatMessageList) {
				chatLine.updateLines(sf, chatWidth);
    			totalChatLines += chatLine.lines.size() - 1;
			}
			scroll(totalChatLines);
		}
		else if(scroll > 0) {
        	int msgCount = 0;
    		for(int i = 0; i < chatMessageList.size(); i++) {
    			scroll -= chatMessageList.get(i).lines.size();
    			if(scroll < 0) {
    	    		scroll = 0;
    				msgCount = i;
    				break;
    			}
			}
    		
    		for(int i = 0; i < msgCount; i++) {
    			chatMessageList.get(i).updateLines(sf, chatWidth);
    			scroll += chatMessageList.get(i).lines.size();
    			totalChatLines += chatMessageList.get(i).lines.size() - 1;
			}
    		scroll(0);
        }
	}
	
	private int chatHeight;
	private int chatWidth;
	private int ingameChatHeight;
	private int chatHeightOffset;
	private int chatWidthOffset;
	private int maxMessageListSize;
	private int width;
	private int height;
	public int scroll;
	private float sf;
	
	public void scroll(int scrollAmount) {
		int newScroll = scroll + scrollAmount;
		if(newScroll >= 0) {
			if(newScroll <= totalChatLines - chatHeight) {
				scroll = newScroll;
			}
			else {
				scroll = totalChatLines - chatHeight;
			}
		}
	}
	
	public void renderGameOverlay(float f, boolean flag, int i, int j)
    {
		super.renderGameOverlay(f, flag, i, j);
		
		int visibleChatLineCount = ingameChatHeight;
        boolean chatOpen = false;
        int scrollAmount = 0;
        if(mc.currentScreen instanceof GuiChat)
        {
        	visibleChatLineCount = chatHeight + 1;
        	chatOpen = true;
        	scrollAmount = scroll;
        }
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, height - 48, 0.0F);
        
        if(scrollAmount > 0) {
        	int chatPixelHeight = chatHeight * 9;
        	int scrollbarHeight = (int) Math.round((chatHeight / (double) totalChatLines) * chatPixelHeight);
        	int scrollbarY = (int) (chatHeightOffset - scrollbarHeight + 9 - ((chatPixelHeight - scrollbarHeight) * (scrollAmount / (double)(totalChatLines - chatHeight))));
        	GL11.glPushMatrix();
            GL11.glScaled(1.0F, sf, 1.0F);
        	drawRect(chatWidth - 4, scrollbarY, chatWidth, scrollbarHeight + scrollbarY, 0x80ffffff);
        	GL11.glPopMatrix();
        }
        else {
        	newMessagesWhilstScrolled = 0;
        	recentMessagesNoti.updateCounter = 200;
        }
        
        int offset = 0;
        int processedLines = 0;
        for(int i5 = 0; i5 < chatMessageList.size() && offset < visibleChatLineCount; i5++)
        {
        	
        	ChatLineImproved line = chatMessageList.get(i5);
            if(line.updateCounter >= 200 && !chatOpen)
            {
                continue;
            }
            int transparency = getTransparency(line.updateCounter, chatOpen);
            if(transparency > 0)
            {
            	if(!line.initialised) {
            		line.updateLines(sf, chatWidth);
            		totalChatLines += line.lines.size() - 1;
            	}
            	for(String s : line.lines) {
            		if(processedLines >= scrollAmount && offset < visibleChatLineCount) {
                		drawLine(offset, s, transparency, sf, chatWidth);
                    	offset++;
            		}
            		processedLines++;
            	}
            }
        }
        
        if(debug) {
        	debug = false;
        	GL11.glScaled(1.0F, sf, 1.0F);
        	 for(int i5 = 0; i5 < ingameChatHeight; i5++)
 	        {
 	        	int byte1 = chatWidthOffset;
 	            int k6 = -i5 * 9;
 	        	drawRect(byte1, k6 - 1 + chatHeightOffset, byte1 + chatWidth, k6 + 8 + chatHeightOffset, 0x80ffffff);
 	        }
        }
        
        GL11.glPopMatrix();
        
        for(ListIterator<ChatLine> iterator = notifications.listIterator(); iterator.hasNext();) {
        	ChatLine noti = iterator.next();
        	if(noti.updateCounter == 200) iterator.remove();
        	else {
            	int transparency = getTransparency(noti.updateCounter, false);
            	if(transparency > 0) {
                	String s = noti.message;
                	int radius = getWidth(s) / 2;
                	int count = iterator.previousIndex() * 10;
                	GL11.glPushMatrix();
                	drawRect(width / 2 - radius - 1, 3 * height / 4 - count, width / 2 + radius + 1, 3 * height / 4 - count + 10, transparency / 2 << 24);
                	GL11.glEnable(3042 /*GL_BLEND*/);
                	drawCenteredString(mc.fontRenderer, s, width / 2, 3 * height / 4 - count + 1, 0xffffff + (transparency << 24));
                	GL11.glPopMatrix();
            	}
        	}
        }
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }
	
	public void drawDebugChat() {
		 debug = true;
	}
	
	private int getWidth(String text) {
		return mc.fontRenderer.getStringWidth(text);
	}
	
	private int getTransparency(int updateCounter, boolean chatOpen) {
		if(chatOpen) {
			return 255;
		}
		double d = (double) updateCounter / 200D;
        d = 1.0D - d;
        d *= 10D;
        if(d < 0.0D)
        {
            d = 0.0D;
        }
        if(d > 1.0D)
        {
            d = 1.0D;
        }
        d *= d;
        return (int)(255D * d);
	}
	
	private void drawLine(int index, String text, int fade, float sf, int width) {
		int byte1 = chatWidthOffset;
        int k6 = -index * 9;
        
        
        GL11.glPushMatrix();
        GL11.glScaled(1.0F, sf, 1.0F);
        drawRect(byte1, k6 - 1 + chatHeightOffset, byte1 + width, k6 + 8 + chatHeightOffset, fade / 2 << 24);
        //drawRect(byte1, (int) (46 / sf) - 12, byte1 + width, k6 + 8 + bigChatOffset, 0x80E50000);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glScaled(sf, 1.0F, 1.0F);
        mc.fontRenderer.drawStringWithShadow(text, (int)(byte1 / sf), k6 + chatHeightOffset, 0xffffff + (fade << 24));
        //GL11.glScaled(2.0F, 2.0F, 1.0F);
        GL11.glPopMatrix();
	}
	
	public void updateTick()
    {
        super.updateTick();
        for(ChatLine line : chatMessageList) {
        	line.updateCounter++;
        }
        for(ChatLine line : notifications) {
        	line.updateCounter++;
        }

    }

    public void clearChatMessages()
    {
        chatMessageList.clear();
    	totalChatLines = 0;
    }
    
    public ChatLine addNotification(String s) {
    	ChatLine noti = new ChatLine(s);
    	noti.updateCounter = 140 - s.length();
    	notifications.add(0, noti);
    	return noti;
    }

    public void addChatMessage(String s)
    {
        if(chatMessageList.size() == maxMessageListSize) {
        	totalChatLines -= chatMessageList.get(maxMessageListSize - 1).lines.size();
        	chatMessageList.remove(chatMessageList.size() - 1);
        }
        chatMessageList.add(0, new ChatLineImproved(s, sf, chatWidth));
        totalChatLines += chatMessageList.get(0).lines.size();
        if(scroll > 0) {
        	newMessagesWhilstScrolled++;
        	scroll += chatMessageList.get(0).lines.size();
        	if(!notifications.contains(recentMessagesNoti)) {
        		notifications.add(0, recentMessagesNoti);
        	}
        	recentMessagesNoti.message = newMessagesWhilstScrolled + " new messages.";
        	recentMessagesNoti.updateCounter = 140 - recentMessagesNoti.message.length();
        }
    }
    
    private boolean debug = false;
    private ChatLine recentMessagesNoti = new ChatLine("");
    private int newMessagesWhilstScrolled = 0;
    private int totalChatLines = 0;
    private Minecraft mc;
	public List<ChatLineImproved> chatMessageList;
	private ArrayList<ChatLine> notifications;
}
