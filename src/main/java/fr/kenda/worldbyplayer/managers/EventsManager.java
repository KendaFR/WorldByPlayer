package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class
EventsManager implements IManager {
    /**
     * Register all events
     */
    @Override
    public void register() {
        final WorldByPlayer instance = WorldByPlayer.getInstance();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(), instance);
        pm.registerEvents(new OnRespawn(), instance);
        pm.registerEvents(new PlayerQuit(), instance);
        pm.registerEvents(new OpenInventory(), instance);
        pm.registerEvents(new WorldChange(), instance);
        pm.registerEvents(new ChatCreatingWorld(), instance);
        pm.registerEvents(new EventProtect(), instance);
        pm.registerEvents(new PlayerChat(), instance);
    }
}