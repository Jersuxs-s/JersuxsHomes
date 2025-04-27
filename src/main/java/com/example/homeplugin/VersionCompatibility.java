package com.example.homeplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

public class VersionCompatibility {
    private static Boolean hasNewMethods = null;
    
    public static boolean supportsNewMethods() {
        if (hasNewMethods == null) {
            try {
                // Verificar si estamos en versión 1.19+
                String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                int major = Integer.parseInt(version.split("_")[1]);
                
                if (major >= 19) {
                    // Verificar si los nuevos métodos existen
                    Player.class.getMethod("getPing");
                    hasNewMethods = true;
                } else {
                    hasNewMethods = false;
                }
            } catch (Exception e) {
                hasNewMethods = false;
            }
        }
        return hasNewMethods;
    }
    
    public static int getPlayerPing(Player player) {
        try {
            if (supportsNewMethods()) {
                return player.getPing();
            } else {
                // Implementación alternativa para versiones anteriores
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
            }
        } catch (Exception e) {
            return -1;
        }
    }
}