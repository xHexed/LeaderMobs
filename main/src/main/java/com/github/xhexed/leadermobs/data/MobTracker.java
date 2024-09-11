package com.github.xhexed.leadermobs.data;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MobTracker {
    MobMessage mobMessage;
    MobDamageTracker damageTracker;
}
