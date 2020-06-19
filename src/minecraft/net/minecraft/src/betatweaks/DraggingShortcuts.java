package net.minecraft.src.betatweaks;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiInventory;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PlayerController;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotCrafting;
import net.minecraft.src.mod_BetaTweaks;

public class DraggingShortcuts {
	
	private static int counter = -1;
	private static boolean droppedItem = false;
	private static int spreadCount = -1;
	private static boolean spreading = false;
	private static boolean collecting = false;
	private static boolean itemClickedOn = false;
	private static int lastSlotNo;
	private static ArrayList<Integer> spreadSlots = new ArrayList<Integer>();
	private static boolean rmbHeld;

    private static final Method getSlot = Utils.getMethod(GuiContainer.class,  new Class<?>[] {int.class, int.class}, "getSlotAtPosition", "a");

	public static void onGuiTick(Minecraft mc, GuiScreen guiscreen) {
		if(!(guiscreen instanceof GuiContainer)) {
			return;
		}
		if(References.isInstalled(References.hmiHandler) && References.hmiHandler.isGuiRecipeViewer(guiscreen)) {
			return;
		}
		GuiContainer container = (GuiContainer)guiscreen;
		
		if(References.isInstalled(References.minecolonyHandler)) {
			if(References.minecolonyHandler.disallowDraggingShortcuts(container)) {
				return;
			}
		}
		
		int x = Utils.cursorX();
        int y = Utils.cursorY();
        EntityPlayerSP player = mc.thePlayer;
    	PlayerController controller = mc.playerController;
    	int windowId = container.inventorySlots.windowId;
    	Boolean shiftClick = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);

    	Slot slot = null;
		try {
			slot = (Slot)getSlot.invoke(container, new Object[]{x, y});
		} 
		catch (Exception e) { e.printStackTrace(); }
		
		if(slot != null) {
        while(Mouse.next()) {
        	
    		if(Mouse.getEventButtonState())
	        {
    			if(Mouse.getEventButton() == 0 && slot.getHasStack() 
    					&& (player.inventory.getItemStack() == null || !slot.getStack().isItemEqual(player.inventory.getItemStack()))) {
    			
    				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
    					int i2 = slot.getStack().getMaxStackSize() / slot.getStack().stackSize;
    					for(int i = 0; i < i2; i++)
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
    				}
    				else {
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
        				if(!shiftClick) {
        					collecting = true;
        					itemClickedOn = true;
        				}
    				}
    			}
    			else if(Mouse.getEventButton() == 0 && !itemClickedOn && !slot.getHasStack()
    					&& player.inventory.getItemStack() != null) {
    				spreading = true;
    			}
    			else if(Mouse.getEventButton() == 0
    					&& player.inventory.getItemStack() != null && slot.getHasStack()
    					&& slot.getStack().isItemEqual(player.inventory.getItemStack())) {
    				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
    					int i2 = slot.getStack().getMaxStackSize() / slot.getStack().stackSize;
    					for(int i = 0; i < i2; i++)
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, shiftClick, player);
    				}
    				else {
    					collecting = true;
        				lastSlotNo = slot.slotNumber;
        				
        				controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    				}
    			}
    			else if(Mouse.getEventButton() == 1 && player.inventory.getItemStack() == null && slot.getHasStack()) {
    				if(slot instanceof SlotCrafting && shiftClick && slot.getHasStack()) {
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, shiftClick, player);
    				}
    				else if(shiftClick && slot.getHasStack()) {
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
    					controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    				}
    				else {
    					itemClickedOn = true;
    					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
    					lastSlotNo = slot.slotNumber;
    				}
	            }
    			else if(Mouse.getEventButton() == 1/* && slot.getHasStack()*/) {
    				boolean itemHeld = player.inventory.getItemStack() != null;
					controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
					ItemStack item = player.inventory.getItemStack();
					if(!itemHeld && player.inventory.getItemStack() != null) {
						itemClickedOn = true;
						if(!item.isItemEqual(player.inventory.getItemStack())) {
							itemClickedOn = false;
						}
					}
					if(item != null) {
						lastSlotNo = slot.slotNumber;
					}
				}
			} 
			else
	        {
				if(Mouse.getEventButton() == 0 && !spreading && !collecting && (!slot.getHasStack() 
						|| (player.inventory.getItemStack() != null && slot.getStack().isItemEqual(player.inventory.getItemStack())))) {
					if (itemClickedOn) itemClickedOn = false;
					else controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
					collecting = false;
	            }
				else if(Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1) {
					itemClickedOn = false;
    				spreading = false;
    				collecting = false;
    				spreadSlots.clear();
    				spreadCount = -1;
    			}
				else if(Mouse.getEventButton() == 1 && (!slot.getHasStack()
						|| (player.inventory.getItemStack() != null && slot.getStack().isItemEqual(player.inventory.getItemStack())))) {
					if (itemClickedOn) itemClickedOn = false;
					//else controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
				}
	        }
			if(Mouse.isButtonDown(1)) {
				if (!itemClickedOn)
				{
					if(slot.slotNumber != lastSlotNo /*&& !itemClickedOn*/
							&& player.inventory.getItemStack() != null && (!slot.getHasStack() 
							|| (slot.getStack().isItemEqual(player.inventory.getItemStack()) 
									&& slot.getStack().getMaxStackSize() > slot.getStack().stackSize))) {
						controller.handleMouseClick(windowId, slot.slotNumber, 1, false, player);
						lastSlotNo = slot.slotNumber;
					}
				}
					
			}
			else if(Mouse.isButtonDown(0)) {
				if (spreading) {
					if(!slot.getHasStack() && slot.isItemValid(player.inventory.getItemStack())) {
						if(spreadCount == -1) {
							spreadCount = player.inventory.getItemStack().stackSize;
						}
						
						if(!spreadSlots.contains(slot.slotNumber) && spreadSlots.size() < spreadCount) {
							
							spreadSlots.add(slot.slotNumber);
							for(int slotNo : spreadSlots) {
								
								for(Object obj : container.inventorySlots.slots) {
									Slot currentSlot = (Slot)obj;
        							if(currentSlot.slotNumber == slotNo) {
        								controller.handleMouseClick(windowId, currentSlot.slotNumber, 0, false, player);
        								if(currentSlot.getHasStack()) {
        									controller.handleMouseClick(windowId, currentSlot.slotNumber, 0, false, player);
        								}
        								for(int i = 0; i < spreadCount / spreadSlots.size(); i++) {
        									controller.handleMouseClick(windowId, currentSlot.slotNumber, 1, false, player);
            							}
        							}
        						}
    						}
							
						}
					}
					
					
				}
				else if (collecting){
					if(lastSlotNo != slot.slotNumber) {
					if(player.inventory.getItemStack() != null && slot.getHasStack() 
    						&& slot.getStack().isItemEqual(player.inventory.getItemStack()) && !(slot instanceof SlotCrafting)) {
						
						if(!shiftClick && player.inventory.getItemStack().getMaxStackSize() > player.inventory.getItemStack().stackSize) {
							controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
    						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
						}
					}
					else if(player.inventory.getItemStack() == null && slot.slotNumber != lastSlotNo && lastSlotNo != -1 && !(slot instanceof SlotCrafting)) {
						controller.handleMouseClick(windowId, lastSlotNo, 0, false, player);
						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
						controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
					}
					
					}
					if(shiftClick && player.inventory.getItemStack() != null && slot.getHasStack() 
    						&& slot.getStack().isItemEqual(player.inventory.getItemStack()) && !(slot instanceof SlotCrafting)) {
						controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
					}
					lastSlotNo = slot.slotNumber;
				}
				else if(slot.getHasStack() && shiftClick && (player.inventory.getItemStack() == null
						|| slot.getStack().isItemEqual(player.inventory.getItemStack())) && !(slot instanceof SlotCrafting)) {
					controller.handleMouseClick(windowId, slot.slotNumber, 0, true, player);
				}
			}
    	}
        if(counter > -1) counter--;
        if(Keyboard.isKeyDown(mc.gameSettings.keyBindDrop.keyCode) && !droppedItem && (counter == -1 || shiftClick) && player.inventory.getItemStack() == null  && slot.getHasStack()) {
        	counter = 20;
        	controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        	controller.handleMouseClick(windowId, -999, shiftClick ? 0 : 1, false, player);
        	controller.handleMouseClick(windowId, slot.slotNumber, 0, false, player);
        }
        	
        if(Keyboard.getEventKeyState())
        {
        	if(player.inventory.getItemStack() != null && Keyboard.getEventKey() == mc.gameSettings.keyBindDrop.keyCode && !droppedItem)
            {
        		droppedItem = true;
        		controller.handleMouseClick(windowId, -999, shiftClick ? 0 : 1, false, player);
            }
        }
        else {
        	if(Keyboard.getEventKey() == mc.gameSettings.keyBindDrop.keyCode)
            {
        		droppedItem = false;
            }
        }
        
        
	}
	else {
		lastSlotNo = -1;
		if(Mouse.isButtonDown(1)) {
			
		}
		if(!Mouse.isButtonDown(0)) {
			itemClickedOn = false;
			spreading = false;
			collecting = false;
			spreadSlots.clear();
			spreadCount = -1;
		}
	}
		if(spreadSlots.size() > 1) {
			GL11.glPushMatrix();
			Graphics.preRender();
			GL11.glTranslatef((container.width - mod_BetaTweaks.xSize(container)) / 2, (container.height - mod_BetaTweaks.ySize(container)) / 2, 0.0F);
			for(int slotNo : spreadSlots) {
				
				for(Object obj : container.inventorySlots.slots) {
					Slot currentSlot = (Slot)obj;
					if(currentSlot.slotNumber == slotNo && currentSlot != slot) {
						Graphics.drawSlotBackground(currentSlot.xDisplayPosition, currentSlot.yDisplayPosition, 0x80ffffff);
				        Graphics.drawItemStack(currentSlot.xDisplayPosition, currentSlot.yDisplayPosition, currentSlot.getStack());
					}
				}
			}
			InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
	        if(inventoryplayer.getItemStack() != null)
	        {
	        	int k = (container.width - mod_BetaTweaks.xSize(container)) / 2;
	            int l = (container.height - mod_BetaTweaks.ySize(container)) / 2;
	            GL11.glTranslatef(0.0F, 0.0F, 32F);
	            Graphics.drawItemStack(x - k - 8, y - l - 8, inventoryplayer.getItemStack());
	            GL11.glTranslatef(0.0F, 0.0F, -32F);
	        }
    		Graphics.postRender();
    		GL11.glPopMatrix();
		}
	}
	
	public static void onTick(Minecraft mc) {
		if(Mouse.isButtonDown(1) && mc.currentScreen == null) {
			if(!rmbHeld) {
				rmbHeld = true;
				if(mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemArmor) {
					int armourSlotID = ((ItemArmor)mc.thePlayer.inventory.getCurrentItem().getItem()).armorType + 5;
					int heldSlotID = mc.thePlayer.inventorySlots.slots.size() - (9 - mc.thePlayer.inventory.currentItem);
					GuiInventory inv = new GuiInventory(mc.thePlayer);
					mc.playerController.handleMouseClick(inv.inventorySlots.windowId, heldSlotID, 0, false, mc.thePlayer);
					mc.playerController.handleMouseClick(inv.inventorySlots.windowId, armourSlotID, 0, false, mc.thePlayer);
					mc.playerController.handleMouseClick(inv.inventorySlots.windowId, heldSlotID, 0, false, mc.thePlayer);
				}
			}
			
		}
		else {
			rmbHeld = false;
		}
	}
}
