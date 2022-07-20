package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This is a parent class for the {@link BrokenSpawner} and {@link RepairedSpawner}
 * to provide some utility methods.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BrokenSpawner
 * @see RepairedSpawner
 *
 */
public abstract class AbstractMonsterSpawner extends SlimefunItem {

    @ParametersAreNonnullByDefault
    AbstractMonsterSpawner(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    /**
     * This method tries to obtain an {@link EntityType} from a given {@link ItemStack}.
     * The provided {@link ItemStack} must be a {@link RepairedSpawner} item.
     * 
     * @param item
     *            The {@link ItemStack} to extract the {@link EntityType} from
     * 
     * @return An {@link Optional} describing the result
     */
    @Nonnull
    public Optional<EntityType> getEntityType(@Nonnull ItemStack item) {
        Validate.notNull(item, "The Item cannot be null");

        ItemMeta meta = item.getItemMeta();

        // We may want to update this in the future to also make use of the BlockStateMeta
        for (String line : meta.getLore()) {
            String stripColor = ChatColor.stripColor(line);

            if ((stripColor.startsWith("类型: ") || stripColor.startsWith("Type:")) && (!line.contains("<类型>") || line.contains("<Type>"))) {
                EntityType type = EntityType.valueOf(ChatColor.stripColor(line).replace("类型: ", "").replace("Type: ", "").replace(' ', '_').toUpperCase(Locale.ROOT));
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    /**
     * This method returns a finished {@link ItemStack} of this {@link SlimefunItem}, modified
     * to hold and represent the given {@link EntityType}.
     * It updates the lore and {@link BlockStateMeta} to reflect the specified {@link EntityType}.
     *
     * @param type
     *            The {@link EntityType} to apply
     *
     * @return An {@link ItemStack} for this {@link SlimefunItem} holding that {@link EntityType}
     */
    @Nonnull
    public ItemStack getItemForEntityType(@Nonnull EntityType type) {
        Validate.notNull(type, "The EntityType cannot be null");

        ItemStack item = getItem().clone();
        ItemMeta meta = item.getItemMeta();

        // Fixes #2583 - Proper NBT handling of Spawners
        if (meta instanceof BlockStateMeta stateMeta) {
            BlockState state = stateMeta.getBlockState();

            if (state instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(type);
            }

            stateMeta.setBlockState(state);
        }

        // Setting the lore to indicate the Type visually
        List<String> lore = meta.getLore();

        for (int i = 0; i < lore.size(); i++) {
            String currentLine = lore.get(i);
            if (currentLine.contains("<Type>") || currentLine.contains("<类型>")) {
                String typeName = ChatUtils.humanize(type.name());
                lore.set(i, currentLine.replace("<Type>", typeName).replace("<类型>", typeName));
                break;
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

}
