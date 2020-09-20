package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class CheatCommand extends SubCommand {

    CheatCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "cheat", false);
    }


    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("slimefun.cheat.items")) {
                SlimefunGuide.openCheatMenu((Player) sender);
            } else {
                SlimefunPlugin.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }

}
