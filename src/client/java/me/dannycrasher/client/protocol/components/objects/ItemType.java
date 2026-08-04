package me.dannycrasher.client.protocol.components.objects;

import java.util.Map;

public enum ItemType {
    STONE(Map.of(766, 1, 767, 1, 768, 1, 769, 1, 770, 1)),
    BUNDLE(Map.of(766, 930, 767, 930, 768, 954, 769, 963, 770, 974)),
    WRITABLE_BOOK(Map.of(766, 1091, 767, 1091, 768, 1132, 769, 1141, 770, 1152)),
    WRITTEN_BOOK(Map.of(766, 1092, 767, 1092, 768, 1133, 769, 1142, 770, 1153));

    private final Map<Integer, Integer> byVersion;

    ItemType(Map<Integer, Integer> byVersion) {
        this.byVersion = byVersion;
    }

    public int getId(int protocolVersion) {
        return byVersion.getOrDefault(protocolVersion, 0);
    }
}
