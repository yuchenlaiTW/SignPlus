package tw.signplus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public final class SignPlusClient implements ClientModInitializer {
    public static final Map<String, BlockPos> WARPS = new ConcurrentHashMap<>();
    public static final Map<String, BlockPos> HOMES = new ConcurrentHashMap<>();
    private static long lastHandledClick = 0L;

    @Override public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("swarp").then(argument("name", word()).executes(ctx -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return 0;
                String name = getString(ctx, "name");
                WARPS.put(name.toLowerCase(), mc.player.blockPosition());
                mc.player.displayClientMessage(Component.literal("已設定本地傳送點：" + name), false);
                return 1;
            })));
            dispatcher.register(literal("shome").then(argument("name", word()).executes(ctx -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return 0;
                String name = getString(ctx, "name");
                HOMES.put(name.toLowerCase(), mc.player.blockPosition());
                mc.player.displayClientMessage(Component.literal("已設定本地家：" + name), false);
                return 1;
            })));
        });
    }

    public static boolean debounceClick() {
        long now = System.currentTimeMillis();
        if (now - lastHandledClick < 350L) return false;
        lastHandledClick = now;
        return true;
    }
    public static void runClientCommand(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.getConnection() != null) mc.getConnection().sendCommand(command);
    }
    public static void teleportToLocal(BlockPos pos) {
        if (pos != null) runClientCommand("tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }
}
