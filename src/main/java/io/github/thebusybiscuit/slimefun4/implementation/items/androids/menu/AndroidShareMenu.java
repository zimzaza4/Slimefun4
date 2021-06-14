package io.github.thebusybiscuit.slimefun4.implementation.items.androids.menu;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.thebusybiscuit.cscorelib2.inventory.ChestMenu;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.cscorelib2.skull.SkullItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
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

    menu.addItem(1, new CustomItem(HeadTexture.SCRIPT_UP.getAsItemStack(), "", ""));
    menu.addMenuClickHandler(1, (p1, slot, item, cursor, action) -> {
      // @TODO
      return false;
    });

    List<UUID> users = getTrustedUsers(b);

    if (!users.isEmpty()) {
      List<UUID> displayUsers = users.subList(0 + (page - 1), Math.min(users.size() - 1, perPageDisplay));
      for (int index = 0; index < displayUsers.size(); index++) {
        int slot = index + startSlot;
        menu.addItem(slot, SkullItem.fromPlayer(Bukkit.getOfflinePlayer(displayUsers.get(index))));
        menu.addMenuClickHandler(slot, (p1, slot1, item, cursor, action) -> {
          // @TODO
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
