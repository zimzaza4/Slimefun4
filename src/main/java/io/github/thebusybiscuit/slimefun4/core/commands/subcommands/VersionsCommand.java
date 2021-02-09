package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import java.util.Collection;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.papermc.lib.PaperLib;

class VersionsCommand extends SubCommand {

    VersionsCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "versions", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("slimefun.command.versions") || sender instanceof ConsoleCommandSender) {
            // After all these years... Spigot still displays as "CraftBukkit"
            // so we will just fix this inconsistency for them :)
            String serverSoftware = PaperLib.isSpigot() && !PaperLib.isPaper() ? "Spigot" : Bukkit.getName();

            ComponentBuilder builder = new ComponentBuilder();
            builder.append("Slimefun 正在以下环境中运行:\n").color(ChatColor.GRAY)
                    .append(serverSoftware).color(ChatColor.GREEN).append(" " + Bukkit.getVersion() + '\n').color(ChatColor.DARK_GREEN)
                    .append("Slimefun").color(ChatColor.GREEN).append(" v" + SlimefunPlugin.getVersion() + '\n').color(ChatColor.DARK_GREEN);

            if (SlimefunPlugin.getMetricsService().getVersion() != null) {
                builder.append("Metrics 版本: ").color(ChatColor.GREEN)
                        .append("#" + SlimefunPlugin.getMetricsService().getVersion() + '\n').color(ChatColor.DARK_GREEN);
            }

            addJavaVersion(builder);

            if (SlimefunPlugin.getRegistry().isBackwardsCompatible()) {
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        "向后兼容将会极大影响服务器的性能!\n"
                                + "我们强烈推荐你关闭向后兼容,"
                                + "除非你的玩家还有旧版本的物品需要这个功能."
                ));

                builder.append("向后兼容已启用!\n").color(ChatColor.RED).event(hoverEvent);
            }

            builder.append("\n").event((HoverEvent) null);

            addPluginVersions(builder);

            sender.spigot().sendMessage(builder.create());
        } else {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }

    private void addJavaVersion(@Nonnull ComponentBuilder builder) {
        String javaVer = System.getProperty("java.version");
        if (javaVer.startsWith("1.")) {
            javaVer = javaVer.substring(2);
        }

        // If it's like 11.0.1.3 or 8.0_275
        if (javaVer.indexOf('.') != -1) {
            javaVer = javaVer.substring(0, javaVer.indexOf('.'));
        }
        int ver = Integer.parseInt(javaVer);

        if (ver < 11) {
            builder.append("Java " + ver).color(ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            "你应该使用不低于 Java 11 的环境! Paper 将在 MC 1.17 时停止对 Java 11 以下版本的支持."
                    )))
                    .append("\n")
                    .event((HoverEvent) null);
        } else {
            builder.append("Java " + ver + "\n").color(ChatColor.GREEN);
        }
    }

    private void addPluginVersions(@Nonnull ComponentBuilder builder) {
        Collection<Plugin> addons = SlimefunPlugin.getInstalledAddons();
        builder.append("已安装的扩展: ").color(ChatColor.GRAY)
                .append("(" + addons.size() + ")").color(ChatColor.DARK_GRAY);

        for (Plugin plugin : addons) {
            String version = plugin.getDescription().getVersion();

            if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                HoverEvent hoverEvent;
                ClickEvent clickEvent = null;
                if (plugin instanceof SlimefunAddon) {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            "作者: " + String.join(", ", plugin.getDescription().getAuthors())
                                    + "\n单击打开问题反馈页面"
                    ));
                    clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, ((SlimefunAddon) plugin).getBugTrackerURL());
                } else {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            "作者:  " + String.join(", ", plugin.getDescription().getAuthors())
                    ));
                }

                builder.append("\n  " + plugin.getName()).color(ChatColor.GREEN).event(hoverEvent).event(clickEvent)
                        .append(" v" + version).color(ChatColor.DARK_GREEN)
                        .append("").event((ClickEvent) null).event((HoverEvent) null);
            } else {
                HoverEvent hoverEvent;
                ClickEvent clickEvent = null;
                if (plugin instanceof SlimefunAddon) {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text("插件已被禁用, 可以看看后台是否有报错. 单击此处打开问题反馈页面"));
                    if (((SlimefunAddon) plugin).getBugTrackerURL() != null) {
                        clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, ((SlimefunAddon) plugin).getBugTrackerURL());
                    }
                } else {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text("插件已被禁用, 可以看看后台是否有报错"));
                }

                // We need to reset the hover event or it's added to all components
                builder.append("\n  " + plugin.getName()).color(ChatColor.RED).event(hoverEvent).event(clickEvent)
                        .append(" v" + version).color(ChatColor.DARK_RED)
                        .append("").event((ClickEvent) null).event((HoverEvent) null);
            }
        }
    }
}