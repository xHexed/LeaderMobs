package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.configuration.ConfigurationSection;

public class MobDeathMessage {
    private DamageMessage damageDealtMessage;
    private DamageMessage damageTakenMessage;

    public MobDeathMessage(MobMessage mobMessage, ConfigurationSection config) {
        damageDealtMessage = new DamageMessage(mobMessage.getPlugin(), mobMessage, config.getConfigurationSection("damage-dealt"));
        damageTakenMessage = new DamageMessage(mobMessage.getPlugin(), mobMessage, config.getConfigurationSection("damage-taken"));
    }

    public void sendMessages(MobDamageTracker tracker, MobData data) {
        damageDealtMessage.sendMessages(tracker.getDealtDamageTracker(), data);
        damageTakenMessage.sendMessages(tracker.getTakenDamageTracker(), data);
    }
}
