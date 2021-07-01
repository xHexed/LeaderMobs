package com.github.xhexed.leadermobs.data;

import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;

import java.util.*;

public class MobDamageTracker {
    private Map<UUID, Double> damageDealt = new HashMap<>();
    private Map<UUID, Double> damageTaken = new HashMap<>();
    private List<DamageTracker> topDamageDealt = new ArrayList<>();
    private List<DamageTracker> topDamageTaken = new ArrayList<>();
    private double totalDamageDealt;
    private double totalDamageTaken;
    private double damageDealtRequired;
    private double damageTakenRequired;

    public MobDamageTracker(AbstractMobMessage mobMessage) {
        damageDealtRequired = mobMessage.totalDamageRequirement.damageDealtRequired;
        damageTakenRequired = mobMessage.totalDamageRequirement.damageTakenRequired;
    }

    public Map<UUID, Double> getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Map<UUID, Double> damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Map<UUID, Double> getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(Map<UUID, Double> damageTaken) {
        this.damageTaken = damageTaken;
    }

    public double getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public double getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public List<DamageTracker> getTopDamageDealt() {
        return topDamageDealt;
    }

    public List<DamageTracker> getTopDamageTaken() {
        return topDamageTaken;
    }

    public void calculateTop() {
        TopDamageResult result;

        result = calculateTop(topDamageDealt, damageDealt, damageDealtRequired);
        topDamageDealt = result.damageTrackers;
        totalDamageDealt = result.totalDamage;

        result = calculateTop(topDamageTaken, damageTaken, damageTakenRequired);
        topDamageTaken = result.damageTrackers;
        totalDamageTaken = result.totalDamage;
    }

    private TopDamageResult calculateTop(List<DamageTracker> topList, Map<UUID, Double> list, double requiredDamage) {
        if (!topList.isEmpty()) return new TopDamageResult(topList, 0.0);
        double totalDamage = 0.0;
        for (Map.Entry<UUID, Double> entry : list.entrySet()) {
            Double damage = entry.getValue();
            totalDamage += damage;
            if (damage < requiredDamage) continue;
            topList.add(new DamageTracker(damage, entry.getKey()));
        }
        topList.sort(Comparator.comparing(DamageTracker::getTotalDamage));
        return new TopDamageResult(topList, totalDamage);
    }

    private static class TopDamageResult {
        public List<DamageTracker> damageTrackers;
        public double totalDamage;

        private TopDamageResult(List<DamageTracker> damageTrackers, double totalDamage) {
            this.damageTrackers = damageTrackers;
            this.totalDamage = totalDamage;
        }
    }
}
