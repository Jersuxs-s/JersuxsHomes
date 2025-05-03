package com.example.homeplugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor; // Importar ChatColor
import java.util.HashMap;
import java.util.Map;
import com.example.homeplugin.VersionCompatibility;

public class HomeManager {
    private Map<String, Map<String, Location>> playerHomes;
    private HomePlugin plugin;
    private HomeStorageManager storageManager;
    
    public HomeManager(HomePlugin plugin) {
        this.playerHomes = new HashMap<>();
        this.plugin = plugin;
        this.storageManager = new HomeStorageManager(plugin);
        
        // Cargar datos al iniciar
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            playerHomes.put(player.getUniqueId().toString(), 
                storageManager.loadHomes(player.getUniqueId()));
        });
    }
    
    public boolean setHome(Player player, String homeName) {
        String playerId = player.getUniqueId().toString();
        
        // Verificar límite de homes
        int maxHomes = plugin.getConfig().getInt("max-homes", 5);
        if (maxHomes > 0 && playerHomes.getOrDefault(playerId, new HashMap<>()).size() >= maxHomes) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("max-homes")
                    .replace("{max}", String.valueOf(maxHomes))));
            return false;
        }
        
        // Verificar si el mundo está permitido
        if (!plugin.getConfig().getBoolean("world-permissions." + player.getWorld().getName(), true)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("world-not-allowed")));
            return false;
        }
        
        // Establecer home
        playerHomes.computeIfAbsent(playerId, k -> new HashMap<>()).put(homeName, player.getLocation());
        storageManager.saveHomes(player.getUniqueId(), playerHomes.get(playerId));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("home-set")
                .replace("{home}", homeName)));
        return true;
    }
    
    public boolean deleteHome(Player player, String homeName) {
        String playerId = player.getUniqueId().toString();
        
        if (!playerHomes.containsKey(playerId) || !playerHomes.get(playerId).containsKey(homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("home-not-found")
                    .replace("{home}", homeName)));
            return false;
        }
        
        playerHomes.get(playerId).remove(homeName);
        storageManager.saveHomes(player.getUniqueId(), playerHomes.get(playerId));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("home-deleted")
                .replace("{home}", homeName)));
        return true;
    }
    
    public boolean renameHome(Player player, String oldName, String newName) {
        String playerId = player.getUniqueId().toString();
        
        if (!playerHomes.containsKey(playerId) || !playerHomes.get(playerId).containsKey(oldName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("home-not-found")
                    .replace("{home}", oldName)));
            return false;
        }
        
        Location homeLocation = playerHomes.get(playerId).get(oldName);
        playerHomes.get(playerId).remove(oldName);
        playerHomes.get(playerId).put(newName, homeLocation);
        storageManager.saveHomes(player.getUniqueId(), playerHomes.get(playerId));
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("home-renamed")
                .replace("{old}", oldName)
                .replace("{new}", newName)));
        return true;
    }
    
    public Location getHome(Player player, String homeName) {
        String playerId = player.getUniqueId().toString();
        
        if (!playerHomes.containsKey(playerId) || !playerHomes.get(playerId).containsKey(homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("home-not-found")
                    .replace("{home}", homeName)));
            return null;
        }
        
        return playerHomes.get(playerId).get(homeName);
    }
    
    public Map<String, Location> getHomes(Player player) {
        String playerId = player.getUniqueId().toString();
        return playerHomes.getOrDefault(playerId, new HashMap<>());
    }
}