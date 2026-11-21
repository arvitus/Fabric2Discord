package su.rogi.fabric2discord.mixin

import net.minecraft.server.level.ServerPlayer
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.utils.MessageUtils

object PlayerManagerMixinKotlin {
    fun onPlayerConnect(player: ServerPlayer) {
        if (!Configs.MESSAGES.entries.player.joined.enabled) return
        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.CONNECTIONS)) {
            Configs.MESSAGES.entries.player.joined.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(player = player))
            }
        }
    }
}