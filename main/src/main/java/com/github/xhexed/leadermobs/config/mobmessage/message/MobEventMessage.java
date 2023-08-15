package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.LeaderMobs;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class MobEventMessage {
    private LeaderMobs plugin;
    private List<String> messages;
    private long delay;
    private TitleMessage titleMessage;
    private ActionbarMessage actionbarMessage;
    
    public MobEventMessage(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.delay = config.getLong("delay", 0L);
        messages = config.getStringList("messages");
        messages.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
        if (config.contains("title"))
            titleMessage = new TitleMessage(plugin, Objects.requireNonNull(config.getConfigurationSection("title")));
        if (config.contains("actionbar"))
            actionbarMessage = new ActionbarMessage(plugin, Objects.requireNonNull(config.getConfigurationSection("actionbar")));
    }
}
