package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Map;

public class DataLore implements DataComponent {
	private final List<Tag> lines;

	public DataLore(List<Tag> lines) {
		this.lines = lines;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 7, 767, 7,
				768, 8, 769, 8,
				770, 8
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, this.lines.size());
		for (Tag line : this.lines) {
			ComponentsCodec.writeTag(buf, line);
		}
	}
}
