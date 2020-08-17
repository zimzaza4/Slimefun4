package io.github.thebusybiscuit.slimefun4.implementation.guide;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideLayout;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class CheatSheetSlimefunGuide extends ChestSlimefunGuide {

    private final ItemStack item;

    public CheatSheetSlimefunGuide() {
        super(false);

        item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColors.color("&cSlimefun 指南 &4(作弊模式)"));

        List<String> lore = new LinkedList<>();

        lore.add(ChatColors.color("&4&l仅限管理员使用"));
        lore.add(ChatColors.color("&e右键 &8\u21E8 &7浏览物品"));
        lore.add(ChatColors.color("&eShift + 右键 &8\u21E8 &7打开设置 / 关于"));

        meta.setLore(lore);
        SlimefunPlugin.getItemTextureService().setTexture(meta, "SLIMEFUN_GUIDE");
        item.setItemMeta(meta);
    }

    @Override
    public boolean isSurvivalMode() {
        return false;
    }

    @Override
    public SlimefunGuideLayout getLayout() {
        return SlimefunGuideLayout.CHEAT_SHEET;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    protected void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        super.createHeader(p, profile, menu);

        // Remove Settings Panel
        menu.addItem(1, ChestMenuUtils.getBackground());
        menu.addMenuClickHandler(1, ChestMenuUtils.getEmptyClickHandler());
    }
}