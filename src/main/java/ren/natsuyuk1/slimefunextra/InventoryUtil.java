package ren.natsuyuk1.slimefunextra;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class InventoryUtil {
    public static void closeInventory(Inventory inventory) {
        if (inventory != null) {
            inventory.getViewers().forEach(HumanEntity::closeInventory);
        }
    }
}
