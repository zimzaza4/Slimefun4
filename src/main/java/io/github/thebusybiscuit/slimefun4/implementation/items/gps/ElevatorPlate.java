package io.github.thebusybiscuit.slimefun4.implementation.items.gps;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.cscorelib2.chat.json.ChatComponent;
import io.github.thebusybiscuit.cscorelib2.chat.json.ClickEvent;
import io.github.thebusybiscuit.cscorelib2.chat.json.CustomBookInterface;
import io.github.thebusybiscuit.cscorelib2.chat.json.HoverEvent;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ElevatorPlate extends SimpleSlimefunItem<BlockUseHandler> {
    private static final String DATA_KEY = "floor";

    private final Set<UUID> users = new HashSet<>();

    public ElevatorPlate(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);

        addItemHandler(onPlace());
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Block b = e.getBlock();
                BlockStorage.addBlockInfo(b, DATA_KEY, "&f一楼");
                BlockStorage.addBlockInfo(b, "owner", e.getPlayer().getUniqueId().toString());
            }
        };
    }

    public Set<UUID> getUsers() {
        return users;
    }

    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            if (e.getClickedBlock().isPresent()) {
                Block b = e.getClickedBlock().get();

                if (BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(e.getPlayer().getUniqueId().toString())) {
                    openEditor(e.getPlayer(), b);
                }
            }
        };
    }

    public List<Block> getFloors(Block b) {
        List<Block> floors = new LinkedList<>();

        for (int y = 0; y < b.getWorld().getMaxHeight(); y++) {
            if (b.getY() == y) {
                floors.add(b);
                continue;
            }

            Block block = b.getWorld().getBlockAt(b.getX(), y, b.getZ());

            if (BlockStorage.check(block, getID()) && block.getType() == getItem().getType()) {
                floors.add(block);
            }
        }

        floors.sort((b1, b2) -> b2.getY() - b1.getY());

        return floors;
    }

    public void open(Player p, Block b) {
        if (users.remove(p.getUniqueId())) {
            return;
        }

        CustomBookInterface book = new CustomBookInterface(SlimefunPlugin.instance());
        ChatComponent page = null;

        List<Block> floors = getFloors(b);

        if (floors.size() < 2) {
            SlimefunPlugin.getLocalization().sendMessage(p, "machines.ELEVATOR.no-destinations", true);
        }

        for (int i = 0; i < floors.size(); i++) {
            if (i % 10 == 0) {
                if (page != null) {
                    book.addPage(page);
                }

                page = new ChatComponent(ChatColors.color(SlimefunPlugin.getLocalization().getMessage(p, "machines.ELEVATOR.pick-a-floor")) + "\n");
            }

            Block block = floors.get(i);
            String floor = ChatColors.color(BlockStorage.getLocationInfo(block.getLocation(), DATA_KEY));
            ChatComponent line;

            if (block.getY() == b.getY()) {
                line = new ChatComponent("\n" + ChatColor.GRAY + "> " + (floors.size() - i) + ". " + ChatColor.WHITE + floor);
                line.setHoverEvent(new HoverEvent(ChatColors.color(SlimefunPlugin.getLocalization().getMessage(p, "machines.ELEVATOR.current-floor")), "", ChatColor.BLACK + floor, ""));
            } else {
                line = new ChatComponent("\n" + ChatColor.GRAY + (floors.size() - i) + ". " + ChatColor.WHITE + floor);
                line.setHoverEvent(new HoverEvent(ChatColors.color(SlimefunPlugin.getLocalization().getMessage(p, "machines.ELEVATOR.click-to-teleport")), "", ChatColor.BLACK + floor, ""));
                line.setClickEvent(new ClickEvent(new NamespacedKey(SlimefunPlugin.instance(), DATA_KEY + i), player -> Slimefun.runSync(() -> {
                    users.add(player.getUniqueId());

                    float yaw = player.getEyeLocation().getYaw() + 180;

                    if (yaw > 180) {
                        yaw = -180 + (yaw - 180);
                    }

                    Location destination = new Location(player.getWorld(), block.getX() + 0.5, block.getY() + 0.4, block.getZ() + 0.5, yaw, player.getEyeLocation().getPitch());
                    PaperLib.teleportAsync(player, destination).thenAccept(teleported -> {
                        if (teleported) {
                            player.sendTitle(ChatColor.WHITE + ChatColors.color(floor), null, 20, 60, 20);
                        }
                    });
                })));
            }

            page.append(line);
        }

        if (page != null) {
            book.addPage(page);
        }

        book.open(p);
    }

    public void openEditor(Player p, Block b) {
        ChestMenu menu = new ChestMenu("电梯设置");

        menu.addItem(4, new CustomItem(Material.NAME_TAG, "&7楼层名 &e(单击编辑)", "", "&r" + ChatColors.color(BlockStorage.getLocationInfo(b.getLocation(), DATA_KEY))));
        menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
            pl.closeInventory();
            pl.sendMessage("");
            SlimefunPlugin.getLocalization().sendMessage(p, "machines.ELEVATOR.enter-name");
            pl.sendMessage("");

            ChatUtils.awaitInput(pl, message -> {
                BlockStorage.addBlockInfo(b, DATA_KEY, message.replace(ChatColor.COLOR_CHAR, '&'));

                pl.sendMessage("");
                SlimefunPlugin.getLocalization().sendMessage(p, "machines.ELEVATOR.named", msg -> msg.replace("%floor%", message));
                pl.sendMessage("");

                openEditor(pl, b);
            });

            return false;
        });

        menu.open(p);
    }

}