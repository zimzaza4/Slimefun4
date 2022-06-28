package cn.zimzaza4.slimefun4;

import io.github.thebusybiscuit.slimefun4.api.events.RedisReceiveEvent;
import io.github.thebusybiscuit.slimefun4.core.services.RedisService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerLag implements Listener {

    private final static String MESSAGE = "syncLowPerformanceModePlayer";
    public final static Set<Player> LowPerformanceModePlayers = new HashSet<>();
    public static void setLowPerformanceMode(Player p, boolean on) {
        Jedis jedis = RedisService.getPool().getResource();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (on) {
                    jedis.sadd("LowPerformanceMode", p.getUniqueId().toString());
                } else {
                    jedis.srem("LowPerformanceMode", p.getUniqueId().toString());
                }
                jedis.publish("Slimefun", MESSAGE);
                jedis.close();
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }

    public static boolean isLowPerformanceMode(Player p) {
        return LowPerformanceModePlayers.contains(p);
    }

    @EventHandler
    public void onRedisMessage(RedisReceiveEvent e) {
        if (e.getChannel().equals("Slimefun")) {
            if (e.getMsg().equals(MESSAGE)) {
                LowPerformanceModePlayers.clear();
                Jedis jedis = RedisService.getPool().getResource();
                Set<String> players = jedis.smembers("LowPerformanceMode");
                for (String uuid : players) {
                    Player p = Bukkit.getPlayer(UUID.fromString(uuid));
                    if (p != null && p.isOnline()) {
                        LowPerformanceModePlayers.add(p);
                    }
                }
                jedis.close();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Jedis jedis = RedisService.getPool().getResource();
        new BukkitRunnable() {
            @Override
            public void run() {
                jedis.publish("Slimefun", MESSAGE);
                jedis.close();
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }

}
