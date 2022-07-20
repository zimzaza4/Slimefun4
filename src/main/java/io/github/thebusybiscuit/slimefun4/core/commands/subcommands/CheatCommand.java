package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

class CheatCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    CheatCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "cheat", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (sender.hasPermission("slimefun.cheat.items")) {
                SlimefunGuide.openCheatMenu(player);
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }

}
