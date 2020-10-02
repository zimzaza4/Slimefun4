package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * This enum covers all different fuel sources a {@link ProgrammableAndroid} can have.
 *
 * @author TheBusyBiscuit
 */
public enum AndroidFuelSource {

    /**
     * This {@link ProgrammableAndroid} runs on solid fuel, e.g. Wood or coal
     */
    SOLID("", "&f这类机器人需要固态燃料", "&f例如煤, 原木等..."),

    /**
     * This {@link ProgrammableAndroid} runs on liquid fuel, e.g. Fuel, Oil or Lava
     */
    LIQUID("", "&f这类机器人需要液态燃料", "&f例如岩浆, 原油, 燃油等..."),

    /**
     * This {@link ProgrammableAndroid} runs on nuclear fuel, e.g. Uranium
     */
    NUCLEAR("", "&f这类机器人需要放射性燃料", "&f例如铀, 镎或强化铀");

    private final String[] lore;

    AndroidFuelSource(@Nonnull String... lore) {
        this.lore = lore;
    }

    /**
     * This returns a display {@link ItemStack} for this {@link AndroidFuelSource}.
     *
     * @return An {@link ItemStack} to display
     */
    public ItemStack getItem() {
        return new CustomItem(HeadTexture.GENERATOR.getAsItemStack(), "&8\u21E9 &c燃料输入口 &8\u21E9", lore);
    }

}