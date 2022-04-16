package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class QuicklyPortableTeleporter extends PortableTeleporter{
    private static final int CAPACITY = 200;
    private static final int DEFAULT_COST = 20;



    @ParametersAreNonnullByDefault
    public QuicklyPortableTeleporter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemSetting(getCost());
    }

    @Override
    public ItemSetting<Integer> getCost() {
        return new IntRangeSetting(this, "teleportation-cost", 0, DEFAULT_COST, CAPACITY);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            ItemStack item = e.getItem();
            e.cancel();

            if (removeItemCharge(item, getCost().getValue())) {
                Player p = e.getPlayer();
                Slimefun.getGPSNetwork().getTeleportationManager().teleportQuicklyUsers.add(p.getUniqueId());
                Slimefun.getGPSNetwork().getTeleportationManager().openTeleporterGUI(p, p.getUniqueId(), p.getLocation().getBlock().getRelative(BlockFace.DOWN));
            }
        };
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return CAPACITY;
    }
}
