package io.github.thebusybiscuit.slimefun4.core.redis;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RedisMessage {

    private String channel;
    private String msg;

    public RedisMessage(String channel, String msg) {
        this.channel = channel;
        this.msg = msg;
    }

    public RedisMessage(String channel, byte[] bytes) {
        this.channel = channel;
        this.msg = deserialize(new String(bytes, StandardCharsets.UTF_8));
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void send() {
        Slimefun.getRedisService().sendMessage(channel, serialize(msg));
    }

    public static String deserialize(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }
    public static String serialize(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
