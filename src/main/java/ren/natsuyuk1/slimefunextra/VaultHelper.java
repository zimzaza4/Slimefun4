package ren.natsuyuk1.slimefunextra;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import net.milkbowl.vault.economy.Economy;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * @author StarWishsama
 */
public class VaultHelper {
    private static Economy econ = null;

    public static void register(@Nonnull Slimefun plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            var rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
                plugin.getLogger().log(Level.INFO, "成功接入 Vault");
            } else {
                plugin.getLogger().log(Level.WARNING, "无法接入 Vault. 如果你是 CMI 用户, 请至配置文件启用经济系统");
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "无法接入 Vault. 你必须先安装 Vault!");
        }
    }

    public static void shutdown() {
        econ = null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isUsable() {
        return econ != null && Slimefun.getRegistry().isUseMoneyUnlock();
    }
}
