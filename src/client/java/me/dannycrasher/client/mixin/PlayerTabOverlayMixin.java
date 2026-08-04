package me.dannycrasher.client.mixin;

import me.dannycrasher.client.chat.ChatHelper;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void dannyCrasher$injectTabListHeaderFooter(GuiGraphics graphics, int screenWidth, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
        PlayerTabOverlay self = (PlayerTabOverlay) (Object) this;

        self.setHeader(ChatHelper.format("\n \n"
                + "  <gradient:#4facfe:#00f2fe>Danny Crasher <dark_gray>| <white>Crash Toolkit  \n"
                + "  <white>by <aqua>danny  "
                + "\n \n"
        ));

        self.setFooter(ChatHelper.format("\n \n"
                + "  <gradient:#4facfe:#00f2fe>⬇ DOWNLOAD DANNY CRASHER ⬇  \n"
                + "  <gray>github.com/Danny42893798473289/dannycrasher  "
                + "\n \n"
        ));
    }
}
