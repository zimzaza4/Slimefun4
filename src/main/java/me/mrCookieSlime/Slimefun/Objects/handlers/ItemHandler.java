package me.mrCookieSlime.Slimefun.Objects.handlers;

import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityKillHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * An {@link ItemHandler} represents a certain action that a {@link SlimefunItem}
 * can perform.
 *
 * @author TheBusyBiscuit
 * @see ItemConsumptionHandler
 * @see BlockUseHandler
 * @see EntityKillHandler
 * @see EntityInteractHandler
 */
@FunctionalInterface
public interface ItemHandler {

    /**
     * This method returns the identifier for this {@link ItemHandler}.
     * We use a {@link Class} identifier to group Item Handlers together.
     *
     * @return The {@link Class} identifier for this {@link ItemHandler}
     */
    @Nonnull
    Class<? extends ItemHandler> getIdentifier();

    /**
     * This method is used to check whether a given {@link SlimefunItem} is compatible
     * with this {@link ItemHandler}, it will return an {@link IncompatibleItemHandlerException}
     * if the items are not compatible.
     *
     * @param item The {@link SlimefunItem} to validate
     * @return An {@link Optional} describing the result, it will contain an {@link IncompatibleItemHandlerException}
     * should there be an issue
     */
    @Nonnull
    default Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        return Optional.empty();
    }
}
