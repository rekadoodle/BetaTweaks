package betatweaks;


import betatweaks.mixin.MixinMinecraft;
import betatweaks.mixin.MixinMinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.src.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final Main INSTANCE = new Main();
    public static final Minecraft MC = MixinMinecraftAccessor.getInstance();

    public KeyBinding fullscreenToggle = new KeyBinding("key.fullscreen", Keyboard.KEY_F11);
    public KeyBinding guiIngameToggle = new KeyBinding("key.ingamegui", Keyboard.KEY_F1);
    public KeyBinding debugIngameToggle = new KeyBinding("key.debug", Keyboard.KEY_F3);
    public KeyBinding thirdPersonToggle = new KeyBinding("key.thirdperson", Keyboard.KEY_F5);
    public KeyBinding smoothCamToggle = new KeyBinding("key.smoothcam", Keyboard.KEY_F8);

    public boolean overrideGuiControls = true;

    public void init() {
       MC.hideQuitButton = false;
       this.addKeyBind(fullscreenToggle);
       this.addKeyBind(guiIngameToggle);
       this.addKeyBind(debugIngameToggle);
       this.addKeyBind(thirdPersonToggle);
       this.addKeyBind(smoothCamToggle);

       ModLoaderUtils.AddLocalization("key.fullscreen", "Fullscreen");
       ModLoaderUtils.AddLocalization("key.ingamegui", "Cinema Mode");
        ModLoaderUtils.AddLocalization("key.debug", "Debug");
        ModLoaderUtils.AddLocalization("key.thirdperson", "Thirdperson");
        ModLoaderUtils.AddLocalization("key.smoothcam", "Smoothcam");

    }

    public void addKeyBind(KeyBinding keybind) {
        List<KeyBinding> keylist = new ArrayList<KeyBinding>(Arrays.asList(MC.gameSettings.keyBindings));
        keylist.add(keybind);
        MC.gameSettings.keyBindings = keylist.toArray(new KeyBinding[keylist.size()]);
        MC.gameSettings.loadOptions();
    }





}
