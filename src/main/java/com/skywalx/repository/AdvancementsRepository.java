package com.skywalx.repository;

import org.bukkit.advancement.Advancement;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AdvancementsRepository {

    CompletableFuture<Void> unlock(Advancement advancement);
    CompletableFuture<Void> unlockAll(Collection<Advancement> advancements);
    CompletableFuture<List<Advancement>> getAllSharedUnlockedAdvancements();

}
