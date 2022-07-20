package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Deprecated
public class HerculesPickaxe extends SimpleSlimefunItem<ToolUseHandler> {

    @ParametersAreNonnullByDefault
    public HerculesPickaxe(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            sendDeprecationWarning(e.getPlayer());

            Material mat = e.getBlock().getType();

            if (SlimefunTag.ORES.isTagged(mat)) {
                if (tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                    return;
                }

                if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
                    switch (mat) {
                        case DEEPSLATE_IRON_ORE:
                            drops.add(new CustomItemStack(SlimefunItems.IRON_DUST, 2));
                            return;
                        case DEEPSLATE_GOLD_ORE:
                            drops.add(new CustomItemStack(SlimefunItems.GOLD_DUST, 2));
                            return;
                        case COPPER_ORE:
                        case DEEPSLATE_COPPER_ORE:
                            drops.add(new CustomItemStack(SlimefunItems.COPPER_DUST, 2));
                            return;
                        default:
                            return;
                    }
                }

                switch (mat) {
                    case IRON_ORE:
                        drops.add(new CustomItemStack(SlimefunItems.IRON_DUST, 2));
                        return;
                    case GOLD_ORE:
                        drops.add(new CustomItemStack(SlimefunItems.GOLD_DUST, 2));
                }
            }
        };
    }

}
