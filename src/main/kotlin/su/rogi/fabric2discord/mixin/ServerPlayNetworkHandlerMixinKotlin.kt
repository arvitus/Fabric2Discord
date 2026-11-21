package su.rogi.fabric2discord.mixin

import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import su.rogi.fabric2discord.Fabric2Discord
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.config.components.ChannelCategory
import su.rogi.fabric2discord.kord.KordClient
import su.rogi.fabric2discord.utils.MessageUtils

object ServerPlayNetworkHandlerMixinKotlin {
    fun onPlayerMessageEvent(player: ServerPlayer, message: PlayerChatMessage) {
        if (!Configs.MESSAGES.entries.chat.message.enabled) return

        val embed = Configs.MESSAGES.entries.chat.message.getEmbed(hashMapOf(Pair("message", message.decoratedContent().string)), player)
        if (Configs.SETTINGS.entries.ids.getWebhooks() != null) {
            if (embed.author == null) {
                Fabric2Discord.logger.warn("Unable to send message via webhook because field [text.header/images.header] of embed (messages.chat.message) is missing!")
                return
            }
            KordClient.executeWebhook(
                embed.author!!.name!!,
                embed.author!!.icon!!,
                embed.description!!,
            )
            return
        }

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.GAME_CHAT)) {
            embeds = mutableListOf(embed)
        }
    }

    fun remove(player: ServerPlayer, reason: Component) {
        if (!Configs.MESSAGES.entries.player.left.enabled) return

        MessageUtils.sendDiscordMessage(Configs.SETTINGS.entries.ids.getByCategory(ChannelCategory.CONNECTIONS)) {
            Configs.MESSAGES.entries.player.left.let {
                suppressNotifications = it.silent
                embeds = mutableListOf(it.getEmbed(hashMapOf(Pair("leave_reason", reason.string)), player))
            }
        }
    }
}