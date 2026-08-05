package me.dannycrasher.client.protocol.components.data.impl;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import me.dannycrasher.client.protocol.components.data.DataComponent;
import me.dannycrasher.client.protocol.packets.PacketCodec;

public class DataFireworks implements DataComponent {
	private final int flightDuration;
	private final List<Explosion> explosions;

	public DataFireworks(int flightDuration, List<Explosion> explosions) {
		this.flightDuration = flightDuration;
		this.explosions = explosions;
	}

	@Override
	public Map<Integer, Integer> getIds() {
		return Map.of(
				766, 46, 767, 46,
				768, 56, 769, 56,
				770, 56
		);
	}

	@Override
	public void write(ByteBuf buf) {
		PacketCodec.writeVarInt(buf, flightDuration);
		PacketCodec.writeVarInt(buf, explosions.size());
		for (Explosion explosion : explosions) {
			PacketCodec.writeVarInt(buf, explosion.shape());
			writeIntList(buf, explosion.colors());
			writeIntList(buf, explosion.fadeColors());
			buf.writeBoolean(explosion.hasTrail());
			buf.writeBoolean(explosion.hasTwinkle());
		}
	}

	private static void writeIntList(ByteBuf buf, int[] values) {
		PacketCodec.writeVarInt(buf, values.length);
		for (int value : values) {
			buf.writeInt(value);
		}
	}

	public record Explosion(int shape, int[] colors, int[] fadeColors, boolean hasTrail, boolean hasTwinkle) {
	}
}
