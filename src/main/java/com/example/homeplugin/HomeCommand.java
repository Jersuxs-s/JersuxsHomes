package com.example.homeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor; // Importar ChatColor
import java.util.Map;
import com.example.homeplugin.VersionCompatibility;

public class HomeCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        
        Player player = (Player) sender;
        
        if (args.length == 1) {
            // Autocompletar nombres de homes para comandos que usan un solo argumento
            if (cmd.getName().equalsIgnoreCase("home") || 
                cmd.getName().equalsIgnoreCase("delhome") || 
                cmd.getName().equalsIgnoreCase("edithome")) {
                return new ArrayList<>(homeManager.getHomes(player).keySet());
            }
            
            // Autocompletar nombres de jugadores para homevisit
            if (cmd.getName().equalsIgnoreCase("homevisit")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            }
        }
        
        if (args.length == 2 && cmd.getName().equalsIgnoreCase("edithome")) {
            // No autocompletar para el nuevo nombre en edithome
            return Collections.emptyList();
        }
        
        if (args.length == 2 && cmd.getName().equalsIgnoreCase("homevisit")) {
            // Autocompletar homes del jugador objetivo
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                return new ArrayList<>(homeManager.getHomes(target).keySet());
            }
        }
        
        return Collections.emptyList();
    }
    
    private HomePlugin plugin;
    private HomeManager homeManager;
    
    private HomeInviteManager inviteManager;
    
    public HomeCommand(HomePlugin plugin, HomeManager homeManager, HomeInviteManager inviteManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.inviteManager = inviteManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-console-only")));
            return true;
        }
        
        Player player = (Player) sender;
        
        switch (cmd.getName().toLowerCase()) {
            case "home":
                return handleHomeCommand(player, args);
            case "sethome":
                return handleSetHomeCommand(player, args);
            case "delhome":
                return handleDelHomeCommand(player, args);
            case "edithome":
                return handleEditHomeCommand(player, args);
            case "homes":
                return handleListHomesCommand(player);
            case "homeinvite":
                return handleHomeInviteCommand(player, args);
            case "homevisit":
                return handleHomeVisitCommand(player, args);
            default:
                return false;
        }
    }
    
    private boolean handleHomeCommand(Player player, String[] args) {
        String homeName = args.length > 0 ? args[0] : "default";
        Location home = homeManager.getHome(player, homeName);
        
        if (home == null) {
            // El mensaje de error ya se envía desde HomeManager con colores
            return true;
        }
        
        int delay = plugin.getConfig().getInt("teleport-delay", 3);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("teleporting")
                .replace("{delay}", String.valueOf(delay))));
        
        // Programar teleportación con delay
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getConfig().getBoolean("teleport-effects.particles", true)) {
                    player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50);
                }
                if (plugin.getConfig().getBoolean("teleport-effects.sound", true)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                }
                player.teleport(home);
            }
        }.runTaskLater(plugin, delay * 20L);
        
        return true;
    }
    
    private boolean handleSetHomeCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-usage-sethome")));
            return false;
        }
        
        return homeManager.setHome(player, args[0]);
    }
    
    private boolean handleDelHomeCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-usage-delhome")));
            return false;
        }
        
        return homeManager.deleteHome(player, args[0]);
    }
    
    private boolean handleEditHomeCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-usage-edithome")));
            return false;
        }
        
        return homeManager.renameHome(player, args[0], args[1]);
    }
    
    private boolean handleListHomesCommand(Player player) {
        Map<String, Location> homes = homeManager.getHomes(player);
        
        if (homes.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("no-homes")));
            return true;
        }
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("your-homes")));
        homes.keySet().forEach(home -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &e" + home)));
        
        return true;
    }
    
    private boolean handleHomeInviteCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-usage-homeinvite")));
            return false;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("player-not-found")));
            return false;
        }
        
        String homeName = args[1];
        if (homeManager.getHome(player, homeName) == null) {
            // El mensaje de error ya se envía desde HomeManager con colores
            return false;
        }
        
        inviteManager.sendInvite(player, target, homeName);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getLanguageManager().getMessage("invite-sent")
                .replace("{player}", target.getName())
                .replace("{home}", homeName)));
        
        // El mensaje interactivo ahora se envía desde HomeInviteManager
        
        return true;
    }
    
    private boolean handleHomeVisitCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("command-usage-homevisit")));
            return false;
        }
        
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("player-not-found")));
            return false;
        }
        
        String homeName = args[1];
        String targetUUID = targetPlayer.getUniqueId().toString();
        
        // Verificar si la invitación existe y es válida
        if (!inviteManager.hasPendingInvite(player, targetUUID, homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("no-valid-invitation")));
            return false;
        }
        
        // Aceptar la invitación y teleportar al jugador
        if (inviteManager.acceptInvite(player, targetUUID, homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("visiting-home")
                    .replace("{home}", homeName)
                    .replace("{player}", targetName)));
            return true;
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getLanguageManager().getMessage("visit-failed")));
            return false;
        }
    }
}