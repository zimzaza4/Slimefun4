package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 *
 */
public class CrafterInteractorManager {

    private static final Map<String, CrafterInteractorHandler> handlers = new HashMap<>();

    public static void register(String id, CrafterInteractorHandler handler){
        handlers.put(id,handler);
    }

    public static CrafterInteractorHandler getHandler(String id){
        return handlers.get(id);
    }

    public static CrafterInteractable getInteractor(Block b){
        if(hasInterator(b)){
            CrafterInteractorHandler handler = handlers.get(BlockStorage.getLocationInfo(b.getLocation(),"id"));
            return handler.getInteractor(BlockStorage.getInventory(b));
        }
        return null;
    }

    public static boolean hasInterator(Block b){
        return BlockStorage.hasBlockInfo(b) && handlers.containsKey(BlockStorage.getLocationInfo(b.getLocation(),"id"));
    }

}
