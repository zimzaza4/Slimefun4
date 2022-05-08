package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

class ItemIdCommand extends SubCommand {
    protected ItemIdCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "id", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("slimefun.command.id")) {
                Player p = (Player) sender;
                ItemStack item = p.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    SlimefunItem sfItem = SlimefunItem.getByItem(item);
                    if (sfItem != null) {
                        String sfId = sfItem.getId();
                        TextComponent msg = new TextComponent("该物品的ID为: ");
                        TextComponent idMsg = new TextComponent(sfId);
                        idMsg.setUnderlined(true);
                        idMsg.setItalic(true);
                        idMsg.setColor(ChatColor.GRAY);
                        idMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击复制到剪贴板")));
                        idMsg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sfId));
                        sender.spigot().sendMessage(msg, idMsg);
                    } else {
                        Slimefun.getLocalization().sendMessage(sender, "messages.invalid-item-in-hand", true);
                    }
                } else {
                    sender.sendMessage(ChatColors.color("&b请将需要查看的物品拿在主手!"));
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }
}
