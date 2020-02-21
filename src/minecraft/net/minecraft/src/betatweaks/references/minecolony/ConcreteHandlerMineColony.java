package net.minecraft.src.betatweaks.references.minecolony;

import net.minecraft.src.GuiCitizen;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiHut;
import net.minecraft.src.betatweaks.references.HandlerMineColony;

public class ConcreteHandlerMineColony extends HandlerMineColony {

	@Override
	public boolean disallowDraggingShortcuts(GuiContainer container) {
		return container instanceof GuiHut || container instanceof GuiCitizen;
	}
	
	//The method below was used before the mod handlers were moved out of the root src package
	
	//Since each MineColonyGui.page is package-only, I would have either had to put this in the root package or used reflection
	//I don't want to do the first because that would ruin the point of having these handlers
	//The second option would decrease performance (Not sure of impact)
	
	//I would be happy to do one of these things if this solution is not adequate but for now this should be fine.
	
	//I am aware that this solution is technically a downgrade in functionality but I'd rather have this clean solution cause I am an idiot
	/*
	public boolean allowDraggingShortcuts(GuiContainer container) {
		if(container instanceof GuiHut && ((GuiHut)container).page != 0) {
			return true;
		}
		else if(container instanceof GuiCitizen && ((GuiCitizen)container).page != 0) {
			return true;
		}
		return false;
	}
	*/
}
