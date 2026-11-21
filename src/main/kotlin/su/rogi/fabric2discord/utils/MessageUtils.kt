package su.rogi.fabric2discord.utils

import com.vdurmont.emoji.EmojiParser
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.WebhookMessageCreateBuilder
import eu.pb4.placeholders.api.Placeholders
import eu.pb4.placeholders.api.ServerPlaceholderContext
import eu.pb4.placeholders.api.parsers.NodeParser
import eu.pb4.placeholders.api.parsers.TagParser
import kotlinx.coroutines.launch
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.PlayerList
import net.minecraft.stats.Stats
import su.rogi.fabric2discord.Fabric2Discord
import su.rogi.fabric2discord.config.Configs
import su.rogi.fabric2discord.kord.KordClient

object MessageUtils {
    fun sendDiscordMessage(channels: List<Snowflake>?, builder: UserMessageCreateBuilder.() -> Unit) {
        if (channels.isNullOrEmpty()) return
        Fabric2Discord.scope.launch {
            for (channel in channels) {
                KordClient.kord.getChannelOf<TextChannel>(channel)!!.createMessage {
                    builder()
                }
            }
        }
    }

    fun sendMinecraftMessage(playerList: PlayerList, member: Member, message: () -> Component) {
        val replacements = hashMapOf(
            Pair("discord_username", member.username),
            Pair("discord_nickname", member.effectiveName),
            // TODO: implement colorful names for chat depending on role or profile customization
            // Pair("discord_color", member.accentColor)
        )
        val formattedMessage = checkNotNull(format(
            Configs.MESSAGES.entries.chat.format,
            tags = replacements,
            server = playerList.server
        )).copy().append(message.invoke())

        playerList.broadcastSystemMessage(formattedMessage, false)
    }

    fun WebhookMessageCreateBuilder.createWebhookMessage(username: String, avatar: String, message: String) {
        this.username = username
        this.avatarUrl = avatar
        this.content = message
    }

    fun format(message: String?, tags: HashMap<String, String>? = null, server: MinecraftServer? = null, player: ServerPlayer? = null): Component? {
        if (message == null) return null
        val text = if (player != null) message.replace("%player:playtime%", playtime(player)) else message
        // QUICK_TEXT_WITH_STF still understands the old <tag:arg> syntax, so existing configs keep working
        val node = TagParser.QUICK_TEXT_WITH_STF.parseNode(EmojiParser.parseToUnicode(replacer(text, tags)))
        val context = when {
            player != null -> ServerPlaceholderContext.of(player)
            // without a level, world placeholders only print "Missing world!"
            server != null -> ServerPlaceholderContext.of(server.overworld())
            else -> return node.toComponent()
        }
        return placeholderParser.parseComponent(node, context.asParserContext())
    }

    // most built-in placeholders (player:name etc.) are "common" ones since placeholder-api 3
    private val placeholderParser = NodeParser.merge(Placeholders.SERVER_PLACEHOLDER_PARSER, Placeholders.COMMON_PLACEHOLDER_PARSER)

    // placeholder-api 3.2.0 hands "" instead of null to argument-less placeholders, which makes its own
    // %player:playtime% always come out empty. Same output format as theirs, so configs look the same.
    private fun playtime(player: ServerPlayer): String {
        val seconds = player.stats.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) / 20L
        val d = seconds / 86400
        val h = seconds % 86400 / 3600
        val m = seconds % 3600 / 60
        val s = seconds % 60
        return when {
            d > 0 -> "${d}d${h}h${m}m${s}s"
            h > 0 -> "${h}h${m}m${s}s"
            m > 0 -> "${m}m${s}s"
            s > 0 -> "${s}s"
            else -> "---"
        }
    }

    private fun replacer(message: String, replacements: HashMap<String, String>? = null): String {
        if (replacements == null) return message
        var newMessage = message
        replacements.forEach { (key, value) -> newMessage = newMessage.replace("%${key}%", value) }
        return newMessage
    }

    fun toColor(hex: String): Color {
        val finalHex = hex.replace("#", "")
        return Color(Integer.valueOf(finalHex, 16))
    }

    fun convertDiscordTags(text: String): String {
        return text
            .replace(getMarkdownRegex("||", "\\|\\|"), "<obf>$2</obf>")
            .replace(getMarkdownRegex("**", "\\*\\*"), "<bold>$2</bold>")
            .replace(getMarkdownRegex("__", "__"), "<underline>$2</underline>")
            .replace(getMarkdownRegex("~~", "~~"), "<strikethrough>$2</strikethrough>")
            .replace(getMarkdownRegex("*", "\\*"), "<italic>$2</italic>")
    }

    /*
     * Parts of the code from StyledChat by Patbox
     * https://github.com/Patbox/StyledChat/blob/e8e792f7ff29b93efab2595fd094415226c8f3d4/src/main/java/eu/pb4/styledchat/StyledChatUtils.java
     */
    private fun getMarkdownRegex(base: String, sides: String): Regex {
        return Regex("($sides)(?<id>[^$base]+)($sides)")
    }
}