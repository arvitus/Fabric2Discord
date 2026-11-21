package su.rogi.fabric2discord.mixins;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.ServerPlayNetworkHandlerMixinKotlin;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getPlayerList()Lnet/minecraft/server/players/PlayerList;"))
    private void onPlayerMessageEvent(PlayerChatMessage message, CallbackInfo ci) {
        ServerPlayNetworkHandlerMixinKotlin.INSTANCE.onPlayerMessageEvent(player, message);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void remove(DisconnectionDetails details, CallbackInfo ci) {
        ServerPlayNetworkHandlerMixinKotlin.INSTANCE.remove(player, details.reason());
    }
}
