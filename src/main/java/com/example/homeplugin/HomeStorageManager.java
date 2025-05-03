package com.example.homeplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeStorageManager {
    private final HomePlugin plugin;
    private final File dataFolder;
    
    public HomeStorageManager(HomePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "homes");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
    
    public void saveHomes(UUID playerId, Map<String, Location> homes) {
        File playerFile = getPlayerFile(playerId);
        YamlConfiguration config = new YamlConfiguration();
        
        homes.forEach((homeName, location) -> {
            String path = "homes." + homeName;
            config.set(path + ".world", location.getWorld().getName());
            config.set(path + ".x", location.getX());
            config.set(path + ".y", location.getY());
            config.set(path + ".z", location.getZ());
            config.set(path + ".yaw", location.getYaw());
            config.set(path + ".pitch", location.getPitch());
        });
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar homes: " + e.getMessage());
        }
    }
    
    public Map<String, Location> loadHomes(UUID playerId) {
        File playerFile = getPlayerFile(playerId);
        if (!playerFile.exists()) {
            return new HashMap<>();
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        Map<String, Location> homes = new HashMap<>();
        
        if (config.contains("homes")) {
            for (String homeName : config.getConfigurationSection("homes").getKeys(false)) {
                String path = "homes." + homeName;
                Location location = new Location(
                    Bukkit.getWorld(config.getString(path + ".world")),
                    config.getDouble(path + ".x"),
                    config.getDouble(path + ".y"),
                    config.getDouble(path + ".z"),
                    (float) config.getDouble(path + ".yaw"),
                    (float) config.getDouble(path + ".pitch")
                );
                homes.put(homeName, location);
            }
        }
        
        return homes;
    }
    
    private File getPlayerFile(UUID playerId) {
        return new File(dataFolder, playerId.toString() + ".yml");
    }
}