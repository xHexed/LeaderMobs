package com.github.xhexed.leadermobs.config.updater.impl;

import com.github.xhexed.leadermobs.config.updater.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OldConfigUpdater implements ConfigUpdater {
    public static OldConfigUpdater INSTANCE = new OldConfigUpdater();

    @Override
    public void update(FileConfiguration config, FileConfiguration defaultConfig) {
        updatePluginMessages(config, defaultConfig);
        updateMobList(config);
        setConfig(config, "players-required", config.get("PlayersRequired"));
        setConfig(config, "places-to-broadcast", config.get("PlacesToBroadcast"));
        if (config.contains("Damage")) {
            setConfig(config, "total-damage-requirement.dealt-damage-required", config.get("Damage.required"));
            setConfig(config, "total-damage-requirement.taken-damage-required", config.get("Damage.required"));
        }
        if (config.contains("Warmup")) {
            setConfig(config, "warmup-damage-requirement.damage", config.get("Warmup.damage"));
            setConfig(config, "warmup-damage-requirement.reset-interval", config.get("Warmup.reset.interval"));
        }
        if (config.contains("Reset")) {
            setConfig(config, "reset-damage-requirement.damage", config.get("Reset.damage"));
            setConfig(config, "reset-damage-requirement.reset-interval", config.get("Reset.interval"));
        }
        if (config.contains("Messages.MobSpawn")) {
            convertMessages(config, "Messages.MobSpawn", "mob-spawn-message");
        }
        if (config.contains("Messages.MobDead")) {
            if (config.contains("Messages.MobDead.message"))
                convertMobDeadMessages(config, "Messages.MobDead", "mob-dead-message.damage-dealt");
            if (config.contains("Messages.MobDead.damageDealt"))
                convertMobDeadMessages(config, "Messages.MobDead.damageDealt", "mob-dead-message.damage-dealt");
            if (config.contains("Messages.MobDead.damageTaken"))
                convertMobDeadMessages(config, "Messages.MobDead.damageTaken", "mob-dead-message.damage-taken");
        }
    }

    private void updateMobList(FileConfiguration config) {
        List<String> mobList = new ArrayList<>();
        String prefix = "^(";
        String suffix = ")$";
        if (config.getBoolean("Blacklist.Whitelist", false)) {
            prefix = "^(?!(";
            suffix = ")$).*$";
        }
        if (config.contains("Blacklist.MythicMobs")) {
            mobList.add("MythicMobs;" + prefix + String.join("|", config.getStringList("Blacklist.MythicMobs")) + suffix);
        }
        if (config.contains("Blacklist.Boss")) {
            mobList.add("Boss;" + prefix + String.join("|", config.getStringList("Blacklist.Boss")) + suffix);
        }
        setConfig(config, "mobs", mobList);
    }

    private void updatePluginMessages(FileConfiguration config, FileConfiguration defaultConfig) {
        config.set("plugin-messages", defaultConfig.get("plugin-messages"));
        convertToggleMessage(config, "Messages.Toggle.true", "Messages.Toggle.false");
        convertToggleMessage(config, "Messages.toggle.true", "Messages.toggle.false");
        convertToggleMessage(config, "Messages.ToggleON", "Messages.ToggleOFF");
        convertToggleMessage(config, "Messages.Toggle.On", "Messages.Toggle.Off");
        convertToggleMessage(config, "Messages.toggle.on", "Messages.toggle.off");
    }
    
    private void convertToggleMessage(FileConfiguration config, String toggleOnString, String toggleOffString) {
        if (config.contains(toggleOnString))
            config.set("plugin-messages.toggle-broadcast.true",
                    config.get(toggleOnString));
        if (config.contains(toggleOffString))
            config.set("plugin-messages.toggle-broadcast.false",
                    config.get(toggleOffString));
    }

    private void convertMobDeadMessages(FileConfiguration config, String section, String replacedSection) {
        convertMessages(config, section, replacedSection);
        if (config.contains("PlacePrefix")) {
            ConfigurationSection placePrefix = config.getConfigurationSection("PlacePrefix");
            setConfig(config, replacedSection + ".default-place-prefix", placePrefix.get("default"));
            placePrefix.set("default", null);
            setConfig(config, replacedSection + ".place-prefix", placePrefix);
        }
        if (config.contains(section + ".header"))
            setConfig(config, replacedSection + ".header-messages", Collections.singletonList(config.get(section + ".header")));
        if (config.contains(section + ".footer"))
            setConfig(config, replacedSection + ".footer-messages", Collections.singletonList(config.get(section + ".footer")));
    }
    
    private void convertMessages(FileConfiguration config, String section, String replacedSection) {
        setConfig(config, replacedSection + ".delay", config.get(section + ".delay"));
        if (config.contains(section + ".message")) {
            setConfig(config, replacedSection + ".messages", Collections.singletonList(config.getString(section + ".message")));
        } else if (config.contains(section + ".messages")) {
            setConfig(config, replacedSection + ".messages", config.getStringList(section + ".messages"));
        }
        if (config.contains(section + ".title")) {
            String replaced = replacedSection + (config.getBoolean(section + ".title.enabled", true) ? ".title" : ".title-disabled");
            setConfig(config, replaced + ".delay", config.get(section + ".title.delay"));
            setConfig(config, replaced + ".title", config.get(section + ".title.title"));
            setConfig(config, replaced + ".sub-title", config.get(section + ".title.subTitle"));
            setConfig(config, replaced + ".fade-in", config.get(section + ".title.fadeIn"));
            setConfig(config, replaced + ".stay", config.get(section + ".title.stay"));
            setConfig(config, replaced + ".fade-out", config.get(section + ".title.fadeOut"));
        }
        if (config.contains(section + ".actionbar")) {
            String replaced = replacedSection + (config.getBoolean(section + ".actionbar.enabled", true) ? ".actionbar" : ".actionbar-disabled");
            setConfig(config, replaced + ".delay", config.get(section + ".actionbar.delay"));
            setConfig(config, replaced + ".message", config.get(section + ".actionbar.message"));
        }
    }
    
    private void setConfig(FileConfiguration config, String path, Object value) {
        config.set("mob-messages.default." + path, value);
    }
}
