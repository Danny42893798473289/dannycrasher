package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import net.minecraft.nbt.Tag;

public class DataBlockEntityData implements DataComponent {
	private final Tag data;

	public DataBlockEntityData(Tag data) {
		this.data = data;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 39, 767, 39,
				768, 49, 769, 49,
				770, 49
		);
	}

	@Override
	public void write(ByteBuf buf) {
		ComponentsCodec.writeTag(buf, this.data);
	}
}
