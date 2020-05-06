package betatweaks.mixin;

import betatweaks.Main;
import net.minecraft.src.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
    @Redirect(method = "handleKeyboardInput", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 0))
    private int toggleFullscreen() {
        if(Keyboard.getEventKey() == Main.INSTANCE.fullscreenToggle.keyCode) {
            return 87;
        }
        return 0;
    }
}
