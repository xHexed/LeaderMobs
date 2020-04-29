package com.github.xhexed.leadermobs.data;

import java.util.HashMap;
import java.util.Map;

public class MobDamageInfo {
    private final Map<String, Double> damageDealt;
    private final Map<String, Double> damageTaken;

    public MobDamageInfo(final Map<String, Double> damageDealt, final Map<String, Double> damageTaken) {
        this.damageDealt = new HashMap<>(damageDealt);
        this.damageTaken = new HashMap<>(damageTaken);
    }

    public Map<String, Double> getDamageDealt() {
        return damageDealt;
    }

    public Map<String, Double> getDamageTaken() {
        return damageTaken;
    }
}
