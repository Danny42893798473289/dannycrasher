package me.dannycrasher.client.protocol.components.objects;

import me.dannycrasher.client.protocol.components.data.DataComponents;

public record ItemStack(int id, int amount, DataComponents dataComponents) {
}
