package io.github.starwishsama.utils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

/**
 * @author Nameless
 */
public class VaultHelper {
    private static Economy econ = null;

    public static void register() {
        if (SlimefunPlugin.instance() != null) {
            if (SlimefunPlugin.instance().getServer().getPluginManager().isPluginEnabled("Vault")) {
                RegisteredServiceProvider<Economy> rsp = SlimefunPlugin.instance().getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    SlimefunPlugin.logger().log(Level.INFO, "成功接入 Vault");
                    econ = rsp.getProvider();
                } else {
                    SlimefunPlugin.logger().log(Level.WARNING, "无法接入 Vault. 如果你是 CMI 用户, 请至配置文件启用经济系统");
                }
            } else {
                SlimefunPlugin.logger().log(Level.WARNING, "无法接入 Vault. 你必须先安装 Vault!");
            }
        }
    }

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isUsable() {
        return econ != null && SlimefunPlugin.getRegistry().isUseMoneyUnlock();
    }
}
