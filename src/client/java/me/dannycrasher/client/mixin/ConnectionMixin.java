package me.dannycrasher.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.dannycrasher.client.DannyCrasher;
import me.dannycrasher.client.chat.ChatHelper;
import me.dannycrasher.client.protocol.Protocol;
import net.fabricmc.fabric.impl.networking.RegistrationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(Connection.class)
public class ConnectionMixin {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "DannyCrasher-ChannelInfo");
        thread.setDaemon(true);
        return thread;
    });

    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void onPacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        DannyCrasher.getHudManager().markPacketReceived();

        if (!(packet instanceof ClientboundCustomPayloadPacket p)) return;
        if (!(p.payload() instanceof RegistrationPayload rp)) return;

        SCHEDULER.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                ChatHelper.send("<aqua>Registered payload channels:");

                for (var c : rp.channels()) {
                    ChatHelper.send(c.toString());
                }

            });
        }, 2, TimeUnit.SECONDS);
    }


    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"))
    private void dannyCrasher$clearQueuedPacketsOnDisconnect(DisconnectionDetails details, CallbackInfo callbackInfo) {
        Protocol.disableSendingAndClearQueue();
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void dannyCrasher$clearQueuedPacketsOnException(ChannelHandlerContext context, Throwable throwable, CallbackInfo callbackInfo) {
        Protocol.disableSendingAndClearQueue();
    }

    @Inject(method = "channelInactive", at = @At("HEAD"))
    private void dannyCrasher$clearQueuedPacketsOnInactive(ChannelHandlerContext context, CallbackInfo callbackInfo) {
        Protocol.disableSendingAndClearQueue();
    }
}