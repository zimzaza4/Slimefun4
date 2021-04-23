package io.github.starwishsama.utils;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maxgamer.quickshop.api.QuickShopAPI;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 保护插件权限检查器
 *
 * @author StarWishsama
 */
public class IntegrationHelper implements Listener {
    private static boolean resInstalled;
    private static boolean qsInstalled;

    @EventHandler
    public void onAndroidMine(AndroidMineEvent e) {
        if (e != null) {
            Player p = Bukkit.getPlayer(getOwnerFromJson(BlockStorage.getBlockInfoAsJson(e.getAndroid().getBlock())));

            if (!checkPermission(p, e.getBlock(), ProtectableAction.BREAK_BLOCK)) {
                e.setCancelled(true);
                SlimefunPlugin.getLocalization().sendMessage(p, "android.no-permission");
            }
        }
    }

    public IntegrationHelper(SlimefunPlugin plugin) {
        resInstalled = plugin.getServer().getPluginManager().getPlugin("Residence") != null;
        qsInstalled = plugin.getServer().getPluginManager().getPlugin("QuickShop") != null;

        if (!qsInstalled) {
            plugin.getLogger().log(Level.WARNING, "未检测到 Quickshop-Reremake, 相关功能将自动关闭");
        }

        if (!resInstalled) {
            plugin.getLogger().log(Level.WARNING, "未检测到领地插件, 相关功能将自动关闭");
            return;
        }

        plugin.getLogger().log(Level.INFO, "检测到领地插件, 相关功能已开启");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 检查是否可以在领地/地皮内破坏/交互方块
     *
     * @param p      玩家
     * @param block  被破坏的方块
     * @param action 交互类型
     * @return 是否可以破坏
     */
    public static boolean checkPermission(OfflinePlayer p, Block block, ProtectableAction action) {
      if (!resInstalled || p == null || block == null || p.isOp()) {
        return true;
      }

      ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(block.getLocation());

      if (res != null) {
        if (res.getOwnerUUID() == p.getUniqueId()) {
          return true;
        }

        ResidencePermissions perms = res.getPermissions();

        if (perms != null) {
          ResidencePlayer rp = new ResidencePlayer(p);

          if (perms.playerHas(rp, Flags.admin, true) || !action.isBlockAction()) {
            return true;
          }

          switch (action) {
            case BREAK_BLOCK:
            case INTERACT_BLOCK:
              // 领地已支持 Slimefun
              // 详见 https://github.com/Zrips/Residence/blob/master/src/com/bekvon/bukkit/residence/slimeFun/SlimeFunResidenceModule.java
              return SlimefunPlugin.getProtectionManager()
                      .hasPermission(p, block.getLocation(), action);
            case PLACE_BLOCK:
              // move 是为了机器人而检查的, 防止机器人跑进别人领地然后还出不来
              return perms.playerHas(rp, Flags.place, true)
                      || perms.playerHas(rp, Flags.build, true)
                      || !perms.playerHas(rp, Flags.move, true);
          }
        }
      }
      return true;
    }

    public static UUID getOwnerFromJson(String json) {
        if (json != null) {
            JsonElement element = new JsonParser().parse(json);
            if (!element.isJsonNull()) {
                JsonObject object = element.getAsJsonObject();
                return UUID.fromString(object.get("owner").getAsString());
            }
        }
        return null;
    }

    public static boolean checkForQuickShop(@Nonnull Location l) {
        if (!qsInstalled) {
            return false;
        }

        try {
            return QuickShopAPI.getShopAPI().getShopWithCaching(l) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
