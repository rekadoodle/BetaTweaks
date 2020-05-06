package betatweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiControls;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiControls.class)
public interface MixinGuiControlsAccessor {
    @Accessor(value = "parentScreen")
    public GuiScreen getParentScreen();

    @Accessor(value = "options")
    public GameSettings getGameSettings();
}
