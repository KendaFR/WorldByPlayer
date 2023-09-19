package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.commands.*;
import org.bukkit.Bukkit;

@SuppressWarnings("all")
public class CommandManager implements IManager {
    /**
     * Register all command
     */
    @Override
    public void register() {
        Bukkit.getPluginCommand("hub").setExecutor(new HubCmd());
        Bukkit.getPluginCommand("worldconfig").setExecutor(new WorldConfigCmd());
        Bukkit.getPluginCommand("world").setExecutor(new WorldCmd());
        Bukkit.getPluginCommand("worldadmin").setExecutor(new WorldAdminCmd());
        Bukkit.getPluginCommand("menu").setExecutor(new NavigationCmd());
        Bukkit.getPluginCommand("spawn").setExecutor(new SpawnCmd());
    }
}
