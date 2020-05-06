package betatweaks.mixin;

import net.minecraft.src.ThreadDownloadResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadDownloadResources.class)
public class MixinThreadDownloadResources {

    @Inject(method = "downloadAndInstallResource", at = @At("HEAD"), cancellable = true)
    private void noSpam(CallbackInfo info) {
        info.cancel();
    }
}
