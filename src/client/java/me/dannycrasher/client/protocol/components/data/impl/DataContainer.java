package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class DataContainer implements DataComponent {
	private final List<ItemStack> items;

	public DataContainer(List<ItemStack> items) {
		this.items = items;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 52, 767, 52,
				768, 62, 769, 62,
				770, 62
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, items.size());
		items.forEach(item -> ComponentsCodec.writeItem(buf, item));
	}
}
