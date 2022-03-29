package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.utils.PersistentType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This {@link Listener} is responsible for all events centered around a {@link SlimefunBackpack}.
 * This also includes the {@link Cooler}
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 * @author NihilistBrew
 * @author AtomicScience
 * @author VoidAngel
 * @author John000708
 * 
 * @see SlimefunBackpack
 * @see PlayerBackpack
 *
 */
public class BackpackListener implements Listener {

    private Slimefun instance = null;
    private final String itemKey = "SLOT_";
    public static final Map<Player, Inventory> openedInventories = new HashMap<>();
    private static NamespacedKey key;
    private final Map<Player, ItemStack> usingBackPacks = new HashMap<>();
    private final Map<UUID, ItemStack> backpacks = new HashMap<>();

    public void register(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        key = new NamespacedKey(plugin, "ITEMS");
        instance = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (openedInventories.containsValue(e.getInventory())) {
            openedInventories.remove(p);
            saveBackpack(usingBackPacks.get(p), e.getInventory());
            p.playSound(p.getLocation(), Sound.ENTITY_HORSE_ARMOR, 1F, 1F);
            usingBackPacks.remove(p);
        }
    }

    private void saveBackpack(@Nonnull ItemStack backpack, Inventory inventory) {

        if (backpack.hasItemMeta()) {
            ItemMeta meta = backpack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            PersistentDataContainer items = container.get(key, PersistentDataType.TAG_CONTAINER);
            int i = 0;
            for (ItemStack item : inventory) {
                if (item != null) {
                    NamespacedKey slotKey = new NamespacedKey(instance, itemKey + i);
                    items.set(slotKey, PersistentType.ITEM_STACK_OLD, item);
                };
                i++;
            }
            container.set(key, PersistentDataType.TAG_CONTAINER, items);
            backpack.setItemMeta(meta);
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (backpacks.containsKey(e.getPlayer().getUniqueId())) {
            ItemStack item = e.getItemDrop().getItemStack();
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem instanceof SlimefunBackpack) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        ItemStack item = backpacks.get(e.getWhoClicked().getUniqueId());

        if (item != null) {
            SlimefunItem backpack = SlimefunItem.getByItem(item);

            if (backpack instanceof SlimefunBackpack) {
                if (e.getClick() == ClickType.NUMBER_KEY) {
                    if (e.getClickedInventory().getType() != InventoryType.PLAYER || e.getClickedInventory().getType() != InventoryType.CREATIVE) {
                        ItemStack hotbarItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());

                        if (!isAllowed((SlimefunBackpack) backpack, hotbarItem)) {
                            e.setCancelled(true);
                        }
                    }
                } else if (e.getClick() == ClickType.SWAP_OFFHAND && e.getClickedInventory().getType() != InventoryType.PLAYER) {
                    // Fixes #3265
                    ItemStack offHandItem = e.getWhoClicked().getInventory().getItemInOffHand();
                    if (!isAllowed((SlimefunBackpack) backpack, offHandItem)) {
                        e.setCancelled(true);
                    }
                } else if (!isAllowed((SlimefunBackpack) backpack, e.getCurrentItem())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private boolean isAllowed(@Nonnull SlimefunBackpack backpack, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }

        return backpack.isItemAllowed(item, SlimefunItem.getByItem(item));
    }

    @ParametersAreNonnullByDefault
    public void openBackpack(Player p, ItemStack item, SlimefunBackpack backpack) {
        if (item.getAmount() == 1) {
            if (backpack.canUse(p, true) && !openBackpack(p, item, backpack.getSize())) {
                Slimefun.getLocalization().sendMessage(p, "messages.opening-backpack");
            }
        } else {
            Slimefun.getLocalization().sendMessage(p, "backpack.no-stack", true);
        }
    }

    @ParametersAreNonnullByDefault
    private boolean openBackpack(Player p, ItemStack item, int size) {
        List<String> lore = item.getItemMeta().getLore();

        for (int line = 0; line < lore.size(); line++) {
            if (lore.get(line).equals(ChatColor.GRAY + "ID: <ID>")) {
                setBackpackId(p, item, line);
                break;
            }
        }

        /*
         * If the current Player is already viewing a backpack (for whatever reason),
         * terminate that view.
         */
        if (openedInventories.containsKey(p)) {
            p.closeInventory();
        }

        // Check if someone else is currently viewing this backpack
        if (!backpacks.containsValue(item)) {
            p.playSound(p.getLocation(), Sound.ENTITY_HORSE_ARMOR, 1F, 1F);
            backpacks.put(p.getUniqueId(), item);
            openBackpackInventory(p, item, size);
            return true;
        } else {
            Slimefun.getLocalization().sendMessage(p, "backpack.already-open", true);
        }

        return false;
    }


    public boolean openBackpackInventory(Player p, ItemStack item, int size) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            PersistentDataContainer items = pdc.get(key, PersistentDataType.TAG_CONTAINER);
            Inventory inventory = Bukkit.createInventory(null, size, "Backpack [" + size + " Slots]");
            if (items != null) {
                for (int i = 0; i < size; i++) {
                    NamespacedKey slotKey = new NamespacedKey(instance, itemKey + i);
                    if (items.has(slotKey, PersistentType.ITEM_STACK_OLD)) {
                        inventory.setItem(i, items.get(slotKey, PersistentType.ITEM_STACK_OLD));
                    }
                }
            } else {

                pdc.set(key, PersistentDataType.TAG_CONTAINER, pdc.getAdapterContext().newPersistentDataContainer());
                item.setItemMeta(meta);
            }
            openedInventories.put(p, inventory);
            p.openInventory(inventory);
            usingBackPacks.put(p, item);
            return true;
        }
        return false;
    }
    /**
     * This method sets the id for a backpack onto the given {@link ItemStack}.
     * 
     * @param backpackOwner
     *            The owner of this backpack
     * @param item
     *            The {@link ItemStack} to modify
     * @param line
     *            The line at which the ID should be replaced
     */
    public void setBackpackId(@Nonnull OfflinePlayer backpackOwner, @Nonnull ItemStack item, int line) {

        ItemMeta im = item.getItemMeta();

        if (!im.hasLore()) {
            throw new IllegalArgumentException("This backpack does not have any lore!");
        }

        List<String> lore = im.getLore();

        if (line >= lore.size() || !lore.get(line).contains("<ID>")) {
            throw new IllegalArgumentException("Specified a line that is out of bounds or invalid!");
        }

        lore.set(line, lore.get(line).replace("<ID>", backpackOwner.getUniqueId().toString()));
        im.setLore(lore);

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        pdc.set(key, PersistentDataType.TAG_CONTAINER, pdc.getAdapterContext().newPersistentDataContainer());
        item.setItemMeta(im);
    }



}
