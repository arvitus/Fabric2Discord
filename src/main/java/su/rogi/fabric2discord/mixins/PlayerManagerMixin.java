package su.rogi.fabric2discord.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.PlayerManagerMixinKotlin;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        PlayerManagerMixinKotlin.INSTANCE.onPlayerConnect(player);
    }
}
