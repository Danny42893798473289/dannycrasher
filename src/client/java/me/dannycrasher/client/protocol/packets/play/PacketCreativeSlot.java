package me.dannycrasher.client.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.packets.Packet;
import me.dannycrasher.client.protocol.packets.PacketCodec;

import java.util.Map;

public class PacketCreativeSlot implements Packet {
	private static final Map<Integer, Integer> PACKET_IDS = Map.of(
			766, 50,
			767, 50,
			768, 52,
			769, 54,
			770, 54
	);

	private final int protocolId;
	private final short slot;
	private final ItemStack item;

	public PacketCreativeSlot(int protocolId, int slot, ItemStack item) {
		this.protocolId = protocolId;
		this.slot = (short) slot;
		this.item = item;
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, PACKET_IDS.getOrDefault(protocolId, 54));
		buf.writeShort(slot);
		ComponentsCodec.writeItem(buf, item);
	}
}
