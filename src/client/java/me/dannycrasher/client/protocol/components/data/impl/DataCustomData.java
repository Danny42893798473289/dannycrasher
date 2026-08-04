package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import net.minecraft.nbt.Tag;

import java.util.Map;

public class DataCustomData implements DataComponent {
	private final Tag data;

	public DataCustomData(Tag data) {
		this.data = data;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 0, 767, 0,
				768, 0, 769, 0,
				770, 0
		);
	}

	@Override
	public void write(ByteBuf buf) {
		ComponentsCodec.writeTag(buf, this.data);
	}
}
