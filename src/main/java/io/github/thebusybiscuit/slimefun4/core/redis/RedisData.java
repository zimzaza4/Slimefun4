package io.github.thebusybiscuit.slimefun4.core.redis;

import io.github.thebusybiscuit.slimefun4.core.services.RedisService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

public class RedisData {
    public static String getData(String key) {
        Jedis jedis = RedisService.getPool().getResource();
        String r = jedis.get(key);
        jedis.close();
        return r;
    }

    public static void setData(String key, String data) {
        Jedis jedis = RedisService.pool.getResource();
        new BukkitRunnable(){

            public void run() {
                try {
                    jedis.set(key, data);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                jedis.close();
            }
        }.runTaskAsynchronously(Slimefun.instance());
    }

    public static void deleteData(String key) {

        Jedis jedis = RedisService.pool.getResource();
        new BukkitRunnable(){

            public void run() {
                try {
                    jedis.del(key);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                jedis.close();
            }
        }.runTaskAsynchronously(Slimefun.instance());

    }
}
