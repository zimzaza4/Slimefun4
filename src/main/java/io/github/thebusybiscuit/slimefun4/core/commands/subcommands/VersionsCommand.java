package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * This is our class for the /sf versions subcommand.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
class VersionsCommand extends SubCommand {

    VersionsCommand(SlimefunPlugin plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "versions", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("slimefun.command.versions") || sender instanceof ConsoleCommandSender) {
            /*
             * After all these years... Spigot still displays as "CraftBukkit".
             * so we will just fix this inconsistency for them :)
             */
            String serverSoftware = PaperLib.isSpigot() && !PaperLib.isPaper() ? "Spigot" : Bukkit.getName();
            ComponentBuilder builder = new ComponentBuilder();

            // @formatter:off
            builder.append("Slimefun 运行的服务器环境:\n")
                    .color(ChatColor.GRAY)
                    .append(serverSoftware)
                    .color(ChatColor.GREEN)
                    .append(" " + Bukkit.getVersion() + '\n')
                    .color(ChatColor.DARK_GREEN)
                    .append("Slimefun ")
                    .color(ChatColor.GREEN)
                    .append(SlimefunPlugin.getVersion() + '\n')
                    .color(ChatColor.DARK_GREEN);
            // @formatter:on

            if (SlimefunPlugin.getMetricsService().getVersion() != null) {
                // @formatter:off
                builder.append("Metrics-组件 ")
                        .color(ChatColor.GREEN)
                        .append("#" + SlimefunPlugin.getMetricsService().getVersion() + '\n')
                        .color(ChatColor.DARK_GREEN);
                // @formatter:on
            }

            addJavaVersion(builder);

            if (SlimefunPlugin.getRegistry().isBackwardsCompatible()) {
                // @formatter:off
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(
                        "向后兼容将会极大地影响服务器性能!\n" +
                                "我们推荐你关闭它, 除非服务器中还有" +
                                "旧版本中的 Slimefun 物品 (通常为 1.13 版本前的)."
                )});
                // @formatter:on

                builder.append("\n向后兼容已启用!\n").color(ChatColor.RED).event(hoverEvent);
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

        int version = Integer.parseInt(javaVer);

        if (version < 11) {
            // @formatter:off
            builder.append("Java " + version).color(ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(
                            "你使用的 Java 版本已过时!\n!" +
                                    "推荐你使用 Java 11 或更高版本.\n" +
                                    "Paper 将在 MC 1.17 发布时停止对 Java 11 以下版本的支持."
                    )}))
                    .append("\n")
                    .event((HoverEvent) null);
            // @formatter:on
        } else {
            builder.append("Java ").color(ChatColor.GREEN).append(version + "\n").color(ChatColor.DARK_GREEN);
        }
    }

    private void addPluginVersions(@Nonnull ComponentBuilder builder) {
        Collection<Plugin> addons = SlimefunPlugin.getInstalledAddons();

        if (addons.isEmpty()) {
            builder.append("没有安装任何附属插件").color(ChatColor.GRAY).italic(true);
            return;
        }

        builder.append("安装的附属插件: ").color(ChatColor.GRAY).append("(" + addons.size() + ")").color(ChatColor.DARK_GRAY);

        for (Plugin plugin : addons) {
            String version = plugin.getDescription().getVersion();

            HoverEvent hoverEvent = null;
            ClickEvent clickEvent = null;
            ChatColor primaryColor;
            ChatColor secondaryColor;

            if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                primaryColor = ChatColor.GREEN;
                secondaryColor = ChatColor.DARK_GREEN;
                String authors = String.join(", ", plugin.getDescription().getAuthors());

                if (plugin instanceof SlimefunAddon && ((SlimefunAddon) plugin).getBugTrackerURL() != null) {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(new ComponentBuilder()
                            .append("作者: ")
                            .append(authors)
                            .color(ChatColor.YELLOW)
                            .append("\n> 单击打开反馈页面")
                            .color(ChatColor.GOLD)
                            .create()
                    )});
                    // @formatter:on

                    clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, ((SlimefunAddon) plugin).getBugTrackerURL());
                } else {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(new ComponentBuilder()
                            .append("作者: ")
                            .append(authors)
                            .color(ChatColor.YELLOW)
                            .create()
                    )});
                    // @formatter:on
                }
            } else {
                primaryColor = ChatColor.RED;
                secondaryColor = ChatColor.DARK_RED;

                if (plugin instanceof SlimefunAddon && ((SlimefunAddon) plugin).getBugTrackerURL() != null) {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(new ComponentBuilder()
                            .append("此插件已被禁用.\n检查后台是否有报错.")
                            .color(ChatColor.RED)
                            .append("\n> 单击打开反馈页面")
                            .color(ChatColor.DARK_RED)
                            .create()
                    )});
                    // @formatter:on

                    SlimefunAddon addon = (SlimefunAddon) plugin;

                    if (addon.getBugTrackerURL() != null) {
                        clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, addon.getBugTrackerURL());
                    }
                } else {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("插件已被禁用. 可以看看后台是否有报错.")});
                }
            }

            // @formatter:off
            // We need to reset the hover event or it's added to all components
            builder.append("\n  " + plugin.getName())
                    .color(primaryColor)
                    .event(hoverEvent)
                    .event(clickEvent)
                    .append(" v" + version)
                    .color(secondaryColor)
                    .append("")
                    .event((ClickEvent) null)
                    .event((HoverEvent) null);
            // @formatter:on
        }
    }
}