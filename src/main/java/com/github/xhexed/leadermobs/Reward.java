package com.github.xhexed.leadermobs;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

import static com.github.xhexed.leadermobs.Utils.DAMAGE_POS;
import static com.github.xhexed.leadermobs.Utils.PLAYER_NAME;

public class Reward extends Thread {
    private final HashMap<Integer, String> data;
    private final String mobname;

    public Reward(final String mobname, final HashMap<Integer, String> data) {
        this.data      = data;
        this.mobname = mobname;
        start();
    }

    @Override
    public final void start() {
        final LeaderMobs instance = LeaderMobs.getInstance();
        final Map<Integer, List<String>> rewards_final = new HashMap<>();
        if (LeaderMobs.debug && data != null) {
            instance.debug("Start calculating rewards for mob: " + mobname);
            for (final Map.Entry<Integer, String> entry : data.entrySet()) {
                instance.debug("Data: Place " + entry.getKey() + ", name: " + entry.getValue());
            }
            for (final Integer ignored : data.keySet()) {
                final YamlConfiguration rconfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "rewards.yml"));
                if (!rconfig.contains(mobname)) {
                    instance.debug("Rewards for boss: " + mobname + " not found...");
                    interrupt();
                    return;
                }
                setRewards(rewards_final, rconfig);
            }
            instance.debug("Starting calculating final rewards for boss: " + mobname);
            rewards_final.forEach((key, value) -> {
                for (final String reward_command : value) {
                    instance.debug("Place: " + key + ", commands: " + reward_command);
                }
            });
            final Server server = instance.getServer();
            final CommandSender sender = instance.getServer().getConsoleSender();
            for (final Map.Entry<Integer, List<String>> entry : rewards_final.entrySet()) {
                final Integer place = entry.getKey();
                if (data.get(place) == null) continue;
                for (String command : entry.getValue()) {
                    instance.debug("Pos to exec reward: " + place + " - " + data.get(place));
                    command = PLAYER_NAME.matcher(command).replaceAll(Objects.requireNonNull(Bukkit.getPlayer(data.get(place))).getName());
                    command = DAMAGE_POS.matcher(command).replaceAll(place.toString());
                    server.dispatchCommand(sender, command);
                    instance.debug("Executed reward command: " + command + ", for boss: " + mobname);
                }
            }
        }
        else {
            if (data == null) return;
            for (final Integer ignored : data.keySet()) {
                final File rfile = new File(instance.getDataFolder(), "rewards.yml");
                final YamlConfiguration rconfig = YamlConfiguration.loadConfiguration(rfile);
                if (rconfig.getConfigurationSection(mobname) == null) {
                    interrupt();
                    return;
                }
                setRewards(rewards_final, rconfig);
            }
            for (final Map.Entry<Integer, List<String>> entry : rewards_final.entrySet()) {
                final Integer place = entry.getKey();
                if (data.get(place) == null) continue;
                entry.getValue().stream()
                        .map(command -> PLAYER_NAME.matcher(command).replaceAll(Objects.requireNonNull(Bukkit.getPlayer(data.get(place))).getName()))
                        .map(command -> DAMAGE_POS.matcher(command).replaceAll(place.toString()))
                        .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
            }
        }
    }

    private void setRewards(final Map<? super Integer, ? super List<String>> rewards_final, final ConfigurationSection config) {
        for (final String reward_places : Objects.requireNonNull(config.getConfigurationSection(mobname)).getKeys(false)) {
            rewards_final.put(Integer.valueOf(reward_places), new ArrayList<>(config.getStringList(mobname + "." + reward_places + ".rewards")));
        }
    }
}
