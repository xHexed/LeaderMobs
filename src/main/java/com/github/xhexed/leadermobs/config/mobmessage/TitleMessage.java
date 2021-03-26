package com.github.xhexed.leadermobs.config.mobmessage;

import org.bukkit.configuration.ConfigurationSection;

public class TitleMessage {
    public long delay;
    public String title;
    public String subTitle;
    public int fadeIn;
    public int stay;
    public int fadeOut;
    
    public TitleMessage(ConfigurationSection config) {
        delay = config.getLong("delay");
        title = config.getString("title");
        subTitle = config.getString("sub-title");
        fadeIn = config.getInt("fade-in");
        stay = config.getInt("stay");
        fadeOut = config.getInt("fade-out");
    }
}
