package io.github.thebusybiscuit.slimefun4.implementation.guide;

import io.github.thebusybiscuit.cscorelib2.collections.LoopIterator;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link RecipeChoiceTask} is an asynchronously repeating task that cycles
 * through the different variants of {@link Material} that a {@link MaterialChoice} or {@link Tag} can represent.
 * <p>
 * It is used in the {@link ChestSlimefunGuide} for any {@link ItemStack} from Minecraft
 * that accepts more than one {@link Material} in its {@link Recipe}.
 *
 * @author TheBusyBiscuit
 */
class RecipeChoiceTask implements Runnable {

    private static final int UPDATE_INTERVAL = 15;

    private Inventory inventory;
    private int id;
    private Map<Integer, LoopIterator<Material>> iterators = new HashMap<>();

    public void start(Inventory inv) {
        inventory = inv;
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(SlimefunPlugin.instance, this, 0, UPDATE_INTERVAL).getTaskId();
    }

    public void add(int slot, MaterialChoice choice) {
        iterators.put(slot, new LoopIterator<>(choice.getChoices()));
    }

    public void add(int slot, Tag<Material> tag) {
        iterators.put(slot, new LoopIterator<>(tag.getValues()));
    }

    public boolean isEmpty() {
        return iterators.isEmpty();
    }

    @Override
    public void run() {
        if (inventory.getViewers().isEmpty()) {
            Bukkit.getScheduler().cancelTask(id);
            return;
        }

        for (Map.Entry<Integer, LoopIterator<Material>> entry : iterators.entrySet()) {
            inventory.setItem(entry.getKey(), new ItemStack(entry.getValue().next()));
        }
    }

}
