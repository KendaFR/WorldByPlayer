package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.commands.HubCmd;
import fr.kenda.worldbyplayer.commands.WorldCmd;
import org.bukkit.Bukkit;

public class CommandManager implements IManager {
    @Override
    public void register() {
        Bukkit.getPluginCommand("hub").setExecutor(new HubCmd());
        Bukkit.getPluginCommand("world").setExecutor(new WorldCmd());
    }
}
