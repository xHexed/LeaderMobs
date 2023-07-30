package com.github.xhexed.leadermobs.config.mobmessage;

import org.bukkit.configuration.ConfigurationSection;

public class ActionbarMessage {
    public long delay;
    public String message;

    public ActionbarMessage(ConfigurationSection config) {
        delay = config.getLong("delay");
        message = config.getString("message");
    }
}
