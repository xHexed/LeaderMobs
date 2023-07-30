package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.config.mobmessage.ActionbarMessage;
import com.github.xhexed.leadermobs.config.mobmessage.TitleMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

public abstract class MobEventMessage {
    public long delay;
    public List<String> messages;
    public TitleMessage titleMessage;
    public ActionbarMessage actionbarMessage;
    
    public MobEventMessage(ConfigurationSection config) {
        if (config.contains("delay"))
            delay = config.getLong("delay");
        if (config.contains("messages"))
            messages = config.getStringList("messages");
        if (config.contains("title"))
            titleMessage = new TitleMessage(Objects.requireNonNull(config.getConfigurationSection("title")));
        if (config.contains("actionbar"))
            actionbarMessage = new ActionbarMessage(Objects.requireNonNull(config.getConfigurationSection("actionbar")));
    }
}
