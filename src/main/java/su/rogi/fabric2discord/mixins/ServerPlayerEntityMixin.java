package su.rogi.fabric2discord.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.rogi.fabric2discord.mixin.ServerPlayerEntityMixinKotlin;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract ServerWorld getEntityWorld();

    public ServerPlayerEntityMixin(ServerWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntityMixinKotlin.INSTANCE.onDeath(Objects.requireNonNull(this.getCommandSource(this.getEntityWorld()).getPlayer()), source, getDamageTracker());
    }

    @Inject(method = "worldChanged", at = @At("TAIL"))
    private void worldChanged(ServerWorld origin, CallbackInfo ci) {
        ServerPlayerEntityMixinKotlin.INSTANCE.worldChanged(Objects.requireNonNull(this.getCommandSource(this.getEntityWorld()).getPlayer()), origin);
    }
}