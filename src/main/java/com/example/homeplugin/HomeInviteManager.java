package com.example.homeplugin;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.example.homeplugin.HomePlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class HomeInviteManager {
    private Map<String, Map<String, String>> pendingInvites;
    private HomePlugin plugin;
    
    public HomeInviteManager(HomePlugin plugin) {
        this.pendingInvites = new HashMap<>();
        this.plugin = plugin;
    }
    
    public void sendInvite(Player sender, Player target, String homeName) {
        // Guardar la invitación en el mapa
        pendingInvites.computeIfAbsent(target.getUniqueId().toString(), k -> new HashMap<>())
            .put(sender.getUniqueId().toString(), homeName);
        
        // Crear mensaje interactivo
        TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("invite-received")
                .replace("{player}", sender.getName())
                .replace("{home}", homeName)));
        
        // Crear botón para aceptar
        TextComponent acceptButton = new TextComponent(ChatColor.translateAlternateColorCodes('&', 
                "&a[" + plugin.getLanguageManager().getMessage("accept-button") + "]"));
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/homevisit " + sender.getName() + " " + homeName));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder(ChatColor.GREEN + plugin.getLanguageManager().getMessage("accept-hover")).create()));
        
        // Enviar mensaje completo
        target.spigot().sendMessage(message, acceptButton);
    }
    
    public boolean acceptInvite(Player player, String senderUUID, String homeName) {
        Player sender = Bukkit.getPlayer(UUID.fromString(senderUUID));
        if (sender == null || !hasPendingInvite(player, senderUUID, homeName)) {
            return false;
        }
        
        Location homeLocation = plugin.getHomeManager().getHome(sender, homeName);
        if (homeLocation == null) {
            return false;
        }
        
        // Efectos de teleportación configurables
        if (plugin.getConfig().getBoolean("teleport-effects.particles", true)) {
            player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation(), 50);
        }
        if (plugin.getConfig().getBoolean("teleport-effects.sound", true)) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        
        player.teleport(homeLocation);
        pendingInvites.get(player.getUniqueId().toString()).remove(senderUUID);
        return true;
    }
    
    public boolean hasPendingInvite(Player player, String senderName, String homeName) {
        String playerId = player.getUniqueId().toString();
        return pendingInvites.containsKey(playerId) && 
               pendingInvites.get(playerId).containsKey(senderName) &&
               pendingInvites.get(playerId).get(senderName).equals(homeName);
    }
    
    /**
     * Limpia las invitaciones pendientes
     */
    public void clearInvites() {
        pendingInvites.clear();
    }
}