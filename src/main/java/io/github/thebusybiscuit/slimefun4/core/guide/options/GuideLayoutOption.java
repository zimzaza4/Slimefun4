package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GuideLayoutOption implements SlimefunGuideOption<SlimefunGuideMode> {

    @Override
    public SlimefunAddon getAddon() {
        return SlimefunPlugin.instance();
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(SlimefunPlugin.instance(), "guide_layout");
    }

    @Nonnull
    @Override
    public Optional<ItemStack> getDisplayItem(@Nonnull Player p, @Nonnull ItemStack guide) {
        Optional<SlimefunGuideMode> current = getSelectedOption(p, guide);

        if (current.isPresent()) {
            SlimefunGuideMode layout = current.get();
            ItemStack item = new ItemStack(Material.AIR);

            if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
                item.setType(Material.CHEST);
            } else {
                item.setType(Material.COMMAND_BLOCK);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Slimefun 指南样式: " + ChatColor.YELLOW + ChatUtils.humanize(layout.name()));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add((layout == SlimefunGuideMode.SURVIVAL_MODE ? ChatColor.GREEN : ChatColor.GRAY) + "箱子界面");

            if (p.hasPermission("slimefun.cheat.items")) {
                lore.add((layout == SlimefunGuideMode.CHEAT_MODE ? ChatColor.GREEN : ChatColor.GRAY) + "作弊界面");
            }

            lore.add("");
            lore.add(ChatColor.GRAY + "\u21E8 " + ChatColor.YELLOW + "单击修改指南样式");
            meta.setLore(lore);
            item.setItemMeta(meta);

            return Optional.of(item);
        }

        return Optional.empty();
    }

    @Override
    public void onClick(@Nonnull Player p, @Nonnull ItemStack guide) {
        Optional<SlimefunGuideMode> current = getSelectedOption(p, guide);

        if (current.isPresent()) {
            SlimefunGuideMode next = getNextLayout(p, current.get());
            setSelectedOption(p, guide, next);
        }

        SlimefunGuideSettings.openSettings(p, guide);
    }

    @Nonnull
    private SlimefunGuideMode getNextLayout(Player p, SlimefunGuideMode layout) {
        if (p.hasPermission("slimefun.cheat.items") && layout == SlimefunGuideMode.SURVIVAL_MODE) {
            return SlimefunGuideMode.CHEAT_MODE;
        }
        return SlimefunGuideMode.SURVIVAL_MODE;
    }

    @Override
    public Optional<SlimefunGuideMode> getSelectedOption(@Nonnull Player p, @Nonnull ItemStack guide) {
        for (SlimefunGuideMode layout : SlimefunGuideMode.valuesCache) {
            if (SlimefunUtils.isItemSimilar(guide, SlimefunGuide.getItem(layout), true, false)) {
                return Optional.of(layout);
            }
        }

        return Optional.empty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setSelectedOption(Player p, ItemStack guide, SlimefunGuideMode value) {
        guide.setItemMeta(SlimefunGuide.getItem(value).getItemMeta());
    }

}