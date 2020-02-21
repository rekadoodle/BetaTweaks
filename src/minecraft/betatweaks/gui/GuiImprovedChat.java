package betatweaks.gui;

import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import betatweaks.*;
import betatweaks.Config;
import net.minecraft.src.*;

public class GuiImprovedChat extends GuiChat {

	public GuiImprovedChat() {
		super();
	}
	
	//TODO
	//autocomplete list(scrapped for now waste of time?)
	//select text from chat
	//click links
	//ctrl Z ing complete words with smart technology

	public void initGui() {
		super.initGui();
		setMarkerIndex(0);
		int i = 1;
		options = new GuiButton[] {
				new GuiButton(10, width - 151, i, 150, 20, "Hide Options"),
				new GuiSliderBT(width - 151, i+=21, Config.getField("clientImprovedChatFontScaleValue")),
				new GuiSliderBT(width - 151, i+=21, Config.getField("clientImprovedChatWidthValue"), true),
				new GuiSliderBT(width - 151, i+=21, Config.getField("clientImprovedChatIngameHeightOffset"), true),
				new GuiSliderBT(width - 151, i+=21, Config.getField("clientImprovedChatIngameMaxHeight"), true),
				new GuiSliderBT(width - 151, i+=21, Config.getField("clientImprovedChatMaxMessagesSize")),
				new GuiButton(11, width - 151, i+=21, 150, 20, "Indicator Style: " + indicatorStyleText()),
		};
		
		for(GuiButton button : options) {
			button.enabled2 = optionsVisible;
			controlList.add(button);
		}
		
		if(Utils.mc.ingameGUI instanceof GuiIngameImprovedChat) {
			gui = (GuiIngameImprovedChat) Utils.mc.ingameGUI;
			//gui.scroll = 0;
		}
		onChatSettingChanged();
	}
	
	private String indicatorStyleText() {
		if(Config.clientImprovedChatIndicator == 0) {
			return "Vanilla";
		}
		if(Config.clientImprovedChatIndicator == 1) {
			return "Vertical";
		}
		return "Both";
	}
	
	protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id == 10) {
        	toggleOptions();
        } 
        else if(guibutton.id == 11){
        	Config.clientImprovedChatIndicator++;
        	if(Config.clientImprovedChatIndicator > 2) {
        		Config.clientImprovedChatIndicator = 0;
        	}
        	guibutton.displayString = "Indicator Style: " + indicatorStyleText();
        }
        else {
            super.actionPerformed(guibutton);
        }
    }
	
	public void onGuiClosed()
    {
        super.onGuiClosed();
        gui.scroll = 0;
    }
	
	public static float getFontScaleFactor() {
		float value = Config.clientImprovedChatFontScaleValue;
		if(value <= 0.5F) {
			return  Utils.round2dp(value / 0.5f);
		}
		return Utils.round1dp(18f * value - 8f);
	}

	public static void onChatSettingChanged() {
		try {
			((GuiIngameImprovedChat) Utils.mc.ingameGUI).init();
			((GuiImprovedChat) Utils.mc.currentScreen).init();
		} 
		catch (Exception e) { /* do nothing */ }
	}
	
	public static void drawChatBoxArea() {
		try {
			((GuiIngameImprovedChat) Utils.mc.ingameGUI).drawDebugChat();
		} 
		catch (Exception e) { /* do nothing */ }
	}

	public void init() {
		sf = getFontScaleFactor();
		markerX = getCharPosByIndex(markerIndex);
		if (textSelected()) {
			selectionX = getCharPosByIndex(selectionIndex);
		} else {
			selectionX = markerX;
		}
	}

	private static boolean optionsVisible = false;
	private GuiButton[] options;
	
	private boolean dragging = false;
	private float sf;

	private int markerIndex;
	private int markerX;

	private int selectionIndex;
	private int selectionX;

	private int lastX = -1;
	private int lastY = -1;

	private int lastMessageIndex = 0;
	private String temp;

	public void handleMouseInput() {
		int amount = Mouse.getEventDWheel();
		if (amount != 0) {
			if (amount > 0) {
				amount = 1;
			} else if (amount < 0) {
				amount = -1;
			}
			((GuiIngameImprovedChat) mc.ingameGUI).scroll(amount);
		}
		super.handleMouseInput();
	}
	
	private void toggleOptions() {
		optionsVisible = !optionsVisible;
		for(GuiButton button : options) {
			button.enabled2 = optionsVisible;
		}
	}

	protected void keyTyped(char charId, int keyId) {
		if (keyId == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		}
		else if (keyId == Keyboard.KEY_RETURN) {
			String s = message.trim();
			if (s.length() > 0) {
				String s1 = message.trim();
				if (!mc.lineIsCommand(s1)) {
					lastMessageIndex = 0;
					if (pastMessages.contains(s1)) {
						pastMessages.remove(s1);
					}
					pastMessages.add(0, s1);
					if(s1.equalsIgnoreCase("/chatoptions")) {
						toggleOptions();
						message = "";
						setMarkerIndex(0);
						return;
					}
					mc.thePlayer.sendChatMessage(s1);
				}
			}
			mc.displayGuiScreen(null);
		}
		else if (keyId == Keyboard.KEY_HOME) {
			setMarkerIndex(0);
		}
		else if (keyId == Keyboard.KEY_END) {
			setMarkerIndex(-1);
		}
		else if (keyId == Keyboard.KEY_LEFT) {
			if(markerIndex > 0 || textSelected()) {
				if (!textSelected()) {
					setMarkerIndex(markerIndex - 1);
				} else {
					setMarkerIndex(Math.min(selectionIndex, markerIndex));
				}
			}
		}
		else if (keyId == Keyboard.KEY_RIGHT) {
			if(markerIndex < message.length() || textSelected()) {
				if (!textSelected()) {
					setMarkerIndex(markerIndex + 1);
				} else {
					setMarkerIndex(Math.max(selectionIndex, markerIndex));
				}
			}
			
		}
		else if ((keyId == Keyboard.KEY_BACK || keyId == Keyboard.KEY_DELETE)) {
			if(markerIndex > 0 || textSelected()) {
				if (textSelected()) {
					typeInChat("");
				} else {
					saveMessageToUndos();
					message = new StringBuilder()
							.append(message.substring(0, markerIndex - 1))
							.append(message.substring(markerIndex)).toString();
					setMarkerIndex(markerIndex - 1);
				}
			}
		}
		else if (keyId == Keyboard.KEY_UP) {
			if (lastMessageIndex < pastMessages.size()) {
				if (lastMessageIndex == 0)
					temp = message;
				message = pastMessages.get(lastMessageIndex);
				lastMessageIndex++;
				selectAll();
			}
		}
		else if (keyId == Keyboard.KEY_DOWN) {
			if (lastMessageIndex > 0) {
				lastMessageIndex--;
				if (lastMessageIndex == 0)
					message = temp;
				else
					message = pastMessages.get(lastMessageIndex - 1);
				selectAll();
			}
		}
		else if(keyId == Keyboard.KEY_TAB) {
			int lastSpaceIndex = message.substring(0, markerIndex).lastIndexOf(" ") + 1;
			if(lastSpaceIndex < message.length()) {
				String filter = message.substring(lastSpaceIndex, markerIndex);
				for(String playerName : BetaTweaksMP.playerList) {
					if(playerName.toUpperCase().contains(filter.toUpperCase())) {
						typeInChat(playerName.substring(filter.length()));
						BetaTweaksMP.playerList.remove(playerName);
						BetaTweaksMP.playerList.add(0, playerName);
						break;
					}
				}
			}
		}
		else if (charId == '\001') { // CTRL A
			selectAll();
		}
		else if (charId == '\030') { // CTRL X
			if (textSelected()) {
				setClipboardString(getSelectedText());
				typeInChat("");
			}
		}
		else if (charId == '\032') { // CTRL Z
			if (!undos.isEmpty()) {
				lastMessageIndex = 0;
				redos.push(message);
				setMessageTo(undos.pop());
			}
		}
		else if (charId == '\031') { // CTRL Y
			if (!redos.isEmpty()) {
				undos.push(message);
				setMessageTo(redos.pop());
			}
		}
		else if (charId == '\003') { // CTRL C
			if (textSelected()) {
				setClipboardString(getSelectedText());
				gui.addNotification("Selection copied");
			}
		}
		else if (charId == '\026') // CTRL V
		{
			typeInChat(GuiScreen.getClipboardString());
		}
		else if(ChatAllowedCharacters.allowedCharacters.indexOf(charId) >= 0){
			typeInChat(String.valueOf(charId));
		}
	}
	
	
	
	private void typeInChat(String s) {
		if(s == null) return;
		for (int i = 0; i < s.length(); i++) {
			if (ChatAllowedCharacters.allowedCharacters.indexOf(s.charAt(i)) < 0) {
				gui.addNotification("Cannot enter illegal character(s)");
				return;
			}
		}
		int remainingSpace = 100 - message.length() + getSelectedText().length();
		if(remainingSpace == 0) {
			gui.addNotification("Chat full.");
			return;
		}
		if(remainingSpace < s.length()) {
			s = s.substring(0, remainingSpace);
			gui.addNotification("Text cropped");
		}
		
		int selectionStart = Math.min(selectionIndex, markerIndex);
		int selectionEnd = Math.max(selectionIndex, markerIndex);

		saveMessageToUndos();
		message = new StringBuilder()
				.append(message.substring(0, selectionStart))
				.append(s)
				.append(message.substring(selectionEnd)).toString();
		setMarkerIndex(selectionStart + s.length());
	}
	
	private String getSelectedText() {
		if(textSelected()) {
			int start = Math.min(selectionIndex, markerIndex);
			int end = Math.max(selectionIndex, markerIndex);
			return message.substring(start, end);
		}
		return "";
	}

	private static void setClipboardString(String s) {
		if (s != null) {
			try {
				java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(s), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setMessageTo(String newMessage) {
		if (newMessage.length() == 0) {
			message = newMessage;
			setMarkerIndex(0);
			return;
		}

		if (message.length() == 0) {
			message = newMessage;
			selectAll();
			return;
		}
		int crossover = Math.min(newMessage.length(), message.length());
		for (int i = 0; i < crossover; i++) {
			if (newMessage.charAt(i) != message.charAt(i)) {
				setMarkerIndex(i);
				crossover = i - 1;
				break;
			}
			if (i == Math.min(newMessage.length(), message.length()) - 1) {
				if (newMessage.length() < message.length()) {
					message = newMessage;
					setMarkerIndex(newMessage.length());
				} else {
					message = newMessage;
					setMarkerIndex(newMessage.length());
					setSelectionIndex(i + 1);
				}
				message = newMessage;
				return;
			}
		}
		for (int i = 1; i <= Math.min(newMessage.length(), message.length()); i++) {
			if (newMessage.charAt(newMessage.length() - i) != message.charAt(message.length() - i)
					|| newMessage.length() - i == crossover || message.length() - i == crossover) {
				message = newMessage;
				setSelectionIndex(newMessage.length() - i + 1);
				break;
			}
		}
		message = newMessage;
	}

	private void saveMessageToUndos() {
		undos.push(message);
		redos.clear();
	}

	protected void drawRect(float x1, float y1, float x2, float y2, int colour) {
		drawRect(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), colour);
	}

	protected void drawString(String s, float x, float y, int colour) {
		drawString(fontRenderer, s, Math.round(x), Math.round(y), colour);
	}

	public void drawScreen(int cursorX, int cursorY, float f) {

		GL11.glPushMatrix();
		GL11.glScaled(sf, sf, 1.0F);
		drawRect(2 / sf, (height - 2) / sf - 12, (width - 2) / sf, (height - 2) / sf, 0x80000000);
		GL11.glEnable(3042 /* GL_BLEND */);
		drawString((new StringBuilder()).append("> ").append(message).toString(), 2 / sf + 2, (height - 2) / sf - 10, 0xe0e0e0);
		if (dragging) {
			if (lastX != cursorX || lastY != cursorY) {
				lastX = cursorX;
				lastY = cursorY;
				setSelectionIndex(getCharIndex(cursorX));
			}
		}
		if (!textSelected()) {
			if(Config.clientImprovedChatIndicator != 0) {
				drawString((updateCounter / 6) % 2 != 0 ? "" : "|", markerX / sf, (height - 2) / sf - 10, 0xe0e0e0);
			}
			if(Config.clientImprovedChatIndicator != 1) {
				drawString((updateCounter / 6) % 2 != 0 ? "" : "_", markerX / sf, (height - 2) / sf - 10, 0xe0e0e0);
			}
			
			/* Auto complete list draw (SCRAPPED)
			if(autocompleteList != null && markerIndex == message.length()) {
				int biggestLength = 0;
				for(int i = 0; i < autocompleteList.size() && i < 10; i++) {
					String s = autocompleteList.get(i);
					int length = Math.max(getWidth(s), biggestLength);
					int colour = i == selectedAutocomplete ? 0x80000000 : 0xAA66CD00;
					drawRect(markerX / sf, (height - 2) / sf - 24 - 11 * i, (markerX + length) / sf, (height - 2) / sf - 13 - 11 * i, colour);
					drawString(s, markerX / sf, (height - 2) / sf - 24 - 11 * i, 0xe0e0e0);
					if(length > biggestLength) {
						for(int q = 0; q < i; q++) {
							int colour2 = q == selectedAutocomplete ? 0x80000000 : 0xAA66CD00;
							drawRect((markerX + biggestLength) / sf, (height - 2) / sf - 24 - 11 * q, (markerX + length) / sf, (height - 2) / sf - 13 - 11 * q, colour2);
						}
						biggestLength = length;
					}
				}
			}
			*/
			
		} else {
			int smaller = Math.min(markerX, selectionX);
			int bigger = Math.max(markerX, selectionX);
			drawRect(smaller / sf, (height - 2) / sf - 12, bigger / sf, (height - 2) / sf, 0x80ffffff);
		}
		
		
		GL11.glPopMatrix();

		for (int k = 0; k < controlList.size(); k++) {
			GuiButton guibutton = (GuiButton) controlList.get(k);
			guibutton.drawButton(mc, cursorX, cursorY);
		}
	}

	private int getCharPosByIndex(int index) {
		return (int) (xOffset() + getWidth(message.substring(0, index)) * sf);
	}

	private int getWidth(String text) {
		return fontRenderer.getStringWidth(text);
	}

	private int xOffset() {
		return (int) (2 + (getWidth("> ") + 2) * sf);
	}

	private int getCharIndex(int cursorX) {
		String s = message;
		int xOffset = xOffset();
		int letterWidth = 0;
		if (cursorX >= xOffset + getWidth(s) * sf || cursorX == -1) {
			return s.length();
		}
		if (cursorX <= xOffset) {
			return 0;
		}
		for (int i = 0; i < s.length(); i++) {
			letterWidth = (int) (getWidth(s.substring(i, i + 1)) * sf);
			if (xOffset + letterWidth / 2 > cursorX) {
				return i;
			} else if (xOffset + letterWidth > cursorX) {
				return i + 1;
			} else {
				xOffset += letterWidth;
			}
		}
		return s.length();
	}

	protected void mouseClicked(int cursorX, int cursorY, int k) {
		super.mouseClicked(cursorX, cursorY, k);
		if (k == 0) {
			for (Object obj : controlList) {
				if (obj instanceof GuiButton && ((GuiButton) obj).mousePressed(Utils.mc, cursorX, cursorY)) {
					return;
				}
			}
			setMarkerIndex(getCharIndex(cursorX));
			dragging = true;
		}
	}

	private void selectAll() {
		setMarkerIndex(0);
		setSelectionIndex(-1);
	}

	private void setMarkerIndex(int index) {
		markerIndex = index;
		if (index == -1) {
			markerIndex = message.length();
		}
		markerX = getCharPosByIndex(markerIndex);
		selectionIndex = markerIndex;
	}

	private void setSelectionIndex(int index) {
		selectionIndex = index;
		if (index == -1) {
			selectionIndex = message.length();
		}
		selectionX = getCharPosByIndex(selectionIndex);
	}

	private boolean textSelected() {
		return markerIndex != selectionIndex;
	}

	protected void mouseMovedOrUp(int cursorX, int j, int k) {
		if (k == 0) {
			dragging = false;
		}
		super.mouseMovedOrUp(cursorX, j, k);
	}

	public void updateScreen() {
		updateCounter++;
	}
	
	/* auto complete list code (SCRAPPED)
	
	private void setAutocompleteList(ArrayList<String> newList) {
		autocompleteMarker = markerIndex;
		autocompleteListStore = newList;
		selectedAutocomplete = -1;
		filterAutocompleteList();
	}
	
	private void filterAutocompleteList() {
		String text = message.substring(autocompleteMarker);
		for (String filter : autocompleteListStore) {
			
		}
	}
	
	private ArrayList<String> autocompleteList;
	private ArrayList<String> autocompleteListStore;
	private int selectedAutocomplete;
	private int autocompleteMarker;
	
	*/

	private int updateCounter = 0;
	private Stack<String> undos = new Stack<String>();
	private Stack<String> redos = new Stack<String>();
	private static ArrayList<String> pastMessages = new ArrayList<String>();
	private GuiIngameImprovedChat gui;

}
