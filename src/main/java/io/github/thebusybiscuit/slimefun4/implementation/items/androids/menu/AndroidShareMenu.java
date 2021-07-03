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
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class AndroidShareMenu {
  private static final int startSlot = 9;
  private static final int perPageDisplay = 45;
  private static final Gson gson = new Gson();
  private static final String key = "share-users";

  private AndroidShareMenu() {}

  public static void openShareMenu(@Nonnull Player p, @Nonnull Block b, int page) {
    Validate.notNull(p, "The player cannot be null!");
    Validate.notNull(b, "The android block cannot be null!");

    ChestMenu menu = new ChestMenu(SlimefunPlugin.instance(), "&b共享权限管理");

    menu.setEmptySlotsClickable(false);

    List<UUID> users = getTrustedUsers(b);

    int pages = Math.max(1, users.size() / 36);

    // Draw background

    for (int i = 0; i < 9; i++) {
      menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
      menu.addMenuClickHandler(i, (pl, slot, item, cursor, action) -> false);
    }

    for (int i = 45; i < 54; i++) {
      menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
      menu.addMenuClickHandler(i, (pl, slot, item, cursor, action) -> false);
    }

    menu.addItem(0, new CustomItem(HeadTexture.SCRIPT_UP.getAsItemStack(), "&a添加信任玩家", ""));
    menu.addMenuClickHandler(0, (p1, slot, item, cursor, action) -> {
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
      List<UUID> displayUsers = users.subList(page, Math.min(users.size() - 1, perPageDisplay));
      for (int index = 0; index < displayUsers.size(); index++) {
        int slot = index + startSlot;
        OfflinePlayer current = Bukkit.getOfflinePlayer(displayUsers.get(index));
        menu.addItem(slot, new CustomItem(SkullItem.fromPlayer(current), "&b" + current.getName(), "&c左键 删除玩家"));
        menu.addMenuClickHandler(slot, (p1, slot1, item, cursor, action) -> {
          if (action.isLeftClick()) {
            removePlayer(p1, current, b, users);
          }

          return false;
        });
      }
    }

    menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
    menu.addMenuClickHandler(46, (pl, slot, item, cursor, action) -> {
      int next = page - 1;
      if (next < 1) next = pages;
      if (next != page) {
        openShareMenu(p, b, next);
      }
      return false;
    });

    menu.addItem(50, ChestMenuUtils.getNextButton(p, page, pages));
    menu.addMenuClickHandler(50, (pl, slot, item, cursor, action) -> {
      int next = page + 1;
      if (next > pages) {
        next = 1;
      }
      if (next != page) {
        openShareMenu(p, b, next);
      }
      return false;
    });

    menu.open(p);
  }

  public static String generateEmptyList() {
    return gson.toJson(new ArrayList<UUID>());
  }

  private static void addPlayer(@Nonnull Player owner, @Nonnull OfflinePlayer p, @Nonnull Block android, @Nonnull List<UUID> users) {
    users.add(p.getUniqueId());
    owner.sendMessage("添加玩家 " + p.getName() + " 成功");

    BlockStorage.addBlockInfo(android, key, gson.toJson(users));
  }

  private static void removePlayer(@Nonnull Player owner, @Nonnull OfflinePlayer p, @Nonnull Block android, @Nonnull List<UUID> users) {
    boolean result = users.remove(p.getUniqueId());
    owner.sendMessage("删除玩家 " + p.getName() + " " + (result ? "成功" : "失败"));

    BlockStorage.addBlockInfo(android, key, gson.toJson(users));
  }

  public static List<UUID> getTrustedUsers(@Nonnull Block b) {
    String info = BlockStorage.getBlockInfoAsJson(b);

    JsonElement element = new JsonParser().parse(info);

    if (element.isJsonObject()) {
      return gson.fromJson(element.getAsJsonObject().get(key).getAsString(), new TypeToken<List<UUID>>(){}.getType());
    } else {
      return Collections.emptyList();
    }
  }
}
