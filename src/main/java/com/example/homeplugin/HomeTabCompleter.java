package com.example.homeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeTabCompleter implements TabCompleter {

    private final HomeManager homeManager;

    public HomeTabCompleter(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions; // No autocompletar para la consola
        }

        Player player = (Player) sender;
        String commandName = command.getName().toLowerCase();

        // Autocompletar para /home <nombre>, /delhome <nombre>, /edithome <nombre_actual>
        if ((commandName.equals("home") || commandName.equals("delhome") || commandName.equals("edithome")) && args.length == 1) {
            // Siempre cargar los homes desde almacenamiento para autocompletar
            HomeStorageManager storageManager = new HomeStorageManager(homeManager.getPlugin());
            List<String> homeNames = new ArrayList<>(storageManager.loadHomes(player.getUniqueId()).keySet());
            StringUtil.copyPartialMatches(args[0], homeNames, completions);
        }
        // Autocompletar para /homeinvite <jugador> <home>
        else if (commandName.equals("homeinvite") && args.length == 1) {
            // Autocompletar nombres de jugadores online
            List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
            StringUtil.copyPartialMatches(args[0], playerNames, completions);
        }
        else if (commandName.equals("homeinvite") && args.length == 2) {
            List<String> homeNames = new ArrayList<>(homeManager.getHomes(player).keySet());
            StringUtil.copyPartialMatches(args[1], homeNames, completions);
        }
        // Autocompletar para /homevisit <jugador> <home>
        else if (commandName.equals("homevisit") && args.length == 1) {
            // Autocompletar nombres de jugadores online
            List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
            StringUtil.copyPartialMatches(args[0], playerNames, completions);
        }

        return completions.stream().sorted().collect(Collectors.toList());
    }
}