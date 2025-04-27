package com.example.homeplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private HomePlugin plugin;
    private String language;
    private Map<String, String> messages;
    
    public LanguageManager(HomePlugin plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
        loadLanguage();
    }
    
    public void loadLanguage() {
        // Obtener el idioma de la configuración
        this.language = plugin.getConfig().getString("language.current", "en");
        
        // Limpiar mensajes anteriores
        this.messages.clear();
        
        // Cargar archivo de idioma
        File langFile = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
        FileConfiguration langConfig;
        
        if (langFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
        } else {
            // Si el archivo no existe, cargarlo desde los recursos
            Reader defaultLangStream = new InputStreamReader(
                plugin.getResource("messages_" + language + ".yml"), 
                StandardCharsets.UTF_8
            );
            
            if (defaultLangStream != null) {
                langConfig = YamlConfiguration.loadConfiguration(defaultLangStream);
                try {
                    defaultLangStream.close();
                } catch (IOException e) {
                    plugin.getLogger().warning("Error al cerrar el stream de idioma: " + e.getMessage());
                }
            } else {
                // Si no se encuentra el idioma, usar inglés como respaldo
                plugin.getLogger().warning("No se encontró el archivo de idioma para '" + language + "', usando inglés como respaldo.");
                this.language = "en";
                
                Reader defaultEnStream = new InputStreamReader(
                    plugin.getResource("messages_en.yml"), 
                    StandardCharsets.UTF_8
                );
                
                if (defaultEnStream != null) {
                    langConfig = YamlConfiguration.loadConfiguration(defaultEnStream);
                    try {
                        defaultEnStream.close();
                    } catch (IOException e) {
                        plugin.getLogger().warning("Error al cerrar el stream de idioma: " + e.getMessage());
                    }
                } else {
                    plugin.getLogger().severe("No se pudo cargar el archivo de idioma de respaldo (en). Los mensajes pueden no mostrarse correctamente.");
                    return;
                }
            }
            
            // Guardar el archivo de idioma para futuras referencias
            try {
                langConfig.save(langFile);
            } catch (IOException e) {
                plugin.getLogger().warning("No se pudo guardar el archivo de idioma: " + e.getMessage());
            }
        }
        
        // Cargar todos los mensajes en el mapa
        for (String key : langConfig.getKeys(false)) {
            messages.put(key, langConfig.getString(key));
        }
        
        plugin.getLogger().info("Idioma cargado: " + language);
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "Missing message: " + key);
    }
    
    public String getMessage(String key, String defaultValue) {
        return messages.getOrDefault(key, defaultValue);
    }
    
    public String getCurrentLanguage() {
        return this.language;
    }
}