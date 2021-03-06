package net.minecraft.src.betatweaks.gui;

import net.minecraft.src.GuiAchievement;
import net.minecraft.src.betatweaks.Utils;

public class GuiAchievementNull extends GuiAchievement
{

    public GuiAchievementNull()
    {
    	super(null);
    }

    @Override
    public void updateAchievementWindow()
    {
    	//Do nothing
    }
    
    private static final GuiAchievement vanilla = Utils.MC.guiAchievement; 
    private static final GuiAchievementNull hidden = new GuiAchievementNull();
    
    public static void setVisible(boolean isVisible) {
    	Utils.MC.guiAchievement = isVisible ? vanilla : hidden;
    }
}
