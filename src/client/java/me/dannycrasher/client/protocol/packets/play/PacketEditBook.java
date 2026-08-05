package me.dannycrasher.client.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import me.dannycrasher.client.protocol.packets.Packet;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class PacketEditBook implements Packet {
	private static final Map<Integer, Integer> PACKET_IDS = Map.of(
			766, 20,
			767, 20,
			768, 22,
			769, 22,
			770, 22
	);

	private final int protocolId;
	private final int slot;
	private final List<String> pages;
	private final String title;

	public PacketEditBook(int protocolId, int slot, List<String> pages, String title) {
		this.protocolId = protocolId;
		this.slot = slot;
		this.pages = pages;
		this.title = title;
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, PACKET_IDS.getOrDefault(protocolId, 22));
		PacketCodec.writeVarInt(buf, slot);
		PacketCodec.writeVarInt(buf, pages.size());
		for (String page : pages) {
			PacketCodec.writeUtf(buf, page);
		}
		PacketCodec.writeNullable(buf, title, PacketCodec::writeUtf);
	}
}
