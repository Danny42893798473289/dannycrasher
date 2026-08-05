package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import net.minecraft.nbt.Tag;

public class DataBucketEntityData implements DataComponent {
	private final Tag data;

	public DataBucketEntityData(Tag data) {
		this.data = data;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 38, 767, 38,
				768, 48, 769, 48,
				770, 48
		);
	}

	@Override
	public void write(ByteBuf buf) {
		ComponentsCodec.writeTag(buf, this.data);
	}
}
