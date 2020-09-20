package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

class HelpCommand extends SubCommand {

    HelpCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "help", false);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        cmd.sendHelp(sender);
    }

}
