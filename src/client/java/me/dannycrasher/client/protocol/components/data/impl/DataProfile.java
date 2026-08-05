package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class DataProfile implements DataComponent {
	private final String name;
	private final UUID id;
	private final List<Property> properties;

	public DataProfile(String name, UUID id, List<Property> properties) {
		this.name = name;
		this.id = id;
		this.properties = properties;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 47, 767, 47,
				768, 57, 769, 57,
				770, 57
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeNullable(buf, name, PacketCodec::writeUtf);
		PacketCodec.writeNullable(buf, id, PacketCodec::writeUUID);
		PacketCodec.writeVarInt(buf, properties.size());
		for (Property property : properties) {
			PacketCodec.writeUtf(buf, property.name());
			PacketCodec.writeUtf(buf, property.value());
			PacketCodec.writeNullable(buf, property.signature(), PacketCodec::writeUtf);
		}
	}

	public record Property(String name, String value, String signature) {
		public Property(String name, String value) {
			this(name, value, null);
		}
	}
}
