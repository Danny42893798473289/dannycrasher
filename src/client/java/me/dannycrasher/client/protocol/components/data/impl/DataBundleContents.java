package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import me.dannycrasher.client.protocol.components.ComponentsCodec;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.packets.PacketCodec;

import java.util.List;
import java.util.Map;

public class DataBundleContents implements DataComponent {
	private final List<ItemStack> itemStackList;

	public DataBundleContents(List<ItemStack> itemStackList) {
		this.itemStackList = itemStackList;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 30, 767, 30,
				768, 40, 769, 40,
				770, 40
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, itemStackList.size());
		itemStackList.forEach(itemStack -> ComponentsCodec.writeItem(buf, itemStack));
	}
}
