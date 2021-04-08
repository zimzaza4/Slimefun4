package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

public interface CrafterInteractorHandler {

    CrafterInteractable getInteractor(BlockMenu menu);

}
