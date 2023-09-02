package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.configuration.ConfigurationSection;

public class MobDeathMessage {
    private DamageMessage damageDealtMessage;
    private DamageMessage damageTakenMessage;

    public MobDeathMessage(MobMessage mobMessage, ConfigurationSection config) {
        ConfigurationSection damageDealtSection = config.getConfigurationSection("damage-dealt");
        if (damageDealtSection != null) {
            damageDealtMessage = new DamageMessage(mobMessage.getPlugin(), mobMessage, damageDealtSection);
        }
        ConfigurationSection damageTakenSection = config.getConfigurationSection("damage-taken");
        if (damageTakenSection != null) {
            damageTakenMessage = new DamageMessage(mobMessage.getPlugin(), mobMessage, damageTakenSection);
        }
    }

    public void sendMessages(MobDamageTracker tracker, MobData data) {
        if (damageDealtMessage != null)
            damageDealtMessage.sendMessages(tracker.getDealtDamageTracker(), data);
        if (damageTakenMessage != null)
            damageTakenMessage.sendMessages(tracker.getTakenDamageTracker(), data);
    }
}
