package com.skywalx.listeners;

import com.skywalx.domain.AdvancementPlayer;
import com.skywalx.repository.AdvancementsRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/*
    When player unlocks advancement, award to all players and persist in repo
 */
public class PlayerUnlockAdvancementListener implements Listener {

    private final AdvancementsRepository advancementsRepository;

    public PlayerUnlockAdvancementListener(AdvancementsRepository advancementsRepository) {
        this.advancementsRepository = advancementsRepository;
    }

    // TODO: This is going to try to persist the unlock again for each online player, we should probably add some check to prevent multiple repo calls
    @EventHandler
    public void onUnlockAdvancement(PlayerAdvancementDoneEvent advancementDoneEvent) {
        CompletableFuture<Void> unlockFuture = advancementsRepository.unlock(advancementDoneEvent.getAdvancement());
        try {
            unlockFuture.get();
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> new AdvancementPlayer(onlinePlayer).unlockAdvancement(advancementDoneEvent.getAdvancement()));
        } catch (InterruptedException | ExecutionException e) {
            String advancementName = advancementDoneEvent.getAdvancement().getKey().getKey();
            Bukkit.getServer().broadcastMessage("There was an issue persisting the unlocked advancement " + advancementName);
            Bukkit.getLogger().log(Level.SEVERE, "Something went wrong persisting the unlocked advancement " + advancementName, e);
        }
    }

}
