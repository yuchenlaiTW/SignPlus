package tw.signplus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class SignClickMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void signplus$onUseItemOn(net.minecraft.world.entity.player.Player player,
                                     InteractionHand hand, BlockHitResult hit,
                                     CallbackInfoReturnable<InteractionResult> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || player != mc.player) return;
        if (!SignPlusClient.debounceClick()) return;
        var state = mc.level.getBlockState(hit.getBlockPos());
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.SignBlock)
                && !(state.getBlock() instanceof net.minecraft.world.level.block.WallSignBlock)) return;
        if (!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof SignBlockEntity sign)) return;
        String[] lines = SignTextReader.read(sign);
        if (lines.length < 2) return;
        String type = lines[0].trim().toLowerCase();
        String value = lines[1].trim();
        if (type.equals("[warp]")) {
            BlockPos pos = SignPlusClient.WARPS.get(value.toLowerCase());
            if (pos != null) { SignPlusClient.teleportToLocal(pos); cir.setReturnValue(InteractionResult.SUCCESS); }
            else mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("找不到本地傳送點「" + value + "」。請先使用 /swarp " + value), false);
        } else if (type.equals("[home]")) {
            BlockPos pos = SignPlusClient.HOMES.get(value.toLowerCase());
            if (pos != null) { SignPlusClient.teleportToLocal(pos); cir.setReturnValue(InteractionResult.SUCCESS); }
            else mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("找不到本地家「" + value + "」。請先使用 /shome " + value), false);
        } else if (type.equals("[tp]")) {
            String[] parts = value.replace(",", " ").trim().split("\\s+");
            if (parts.length >= 3) {
                SignPlusClient.runClientCommand("tp " + parts[0] + " " + parts[1] + " " + parts[2]);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else if (type.equals("[https]")) {
            StringBuilder url = new StringBuilder(value);
            for (int i = 2; i < lines.length; i++) if (!lines[i].isBlank()) url.append(lines[i].trim());
            String address = url.toString().trim();
            if (address.startsWith("https://") || address.startsWith("http://")) {
                mc.setScreen(new net.minecraft.client.gui.screens.ConfirmLinkScreen(accepted -> {
                    if (accepted) net.minecraft.Util.getPlatform().openUri(address);
                    mc.setScreen(null);
                }, address, true));
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
