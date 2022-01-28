package ren.natsuyuk1.slimefunextra;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author StarWishsama
 */
public class DeprecationChecker {
    private static final List<String> unrecommendedPlugins = Collections.unmodifiableList(Arrays.asList("SlimeFunBookFix", "SlimefunBugFixer", "WipeGhostSlimefunBlock", "SlimefunFix"));

    public static void checkDeprecation(JavaPlugin plugin) {
        List<Plugin> result = Arrays.stream(plugin.getServer().getPluginManager().getPlugins()).filter(p -> unrecommendedPlugins.contains(p.getName())).collect(Collectors.toList());

        if (!result.isEmpty()) {
            plugin.getLogger().warning("你正在使用过时的修复插件 " + result);
            plugin.getLogger().warning("这些插件仅支持 1.12 以下版本或者官方版本已经修复这些问题");
            plugin.getLogger().warning("如方块放置机刷物品, 快捷键刷物品等");
        }
    }
}
