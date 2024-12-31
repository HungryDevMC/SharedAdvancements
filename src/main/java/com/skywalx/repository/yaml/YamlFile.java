package com.skywalx.repository.yaml;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlFile {

    private final FileConfiguration config;

    public YamlFile(String fileNameWithPath) {
        File file = new File(fileNameWithPath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException fileException) {
                Bukkit.getLogger().log(Level.SEVERE, "Error creating yaml datastore", fileException);
                throw new RuntimeException(fileException);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
