package su.rogi.fabric2discord.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.ServerPlayerEntityMixinKotlin;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        ServerPlayerEntityMixinKotlin.INSTANCE.onDeath(self, source, self.getCombatTracker());
    }

    @Inject(method = "triggerDimensionChangeTriggers", at = @At("TAIL"))
    private void worldChanged(ServerLevel origin, CallbackInfo ci) {
        ServerPlayerEntityMixinKotlin.INSTANCE.worldChanged((ServerPlayer) (Object) this, origin);
    }
}
