package su.rogi.fabric2discord.utils

import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player

object PlaceholderUtils {
    fun registerPlaceholders() {
        registerPlayer("uuid") { it.stringUUID }
        registerPlayer("world") { it.level().dimension().identifier().toString() }
        registerPlayer("world_name") { it.level().dimension().identifier().path }
    }

    private fun registerPlayer(path: String, value: (Player) -> String) {
        Placeholders.registerCommon<Unit>(Identifier.fromNamespaceAndPath("player", path)) { handler, _ ->
            val player = handler.player()
            if (handler.hasPlayer() && player != null) PlaceholderResult.value(value(player))
            else PlaceholderResult.invalid("No player!")
        }
    }
}
