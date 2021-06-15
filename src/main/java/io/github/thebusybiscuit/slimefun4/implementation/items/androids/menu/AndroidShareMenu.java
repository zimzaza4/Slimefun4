package io.github.thebusybiscuit.slimefun4.implementation.items.androids.menu;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.thebusybiscuit.cscorelib2.inventory.ChestMenu;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.cscorelib2.skull.SkullItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class AndroidShareMenu {
  private static int startSlot = 10;
  private static int perPageDisplay = 45;
  private static Gson gson = new Gson();
  private static String key = "share-users";

  private AndroidShareMenu() {}

  public static void openShareMenu(@Nonnull Player p, @Nonnull Block b, int page) {
    Validate.notNull(p, "The player cannot be null!");
    Validate.notNull(b, "The android block cannot be null!");

    ChestMenu menu = new ChestMenu(SlimefunPlugin.instance(), "&b共享权限管理");

    menu.setEmptySlotsClickable(false);

    List<UUID> users = getTrustedUsers(b);

    menu.addItem(1, new CustomItem(HeadTexture.SCRIPT_UP.getAsItemStack(), "&a添加信任玩家", ""));
    menu.addMenuClickHandler(1, (p1, slot, item, cursor, action) -> {
      p1.closeInventory();
      ChatUtils.awaitInput(p1, message -> {
        Player target = Bukkit.getPlayerExact(message);

        if (target == null) {
          p1.sendMessage("找不到指定玩家 " + message);
        } else {
          addPlayer(p1, target, b, users);
        }

      });
      return false;
    });

    if (!users.isEmpty()) {
      List<UUID> displayUsers = users.subList(0 + (page - 1), Math.min(users.size() - 1, perPageDisplay));
      for (int index = 0; index < displayUsers.size(); index++) {
        int slot = index + startSlot;
        OfflinePlayer current = Bukkit.getOfflinePlayer(displayUsers.get(index));
        menu.addItem(slot, new CustomItem(SkullItem.fromPlayer(current), "&b" + current.getName(), "&c左键 删除玩家"));
        menu.addMenuClickHandler(slot, (p1, slot1, item, cursor, action) -> {
          if (action.isLeftClick()) {
            removePlayer(p1, current.getPlayer(), b, users);
          }

          return false;
        });
      }
    }

    // @TODO 上一页下一页按钮

    menu.open(p);
  }

  private static void createMenu() {}

  public static String generateEmptyList() {
    return gson.toJson(new ArrayList<UUID>());
  }

  private static void addPlayer(@Nonnull Player owner, @Nonnull Player p, @Nonnull Block android, @Nonnull List<UUID> users) {
    boolean result = users.add(p.getUniqueId());
    owner.sendMessage("添加玩家 " + p.getDisplayName() + " " + (result ? "成功" : "失败"));

    BlockStorage.addBlockInfo(android, key, gson.toJson(users));
  }

  private static void removePlayer(@Nonnull Player owner, @Nonnull Player p, @Nonnull Block android, @Nonnull List<UUID> users) {
    boolean result = users.remove(p.getUniqueId());
    owner.sendMessage("删除玩家 " + p.getDisplayName() + " " + (result ? "成功" : "失败"));

    BlockStorage.addBlockInfo(android, key, gson.toJson(users));
  }

  private static List<UUID> getTrustedUsers(@Nonnull Block b) {
    String info = BlockStorage.getBlockInfoAsJson(b);

    JsonElement element = new JsonParser().parse(info);

    if (element.isJsonObject()) {
      return gson.fromJson(element.getAsJsonObject().get(key).getAsString(), new TypeToken<List<UUID>>(){}.getType());
    } else {
      return Collections.emptyList();
    }
  }
}
