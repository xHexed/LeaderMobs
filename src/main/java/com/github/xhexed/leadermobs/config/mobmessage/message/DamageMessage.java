package com.github.xhexed.leadermobs.config.mobmessage.message;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DamageMessage extends MobEventMessage {
    public boolean hideEmptyHeader;
    public boolean hideEmptyFooter;
    public List<String> headerMessages;
    public List<String> footerMessages;
    public String defaultPlacePrefix;
    public Map<Integer, String> placePrefixes = new HashMap<>();

    public DamageMessage(ConfigurationSection config) {
        super(config);
        hideEmptyHeader = config.getBoolean("hide-empty-header");
        hideEmptyFooter = config.getBoolean("hide-empty-footer");
        headerMessages = config.getStringList("header-messages");
        footerMessages = config.getStringList("footer-messages");
        if (config.contains("default-place-prefix"))
            defaultPlacePrefix = config.getString("default-place-prefix");
        if (config.contains("place-prefix")) {
            ConfigurationSection placePrefix = config.getConfigurationSection("place-prefix");
            placePrefixes.clear();
            Objects.requireNonNull(placePrefix).getKeys(false).forEach(place -> placePrefixes.put(Integer.valueOf(place), placePrefix.getString(place)));
        }
    }
}
