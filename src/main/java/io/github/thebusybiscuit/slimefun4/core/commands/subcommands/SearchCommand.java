package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

class SearchCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    SearchCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "search", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (sender.hasPermission("slimefun.command.search")) {
                if (args.length > 1) {
                    String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    PlayerProfile.get(player, profile -> SlimefunGuide.openSearch(profile, query, SlimefunGuideMode.SURVIVAL_MODE, true));
                } else {
                    Slimefun.getLocalization().sendMessage(sender, "messages.usage", true, msg -> msg.replace("%usage%", "/sf search <SearchTerm>"));
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }

}
