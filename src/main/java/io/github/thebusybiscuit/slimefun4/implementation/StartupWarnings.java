package io.github.thebusybiscuit.slimefun4.implementation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

/**
 * This class stores some startup warnings we occasionally need to print.
 * If you setup your server the recommended way, you are never going to see
 * any of these messages.
 * 
 * @author TheBusyBiscuit
 *
 */
final class StartupWarnings {

    private static final String BORDER = "****************************************************";
    private static final String PREFIX = "* ";

    private StartupWarnings() {}

    @ParametersAreNonnullByDefault
    static void discourageCSCoreLib(Logger logger) {
        logger.log(Level.WARNING, BORDER);
        logger.log(Level.WARNING, PREFIX + "似乎你还在使用 CS-CoreLib.");
        logger.log(Level.WARNING, PREFIX);
        logger.log(Level.WARNING, PREFIX + "Slimefun 自 2021/01/30 起");
        logger.log(Level.WARNING, PREFIX + "就不再强制依赖 CS-CoreLib 了");
        logger.log(Level.WARNING, PREFIX + "我们推荐你马上删除/卸载 CS-CoreLib");
        logger.log(Level.WARNING, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidMinecraftVersion(Logger logger, int majorVersion, String slimefunVersion) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun 未被正确安装!");
        logger.log(Level.SEVERE, PREFIX + "你正在使用不支持的 Minecraft 版本!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "你正在使用 Minecraft 1.{0}.x", majorVersion);
        logger.log(Level.SEVERE, PREFIX + "但 Slimefun {0} 只支持", slimefunVersion);
        logger.log(Level.SEVERE, PREFIX + "Minecraft {0}", String.join(" / ", Slimefun.getSupportedVersions()));
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidServerSoftware(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun 未被正确安装!");
        logger.log(Level.SEVERE, PREFIX + "我们不再支持 CraftBukkit 服务端了!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "Slimefun 需要你使用 Spigot, Paper");
        logger.log(Level.SEVERE, PREFIX + "或者 Spigot/Paper 分支的任意服务端.");
        logger.log(Level.SEVERE, PREFIX + "(我们推荐 Paper)");
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void oldJavaVersion(Logger logger) {
        int javaVersion = NumberUtils.getJavaVersion();

        logger.log(Level.WARNING, BORDER);
        logger.log(Level.WARNING, PREFIX + "你的 Java 版本 (Java {0}) 已过时.", javaVersion);
        logger.log(Level.WARNING, PREFIX);
        logger.log(Level.WARNING, PREFIX + "我们推荐你升级到 Java 16.");
        logger.log(Level.WARNING, PREFIX + "自 MC 1.17 开始 Minecraft 强制需要 Java 16,");
        logger.log(Level.WARNING, PREFIX + "我们也想尽快充分利用 Java 16 带来的新特性");
        logger.log(Level.WARNING, PREFIX + "因此 Slimefun 在不久的将来will also require Java 16 in");
        logger.log(Level.WARNING, PREFIX + "也会强制需要 Java 16, 所以请更新吧!");
        logger.log(Level.WARNING, BORDER);
    }

}
