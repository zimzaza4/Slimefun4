package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 *
 * This interface define how to get the implementation of {@link CrafterInteractable} from {@link BlockMenu}.
 *
 * @author Xzavier0722
 *
 */
public interface CrafterInteractorHandler {

    CrafterInteractable getInteractor(BlockMenu menu);

}
