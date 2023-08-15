package com.github.xhexed.leadermobs.data;

import com.github.xhexed.leadermobs.config.mobmessage.requirement.TotalDamageRequirement;
import lombok.Getter;

@Getter
public class MobDamageTracker {
    private DamageTracker dealtDamageTracker;
    private DamageTracker takenDamageTracker;

    public MobDamageTracker() {
        dealtDamageTracker = new DamageTracker(this);
        takenDamageTracker = new DamageTracker(this);
    }

    public void calculateTop(TotalDamageRequirement damageRequirement) {
        dealtDamageTracker.calculate(damageRequirement.getDamageDealtRequired());
        takenDamageTracker.calculate(damageRequirement.getDamageTakenRequired());
    }
}
