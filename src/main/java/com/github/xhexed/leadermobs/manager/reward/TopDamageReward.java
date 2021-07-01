package com.github.xhexed.leadermobs.manager.reward;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.DamageTracker;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.util.Util.*;

public class TopDamageReward {
    private LeaderMobs plugin;
    private DamageReward damageDealtRewards;
    private DamageReward damageTakenRewards;

    public TopDamageReward(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        damageDealtRewards = new DamageReward(Objects.requireNonNull(config.getConfigurationSection("dealt")));
        damageTakenRewards = new DamageReward(Objects.requireNonNull(config.getConfigurationSection("taken")));
    }

    public void giveRewards(MobDamageTracker info) {
        giveRewards(damageDealtRewards.placeRewards, info.getTopDamageDealt(), info.getTotalDamageDealt(), DAMAGE_DEALT, DAMAGE_DEALT_PERCENTAGE);
        giveRewards(damageTakenRewards.placeRewards, info.getTopDamageTaken(), info.getTotalDamageTaken(), DAMAGE_TAKEN, DAMAGE_TAKEN_PERCENTAGE);
    }

    private void giveRewards(List<DamageReward.DamagePlaceReward> rewards,
                             List<DamageTracker> topList,
                             double totalDamage,
                             Pattern damageFormat,
                             Pattern percentageFormat) {
        for (DamageReward.DamagePlaceReward reward : rewards) {
            int i = reward.place - 1;
            if (i >= topList.size()) break;
            DamageTracker info = topList.get(i);
            UUID uuid = info.getTracker();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            reward.commands.stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(player.getName()))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1)))
                    .map(command -> damageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(info.getTotalDamage())))
                    .map(command -> percentageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(getPercentage(info.getTotalDamage(), totalDamage))))
                    .map(command -> plugin.getPluginUtil().replacePlaceholder(player, command))
                    .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
        }
    }
}
