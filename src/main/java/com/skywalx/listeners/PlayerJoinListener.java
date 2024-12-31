package com.skywalx.listeners;

import com.skywalx.domain.AdvancementPlayer;
import com.skywalx.repository.AdvancementsRepository;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
    Persists all unlocked advancements of joined player, then fetches all unlocked advancement from server again and updates it for all online players
    This makes sure to keep advancements in sync, even when plugin is installed after advancements have already been unlocked by players.
 */
public class PlayerJoinListener implements Listener {

    private final AdvancementsRepository advancementsRepository;

    public PlayerJoinListener(AdvancementsRepository advancementsRepository) {
        this.advancementsRepository = advancementsRepository;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        Player joinedPlayer = joinEvent.getPlayer();
        AdvancementPlayer advancementPlayer = new AdvancementPlayer(joinedPlayer);
        List<Advancement> unlockedAdvancementsOfJoinedPlayer = advancementPlayer.getUnlockedAdvancements();
        CompletableFuture<Void> unlockFuture = advancementsRepository.unlockAll(unlockedAdvancementsOfJoinedPlayer)
                .thenRun(() -> advancementsRepository.getAllSharedUnlockedAdvancements().thenAccept(advancements ->
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> new AdvancementPlayer(onlinePlayer).unlockAdvancements(advancements))));
        try {
            unlockFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            joinedPlayer.kickPlayer("There was a problem syncing advancements with the server: " + e.getMessage());
        }
    }

}
