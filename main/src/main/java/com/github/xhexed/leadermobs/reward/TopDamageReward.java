package com.github.xhexed.leadermobs.reward;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.DamageTracker;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.util.PlaceholderParser.*;

public class TopDamageReward {
    private LeaderMobs plugin;
    private DamageReward damageDealtRewards;
    private DamageReward damageTakenRewards;

    public TopDamageReward(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        if (config.contains("dealt")) {
            damageDealtRewards = new DamageReward(Objects.requireNonNull(config.getConfigurationSection("dealt")));
        }
        if (config.contains("taken")) {
            damageTakenRewards = new DamageReward(Objects.requireNonNull(config.getConfigurationSection("taken")));
        }
    }

    public void giveRewards(MobDamageTracker info) {
        if (damageDealtRewards != null) {
            giveRewards(damageDealtRewards.placeRewards,
                    info.getDealtDamageTracker().getTopDamageResult().getDamageData(),
                    info.getDealtDamageTracker().getTopDamageResult().getTotalDamage(),
                    DAMAGE_DEALT, DAMAGE_DEALT_PERCENTAGE);
        }
        if (damageTakenRewards != null) {
            giveRewards(damageTakenRewards.placeRewards,
                    info.getTakenDamageTracker().getTopDamageResult().getDamageData(),
                    info.getDealtDamageTracker().getTopDamageResult().getTotalDamage(),
                    DAMAGE_TAKEN, DAMAGE_TAKEN_PERCENTAGE);
        }
    }

    private void giveRewards(List<DamageReward.DamagePlaceReward> rewards,
                             List<DamageTracker.DamageData> topList,
                             double totalDamage,
                             Pattern damageFormat,
                             Pattern percentageFormat) {
        for (DamageReward.DamagePlaceReward reward : rewards) {
            int i = reward.place - 1;
            if (i >= topList.size()) break;
            DamageTracker.DamageData info = topList.get(i);
            UUID uuid = info.getTracker();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            for (String command : reward.commands) {
                command = PLAYER_NAME.matcher(command).replaceAll(player.getName());
                command = DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1));
                command = damageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(info.getTotalDamage()));
                command = percentageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(getPercentage(info.getTotalDamage(), totalDamage)));
                command = plugin.getMessageParser().replacePlaceholder(player, command);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }
}
