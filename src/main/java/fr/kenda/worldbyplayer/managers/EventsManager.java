package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.events.ChatCreatingWorld;
import fr.kenda.worldbyplayer.events.OpenInventory;
import fr.kenda.worldbyplayer.events.PlayerJoin;
import fr.kenda.worldbyplayer.events.WorldChange;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventsManager implements IManager {
    @Override
    public void register() {
        final WorldByPlayer instance = WorldByPlayer.getInstance();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(), instance);
        pm.registerEvents(new OpenInventory(), instance);
        pm.registerEvents(new WorldChange(), instance);
        pm.registerEvents(new ChatCreatingWorld(), instance);
    }
}
