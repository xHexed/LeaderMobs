package com.github.xhexed.leadermobs.data;

import lombok.Getter;

import java.util.*;

@Getter
public class DamageTracker {
    private MobDamageTracker tracker;
    private Map<UUID, Double> damageTracker = new HashMap<>();
    private TopDamageResult topDamageResult;

    public DamageTracker(MobDamageTracker tracker) {
        this.tracker = tracker;
    }

    public void addDamage(UUID player, double damage) {
        damageTracker.put(player, damage + damageTracker.getOrDefault(player, 0D));
    }

    public void calculate(double requiredDamage) {
        topDamageResult = new TopDamageResult(requiredDamage);
    }

    @Getter
    public class TopDamageResult {
        private List<DamageData> damageData = new ArrayList<>();
        private double totalDamage;

        public TopDamageResult(double requiredDamage) {
            if (damageTracker.isEmpty()) return;
            for (Map.Entry<UUID, Double> entry : damageTracker.entrySet()) {
                Double damage = entry.getValue();
                totalDamage += damage;
                if (damage < requiredDamage) continue;
                damageData.add(new DamageData(damage, entry.getKey()));
            }
            damageData.sort((l, r) -> Double.compare(r.getTotalDamage(), l.getTotalDamage()));
        }
    }

    @Getter
    public static class DamageData {
        private double totalDamage;
        private UUID tracker;

        public DamageData(double totalDamage, UUID tracker) {
            this.totalDamage = totalDamage;
            this.tracker = tracker;
        }
    }
}
