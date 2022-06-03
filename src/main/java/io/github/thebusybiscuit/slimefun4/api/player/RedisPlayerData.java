package io.github.thebusybiscuit.slimefun4.api.player;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.services.RedisService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RedisPlayerData {

    public static void savePlayerResearchData(PlayerProfile profile) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = RedisService.getPool().getResource();
                Set<String> data = jedis.smembers("ResearchData:" + profile.getUUID());
                for (Research r : profile.getResearches()) {
                    if (!data.contains(r.getKey().toString())) {
                        jedis.sadd("ResearchData:" + profile.getUUID(), r.getKey().toString());
                    }
                }

                jedis.close();
            }
        }.runTaskAsynchronously(Slimefun.instance());

    }

    public static List<Research> getPlayerResearchData(PlayerProfile profile) {
        Jedis jedis = RedisService.getPool().getResource();
        List<Research> researches = new ArrayList<>();
        for (String id : jedis.smembers("ResearchData:" + profile.getUUID())) {
            Optional<Research> research = Research.getResearch(NamespacedKey.fromString(id));
            research.ifPresent(researches::add);
        }
        return researches;
    }
}
