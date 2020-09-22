package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import javax.annotation.Nonnull;

class ReloadCommand extends SubCommand {
    ReloadCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "reload", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("slimefun.command.reload") || sender instanceof ConsoleCommandSender) {
            SlimefunPlugin.getCfg().reload();
            SlimefunPlugin.getItemCfg().reload();
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.reloaded", true);
        } else {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }
}
