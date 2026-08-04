package me.dannycrasher.client.protocol.packets;

import io.netty.buffer.ByteBuf;

public interface Packet {
    void write(ByteBuf buf);
}
