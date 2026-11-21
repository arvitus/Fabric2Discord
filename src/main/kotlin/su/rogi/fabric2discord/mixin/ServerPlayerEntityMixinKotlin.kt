package su.rogi.fabric2discord.mixin

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.CombatTracker
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.utils.MessageUtils

object ServerPlayerEntityMixinKotlin {
    fun onDeath(player: ServerPlayer, source: DamageSource, tracker: CombatTracker) {
        if (!Configs.MESSAGES.entries.player.died.enabled) return

        val replacements = hashMapOf<String, String>()
        replacements["death_message"] = tracker.deathMessage.string
        replacements["death_by"] = source.msgId

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.DEATHS)) {
            Configs.MESSAGES.entries.player.died.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(replacements, player))
            }
        }
    }

    fun worldChanged(player: ServerPlayer, origin: ServerLevel) {
        if (!Configs.MESSAGES.entries.player.teleported.enabled) return

        val replacements = hashMapOf<String, String>()
        replacements["world_origin"] = origin.dimension().identifier().toString()
        replacements["world_origin_id"] = origin.dimension().identifier().path

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.TELEPORTS)) {
            Configs.MESSAGES.entries.player.teleported.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(replacements, player))
            }
        }
    }
}