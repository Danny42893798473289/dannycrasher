package me.dannycrasher.client.mixin;

import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.command.impl.PlayerListCommand;
import me.dannycrasher.client.command.impl.PluginListCommand;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
	private void dannyCrasher$handleClientCommand(String message, CallbackInfo callbackInfo) {
		if (DannyCrasher.getCommandManager().handleChatMessage(message)) {
			callbackInfo.cancel();
		}
	}
    @Inject(method = "handleCommandSuggestions", at = @At("HEAD"))
    private void dannyCrasher$handleCommandSuggestions(ClientboundCommandSuggestionsPacket packet, CallbackInfo ci) {
        PluginListCommand.handleSuggestions(packet);
        PlayerListCommand.handleSuggestions(packet);
    }
}
