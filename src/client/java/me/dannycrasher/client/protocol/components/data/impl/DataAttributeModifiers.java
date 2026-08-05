package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class DataAttributeModifiers implements DataComponent {
	private final int modifierCount;
	private final boolean showInTooltip;

	public DataAttributeModifiers(int modifierCount, boolean showInTooltip) {
		this.modifierCount = modifierCount;
		this.showInTooltip = showInTooltip;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 12, 767, 12,
				768, 13, 769, 13,
				770, 13
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, modifierCount);
		for (int i = 0; i < modifierCount; i++) {
			// Holder<Attribute> registry id
			PacketCodec.writeVarInt(buf, 1 + (i % 20));
			// AttributeModifier: ResourceLocation id, double amount, Operation id
			PacketCodec.writeUtf(buf, "dannycrasher:mod_" + i);
			buf.writeDouble(Double.MAX_VALUE);
			PacketCodec.writeVarInt(buf, i % 3);
			// EquipmentSlotGroup id
			PacketCodec.writeVarInt(buf, i % 10);
		}
		buf.writeBoolean(showInTooltip);
	}
}
