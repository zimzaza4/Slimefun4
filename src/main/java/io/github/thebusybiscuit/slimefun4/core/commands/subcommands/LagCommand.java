package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import cn.zimzaza4.slimefun4.PlayerLag;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class LagCommand extends SubCommand {
    protected LagCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "lag", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (PlayerLag.isLowPerformanceMode(p)) {
                PlayerLag.setLowPerformanceMode(p, false);
                p.sendMessage("§e已关闭低性能模式");
            } else {
                PlayerLag.setLowPerformanceMode(p, true);
                p.sendMessage("§a已打开低性能模式");
            }
        }
    }
}
