package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CargoManager extends SlimefunItem implements HologramOwner {

    public CargoManager(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        registerBlockHandler(getId(), (p, b, tool, reason) -> {
            removeHologram(b);
            return true;
        });
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                CargoNet.getNetworkFromLocationOrCreate(b.getLocation()).tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }

        }, (BlockUseHandler) e -> {
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent()) {
                Player p = e.getPlayer();
                Block b = block.get();

                if (BlockStorage.getLocationInfo(b.getLocation(), "visualizer") == null) {
                    BlockStorage.addBlockInfo(b, "visualizer", "disabled");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c货运网络可视化: " + "&4\u2718"));
                } else {
                    BlockStorage.addBlockInfo(b, "visualizer", null);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c货运网络可视化: " + "&2\u2714"));
                }
            }
        });
    }

}