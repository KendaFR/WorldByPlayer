package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    /**
     * Saves player information when leaving the server
     *
     * @param e PlayerQuitEvent
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();

        if(player.getWorld() == Bukkit.getWorlds().get(0)) return;

        final FileConfiguration savedPlayers = WorldByPlayer.getInstance().getFileManager().getConfigFrom("saved_players");
        SavePlayerUtils.savePlayerData(player, player.getWorld(), savedPlayers);

    }
}
