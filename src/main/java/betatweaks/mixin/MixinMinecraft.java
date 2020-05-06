package betatweaks.mixin;

import betatweaks.GuiControlsScrollable;
import betatweaks.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiControls;
import net.minecraft.src.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "startGame", at = @At(value = "NEW", target = "net/minecraft/src/OpenGlCapsChecker"))
    private void init(CallbackInfo ci) {
        Main.INSTANCE.init();
    }

    @ModifyVariable(method = "displayGuiScreen", at = @At("HEAD"))
    private GuiScreen overrideGuiScreen(GuiScreen guiscreen) {
        if(Main.INSTANCE.overrideGuiControls && guiscreen instanceof GuiControls) {
            MixinGuiControlsAccessor oldgui = (MixinGuiControlsAccessor) guiscreen;
            return new GuiControlsScrollable(oldgui.getParentScreen(), oldgui.getGameSettings());
        }
        return guiscreen;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 1))
    private int toggleFullscreen() {
        if(Keyboard.getEventKey() == Main.INSTANCE.fullscreenToggle.keyCode) {
            return 87;
        }
        return 0;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 4))
    private int toggleIngameGui() {
        if(Keyboard.getEventKey() == Main.INSTANCE.guiIngameToggle.keyCode) {
            return 59;
        }
        return 0;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 5))
    private int toggleDebugInfo() {
        if(Keyboard.getEventKey() == Main.INSTANCE.debugIngameToggle.keyCode) {
            return 61;
        }
        return 0;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 6))
    private int toggleThirdPerson() {
        if(Keyboard.getEventKey() == Main.INSTANCE.thirdPersonToggle.keyCode) {
            return 63;
        }
        return 0;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventKey ()I", remap = false, ordinal = 7))
    private int toggleSmoothCam() {
        if(Keyboard.getEventKey() == Main.INSTANCE.smoothCamToggle.keyCode) {
            return 66;
        }
        return 0;
    }


}
