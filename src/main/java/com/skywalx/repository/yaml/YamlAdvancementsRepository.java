package com.skywalx.repository.yaml;

import com.skywalx.repository.AdvancementsRepository;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
    This is just a quick implementation, a database should be attached supporting async writing/reading with transaction support
 */
public class YamlAdvancementsRepository implements AdvancementsRepository {

    public static final String DATA_STORE_FILE_NAME = "unlocked_advancements.yaml";
    public static final String UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY = "unlocked_keys";

    private final YamlFile yamlFile;
    private final JavaPlugin plugin;

    public YamlAdvancementsRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        this.yamlFile = new YamlFile(plugin.getDataFolder().getAbsolutePath() + "/" + DATA_STORE_FILE_NAME);
    }

    @Override
    public CompletableFuture<Void> unlock(Advancement advancement) {
        List<String> unlockedAdvancements = yamlFile.getConfig().getStringList(UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY);
        String advancementKey = advancement.getKey().getKey();
        if (!unlockedAdvancements.contains(advancementKey)) {
            unlockedAdvancements.add(advancementKey);
            yamlFile.getConfig().set(UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY, unlockedAdvancements);
            try {
                yamlFile.getConfig().save(plugin.getDataFolder().getAbsolutePath() + "/" + DATA_STORE_FILE_NAME);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> unlockAll(Collection<Advancement> advancements) {
        List<String> unlockedAdvancements = yamlFile.getConfig().getStringList(UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY);
        List<String> toUnlockAdvancementKeys = advancements.stream()
                .map(advancement -> advancement.getKey().getKey())
                .filter(advancementKey -> !unlockedAdvancements.contains(advancementKey))
                .toList();
        if (!toUnlockAdvancementKeys.isEmpty()) {
            unlockedAdvancements.addAll(toUnlockAdvancementKeys);
            yamlFile.getConfig().set(UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY, unlockedAdvancements);
            try {
                yamlFile.getConfig().save(plugin.getDataFolder().getAbsolutePath() + "/" + DATA_STORE_FILE_NAME);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<Advancement>> getAllSharedUnlockedAdvancements() {
        List<String> unlockedKeys = yamlFile.getConfig().getStringList(UNLOCKED_ADVANCEMENTS_LIST_CONFIG_KEY);
        List<Advancement> globallyUnlockedAdvancements = new ArrayList<>();
        Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
        while (advancementIterator.hasNext()) {
            Advancement advancement = advancementIterator.next();
            if (unlockedKeys.contains(advancement.getKey().getKey())) {
                globallyUnlockedAdvancements.add(advancement);
            }
        }

        return CompletableFuture.completedFuture(globallyUnlockedAdvancements);
    }
}
