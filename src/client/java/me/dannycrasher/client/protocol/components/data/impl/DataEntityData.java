package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import net.minecraft.nbt.Tag;

public class DataEntityData implements DataComponent {
	private final Tag data;

	public DataEntityData(Tag data) {
		this.data = data;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 37, 767, 37,
				768, 47, 769, 47,
				770, 47
		);
	}

	@Override
	public void write(ByteBuf buf) {
		ComponentsCodec.writeTag(buf, this.data);
	}
}
