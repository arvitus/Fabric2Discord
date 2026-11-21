package su.rogi.fabric2discord.mixins;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.rogi.fabric2discord.mixin.PlayerAdvancementTrackerMixinKotlin;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void award(AdvancementHolder holder, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Advancement advancement = holder.value();
        if (advancement.display().isPresent() && advancement.display().get().announceToChat()) {
            boolean announce = player.level().getGameRules().get(GameRules.SHOW_ADVANCEMENT_MESSAGES);
            PlayerAdvancementTrackerMixinKotlin.INSTANCE.grantCriterion(player, advancement, announce);
        }
    }
}
