package su.rogi.fabric2discord.utils

import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.util.Identifier

object PlaceholderUtils {
    fun registerPlaceholders() {
        Placeholders.register(Identifier.of("player", "uuid")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.uuidAsString)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        Placeholders.register(Identifier.of("player", "world")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.world.registryKey.value.toString())
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
        Placeholders.register(Identifier.of("player", "world_name")) { handler, _ ->
            if (handler.hasPlayer()) {
                return@register PlaceholderResult.value(handler.player!!.world.registryKey.value.path)
            } else {
                return@register PlaceholderResult.invalid("No player!")
            }
        }
    }
}