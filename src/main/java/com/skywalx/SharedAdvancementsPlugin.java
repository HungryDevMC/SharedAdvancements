package com.skywalx;

import com.skywalx.listeners.PlayerJoinListener;
import com.skywalx.listeners.PlayerUnlockAdvancementListener;
import com.skywalx.repository.AdvancementsRepository;
import com.skywalx.repository.yaml.YamlAdvancementsRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SharedAdvancementsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            AdvancementsRepository advancementsRepository = new YamlAdvancementsRepository(this);
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(advancementsRepository), this);
            getServer().getPluginManager().registerEvents(new PlayerUnlockAdvancementListener(advancementsRepository), this);
        } catch (RuntimeException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to initialize datastore, disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}