package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class RepairedSpawner extends SimpleSlimefunItem<BlockPlaceHandler> {

    public RepairedSpawner(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public BlockPlaceHandler getItemHandler() {
        return new BlockPlaceHandler(true) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                onPlace(e.getItemInHand(), e);
            }

            @Override
            public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
                onPlace(e.getItemStack(), e);
            }

            private void onPlace(ItemStack item, BlockEvent e) {
                Optional<EntityType> entity = getEntityType(item);

                if (entity.isPresent() && e.getBlock().getType() == Material.SPAWNER) {
                    CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
                    spawner.setSpawnedType(entity.get());
                    spawner.update(true, false);
                }
            }
        };
    }


    /**
     * This method tries to obtain an {@link EntityType} from a given {@link ItemStack}.
     * The provided {@link ItemStack} must be a {@link RepairedSpawner} item.
     *
     * @param item
     * @return
     */
    public Optional<EntityType> getEntityType(ItemStack item) {
        for (String line : item.getItemMeta().getLore()) {
            EntityType type;
            if (ChatColor.stripColor(line).startsWith("类型: ") && !line.contains("<类型>")) {
                type = EntityType.valueOf(ChatColor.stripColor(line).replace("类型: ", "").replace(' ', '_').toUpperCase(Locale.ROOT));
                return Optional.of(type);
            }
            if (ChatColor.stripColor(line).startsWith("Type: ") && !line.contains("<Type>")) {
                type = EntityType.valueOf(ChatColor.stripColor(line).replace("Type: ", "").replace(' ', '_').toUpperCase(Locale.ROOT));
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<ItemStack> getDrops() {
        // There should be no drops by default since drops are handled by the
        // Pickaxe of Containment exclusively.
        return new ArrayList<>();
    }
}
