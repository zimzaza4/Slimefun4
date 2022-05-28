package io.github.thebusybiscuit.slimefun4.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class RedisReceiveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String channel;
    private final String msg;

    public RedisReceiveEvent(String ch, String ms) {
        this.channel = ch;
        this.msg = ms;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getMsg() {
        return this.msg;
    }

    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
