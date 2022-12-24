package ren.natsuyuk1.slimefunextra.inventoryholder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SlimefunInventoryHolder implements InventoryHolder {
    protected Inventory inventory;

    public void setInventory(Inventory inv) {
        inventory = inv;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
