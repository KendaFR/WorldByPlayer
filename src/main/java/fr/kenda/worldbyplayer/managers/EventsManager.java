package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.events.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventsManager implements IManager {
    @Override
    public void register() {
        final WorldByPlayer instance = WorldByPlayer.getInstance();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(), instance);
    }
}
