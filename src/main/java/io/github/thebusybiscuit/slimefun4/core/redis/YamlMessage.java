package io.github.thebusybiscuit.slimefun4.core.redis;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

public class YamlMessage extends RedisMessage{

    private Config config;
    public YamlMessage(String channel, Config msg) {
        super(channel, msg.getConfiguration().saveToString());
        this.config = msg;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setMsg(String msg) {
        this.config = new Config(msg);
        super.setMsg(msg);
    }

    public void setMsg(Config config) {
        this.config = config;
        setMsg(config.getConfiguration().saveToString());
    }

    @Override
    public void send() {
        Slimefun.getRedisService().sendMessage(getChannel(), config.getConfiguration().saveToString());
    }

}
