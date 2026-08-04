package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import net.minecraft.nbt.Tag;

import java.util.Map;

public class DataItemName implements DataComponent {
	private final Tag name;

	public DataItemName(Tag name) {
		this.name = name;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 6, 767, 6,
				768, 6, 769, 6,
				770, 6
		);
	}

	@Override
	public void write(ByteBuf buf) {
		ComponentsCodec.writeTag(buf, this.name);
	}
}
