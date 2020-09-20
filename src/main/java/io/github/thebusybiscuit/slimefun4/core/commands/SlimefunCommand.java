package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.commands.subcommands.SlimefunSubCommands;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlimefunCommand implements CommandExecutor, Listener {

    private boolean registered = false;
    private final SlimefunPlugin plugin;
    private final List<SubCommand> commands = new LinkedList<>();
    private final Map<SubCommand, Integer> commandUsage = new HashMap<>();

    public SlimefunCommand(@Nonnull SlimefunPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Validate.isTrue(!registered, "Slimefun's subcommands have already been registered!");

        registered = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getCommand("slimefun").setExecutor(this);
        plugin.getCommand("slimefun").setTabCompleter(new SlimefunTabCompleter(this));

        commands.addAll(SlimefunSubCommands.getAllCommands(this));
    }

    @Nonnull
    public SlimefunPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns a heatmap of how often certain commands are used.
     *
     * @return A {@link Map} holding the amount of times each command was run
     */
    @Nonnull
    public Map<SubCommand, Integer> getCommandUsage() {
        return commandUsage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : commands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    command.recordUsage(commandUsage);
                    command.onExecute(sender, args);
                    return true;
                }
            }
        }

        sendHelp(sender);

        return true;
    }

    public void sendHelp(@Nonnull CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&aSlimefun &2v" + SlimefunPlugin.getVersion()));
        sender.sendMessage("");

        for (SubCommand cmd : commands) {
            if (!cmd.isHidden()) {
                sender.sendMessage(ChatColors.color("&3/sf " + cmd.getName() + " &b") + cmd.getDescription(sender));
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/help slimefun")) {
            sendHelp(e.getPlayer());
            e.setCancelled(true);
        }
    }

    @Nonnull
    public List<String> getSubCommandNames() {
        return commands.stream().map(SubCommand::getName).collect(Collectors.toList());
    }

}
