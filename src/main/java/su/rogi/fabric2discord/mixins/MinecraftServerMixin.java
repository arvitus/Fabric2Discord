package su.rogi.fabric2discord.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.MinecraftServerMixinKotlin;

import java.util.Timer;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    Timer timer = new Timer();

    @Shadow
    private PlayerList playerList;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {
        MinecraftServerMixinKotlin.INSTANCE.afterSetupServer(playerList, timer);
    }

    @Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/network/ServerConnectionListener;stop()V", ordinal = 0), method = "stopServer")
    private void afterShutdownServer(CallbackInfo info) {
        MinecraftServerMixinKotlin.INSTANCE.afterShutdownServer(timer);
    }
}
