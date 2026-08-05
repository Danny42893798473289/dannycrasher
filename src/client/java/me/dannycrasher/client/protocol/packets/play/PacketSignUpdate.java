package me.dannycrasher.client.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import me.dannycrasher.client.protocol.packets.Packet;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class PacketSignUpdate implements Packet {
	private static final Map<Integer, Integer> PACKET_IDS = Map.of(
			766, 53,
			767, 53,
			768, 55,
			769, 57,
			770, 58
	);

	private final int protocolId;
	private final long packedPos;
	private final boolean frontText;
	private final String[] lines;

	public PacketSignUpdate(int protocolId, long packedPos, boolean frontText, String line0, String line1, String line2, String line3) {
		this.protocolId = protocolId;
		this.packedPos = packedPos;
		this.frontText = frontText;
		this.lines = new String[]{line0, line1, line2, line3};
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, PACKET_IDS.getOrDefault(protocolId, 57));
		buf.writeLong(packedPos);
		buf.writeBoolean(frontText);
		for (String line : lines) {
			PacketCodec.writeUtf(buf, line);
		}
	}
}
