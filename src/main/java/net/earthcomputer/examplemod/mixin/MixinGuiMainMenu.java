package net.earthcomputer.examplemod.mixin;

import net.minecraft.src.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {
    @Shadow private String splashText;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void afterInitGui(CallbackInfo ci) {
        this.splashText = "Fabric <3";
    }
}
