package com.example.homeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class HomePlugin extends JavaPlugin {
    private HomeManager homeManager;
    private HomeInviteManager inviteManager;
    private LanguageManager languageManager;
    
    // Códigos ANSI para colores en consola
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
    
    @Override
    public void onEnable() {
        // Cargar configuración
        saveDefaultConfig();
        reloadConfig();
        
        // Configurar idioma por defecto si no existe
        if (!getConfig().contains("language")) {
            getConfig().set("language.current", "en");
            getConfig().set("language.available", new String[]{"en", "es"});
            saveConfig();
        }
        
        // Inicializar gestor de idiomas
        this.languageManager = new LanguageManager(this);
        
        logColorido(ANSI_PURPLE, "============================================");
        logColorido(ANSI_PURPLE, "   JersuxsHome v" + getDescription().getVersion() + " habilitado!   ");
        logColorido(ANSI_PURPLE, "============================================");
        logColorido(ANSI_CYAN, "Autor: " + getDescription().getAuthors());
        logColorido(ANSI_CYAN, "API Version: " + getDescription().getAPIVersion());
        logColorido(ANSI_GREEN, "¡Plugin cargado correctamente!");
        
        // Inicializar managers
        this.homeManager = new HomeManager(this);
        this.inviteManager = new HomeInviteManager(this);
        
        // Crear instancia del TabCompleter
        HomeTabCompleter tabCompleter = new HomeTabCompleter(this.homeManager);

        // Registrar comandos y TabCompleter
        HomeCommand homeExecutor = new HomeCommand(this, this.homeManager, this.inviteManager);
        getCommand("home").setExecutor(homeExecutor);
        getCommand("home").setTabCompleter(tabCompleter);
        
        getCommand("sethome").setExecutor(homeExecutor);
        // No necesita TabCompleter específico para sethome (solo nombre)

        getCommand("delhome").setExecutor(homeExecutor);
        getCommand("delhome").setTabCompleter(tabCompleter);

        getCommand("edithome").setExecutor(homeExecutor);
        getCommand("edithome").setTabCompleter(tabCompleter);

        getCommand("homes").setExecutor(homeExecutor);
        // No necesita TabCompleter para homes (sin argumentos)

        getCommand("homeinvite").setExecutor(homeExecutor);
        getCommand("homeinvite").setTabCompleter(tabCompleter);
        
        getCommand("homevisit").setExecutor(homeExecutor);
        getCommand("homevisit").setTabCompleter(tabCompleter);
        
        // Registrar comando homereload
        getCommand("homereload").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!sender.hasPermission("homeplugin.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            languageManager.getMessage("no-permission")));
                    return true;
                }
                
                // Recargar configuración y archivos de idioma
                reloadConfig();
                languageManager.loadLanguage();
                
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        languageManager.getMessage("config-reloaded")));
                return true;
            }
        });
    }
    
    @Override
    public void onDisable() {
        logColorido(ANSI_PURPLE, "============================================");
        logColorido(ANSI_PURPLE, "   JersuxsHome v" + getDescription().getVersion() + " deshabilitado   ");
        logColorido(ANSI_PURPLE, "============================================");
    }
    
    /**
     * Método para imprimir mensajes con colores en la consola
     * @param color Código ANSI del color
     * @param mensaje Mensaje a mostrar
     */
    private void logColorido(String color, String mensaje) {
        System.out.println(color + "[JersuxsHome] " + mensaje + ANSI_RESET);
    }
    
    /**
     * Recarga la configuración y los archivos de idioma
     */
    public void reloadPlugin() {
        reloadConfig();
        languageManager.loadLanguage();
    }
}