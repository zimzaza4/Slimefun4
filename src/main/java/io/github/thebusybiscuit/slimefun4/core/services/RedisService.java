package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.core.redis.Subscriber;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;


public class RedisService {

    private final Slimefun plugin;
    public static String serverId;
    public static Subscriber subscriber;
    public static JedisPool pool;
    public static String ip;
    public static String password;
    public static int port;
    public static String[] channels;
    public static boolean connected;

    public RedisService(Slimefun plugin) {
        this.plugin = plugin;
    }

    public void load(Config config) {
        ip = config.getString("ip");
        port = config.getInt("port");
        password = config.getString("password");
        serverId = config.getString("server");
        List<String> channelList = config.getStringList("channels");
        channels = new String[channelList.size()];

        int i = 0;
        for (String s : channelList) {
            channels[i] = s;
            Slimefun.logger().info("添加频道:" + s);
            i++;
        }
        pool = password == null || password.equals("") ? new JedisPool(new JedisPoolConfig(), ip, port, 0) : new JedisPool(new JedisPoolConfig(), ip, port, 0, password);

        new BukkitRunnable() {

            public void run() {
                subscriber = new Subscriber(plugin);
                Jedis jedis = pool.getResource();
                try {
                    connected = true;
                    jedis.subscribe(subscriber, channels);
                    Slimefun.logger().info("成功连接上Redis");
                } catch (Exception e) {
                    Slimefun.logger().severe("你的redis大概升天了 所以你的服务器也升天了");
                    jedis.close();
                    connected = false;
                    Bukkit.getServer().shutdown();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public static Subscriber getSubscriber() {
        return subscriber;
    }

    public static JedisPool getPool() {
        return pool;
    }

    public void sendMessage(String channel, String msg) {
        Jedis jedis = pool.getResource();
        new BukkitRunnable(){

            public void run() {
                try {
                    jedis.publish(channel, msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                jedis.close();
            }
        }.runTaskAsynchronously(plugin);
    }
}
