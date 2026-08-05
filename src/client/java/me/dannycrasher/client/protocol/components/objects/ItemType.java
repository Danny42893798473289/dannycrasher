package me.dannycrasher.client.protocol.components.objects;

import java.util.Map;

public enum ItemType {
	STONE(Map.of(766, 1, 767, 1, 768, 1, 769, 1, 770, 1)),
	BUNDLE(Map.of(766, 930, 767, 930, 768, 954, 769, 963, 770, 974)),
	SHULKER_BOX(Map.of(766, 512, 767, 512, 768, 536, 769, 545, 770, 556)),
	WRITABLE_BOOK(Map.of(766, 1091, 767, 1091, 768, 1132, 769, 1141, 770, 1152)),
	WRITTEN_BOOK(Map.of(766, 1092, 767, 1092, 768, 1133, 769, 1142, 770, 1153)),
	PLAYER_HEAD(Map.of(766, 1106, 767, 1106, 768, 1147, 769, 1156, 770, 1167)),
	FIREWORK_ROCKET(Map.of(766, 1113, 767, 1113, 768, 1154, 769, 1163, 770, 1174)),
	TROPICAL_FISH_BUCKET(Map.of(766, 918, 767, 918, 768, 942, 769, 951, 770, 962)),
	CREEPER_SPAWN_EGG(Map.of(766, 1021, 767, 1021, 768, 1062, 769, 1071, 770, 1082)),
	DIAMOND_SWORD(Map.of(766, 819, 767, 819, 768, 860, 769, 869, 770, 880));

	private final Map<Integer, Integer> byVersion;

	ItemType(Map<Integer, Integer> byVersion) {
		this.byVersion = byVersion;
	}

	public int getId(int protocolVersion) {
		return byVersion.getOrDefault(protocolVersion, 0);
	}
}
