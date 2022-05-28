package io.github.thebusybiscuit.slimefun4.core.redis;

import io.github.thebusybiscuit.slimefun4.api.events.RedisReceiveEvent;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {

    private final Slimefun plugin;
    public Subscriber(Slimefun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessage(final String channel, final String msg) {
        Slimefun.runSync(() -> {
            plugin.getServer().getPluginManager().callEvent(new RedisReceiveEvent(channel, msg));
        });
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {
    }

    @Override
    public void onSubscribe(String s, int i) {
    }

    @Override
    public void onUnsubscribe(String s, int i) {
    }

    @Override
    public void onPUnsubscribe(String s, int i) {
    }

    @Override
    public void onPSubscribe(String s, int i) {
    }
}
