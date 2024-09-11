package com.github.xhexed.leadermobs.config.mobmessage.checker;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class MobCheckerTest {
    private MobChecker checker;

    @BeforeMethod
    public void init() {
        checker = new MobChecker();
    }

    public void testOnlyMobNameCondition() {
        MobMessage mobMessage = new MobMessage();
        mobMessage.setMobConditions(List.of(
                new MobCondition("test")
        ));
        Assert.assertTrue(checker.check(mobMessage, "", "test", null));
        Assert.assertFalse(checker.check(mobMessage, "", "tester", null));
    }

    public void testOnlyPluginName() {
        MobMessage mobMessage = new MobMessage();
        mobMessage.setMobConditions(List.of(
                new MobCondition("MythicMobs;")
        ));
        Assert.assertTrue(checker.check(mobMessage, "MythicMobs", "", null));
        Assert.assertFalse(checker.check(mobMessage, "mYtHiCmObS", "", null));
    }

    public void testOnlyMobCondition() {
        checker.addChecker("name", ((entity, mobMessage) -> "a"));
        checker.addChecker("type", ((entity, mobMessage) -> "c"));
        MobMessage mobMessage = new MobMessage();
        mobMessage.setMobConditions(List.of(
                new MobCondition("@name=(a|b)@type=(c|d);")
        ));
        Assert.assertTrue(checker.check(mobMessage, "", "", null));
    }
}
