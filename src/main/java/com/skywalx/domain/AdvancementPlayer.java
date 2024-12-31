package com.skywalx.domain;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AdvancementPlayer {

    private final Player player;

    public AdvancementPlayer(Player player) {
        this.player = player;
    }

    public List<Advancement> getUnlockedAdvancements() {
        List<Advancement> unlockedAdvancements = new ArrayList<>();
        Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
        while (advancementIterator.hasNext()) {
            Advancement advancement = advancementIterator.next();
            if (player.getAdvancementProgress(advancement).isDone()) {
                unlockedAdvancements.add(advancement);
            }
        }
        return unlockedAdvancements;
    }

    public void unlockAdvancement(Advancement advancement) {
        AdvancementProgress advancementProgress = this.player.getAdvancementProgress(advancement);
        advancementProgress.getRemainingCriteria().forEach(advancementProgress::awardCriteria);
    }

    public void unlockAdvancements(Collection<Advancement> advancements) {
        advancements.forEach(this::unlockAdvancement);
    }

}
