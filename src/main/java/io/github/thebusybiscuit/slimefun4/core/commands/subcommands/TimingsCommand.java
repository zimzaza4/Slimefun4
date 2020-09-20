package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

class TimingsCommand extends SubCommand {

    TimingsCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "timings", false);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (sender.hasPermission("slimefun.command.timings") || sender instanceof ConsoleCommandSender) {
            sender.sendMessage("请等待几秒钟... 结果马上就来!");
            SlimefunPlugin.getProfiler().requestSummary(sender);
        } else {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }

}
