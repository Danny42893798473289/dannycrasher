package me.dannycrasher.client.mixin;

import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.gui.menu.MenuRedirectState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "updateTitle", at = @At("HEAD"), cancellable = true)
    private void setCustomTitle(CallbackInfo callbackInfo) {
        Minecraft client = Minecraft.getInstance();

        client.getWindow().setTitle("Danny Crasher 1.21.4 (Version: " + DannyCrasher.VERSION + ") DOWNLOAD: github.com/Danny42893798473289/dannycrasher");

        callbackInfo.cancel();
    }

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen replaceVanillaMenuScreens(Screen screen) {
        return MenuRedirectState.replaceVanillaMenuScreen(screen);
    }
}
