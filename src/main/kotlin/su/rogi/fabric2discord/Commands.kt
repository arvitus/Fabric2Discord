package su.rogi.fabric2discord

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands as McCommands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import su.rogi.fabric2discord.config.Configs

object Commands {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(McCommands.literal("f2d")
                .requires(McCommands.hasPermission(McCommands.LEVEL_OWNERS))
                .then(McCommands.literal("reload")
                    .executes(Commands::reload)
            ))
        })
    }

    @Throws(CommandSyntaxException::class)
    fun reload(context: CommandContext<CommandSourceStack>): Int {
        Configs.SETTINGS.load()
        Configs.MESSAGES.load()
        context.source.sendSuccess(
            { Component
                .literal("Configuration files reloaded")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x19422814)))
            },
            false
        )
        return 1
    }
}
