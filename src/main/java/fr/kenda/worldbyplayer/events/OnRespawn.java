package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnRespawn implements Listener {

    /**
     * Reset player on die
     * @param e DeathEvent
     */
    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        Player player = e.getEntity();
        final FileConfiguration savedPlayers = WorldByPlayer.getInstance().getFileManager().getConfigFrom("saved_players");
        SavePlayerUtils.resetPlayer(player, player.getWorld(), savedPlayers);
    }

    /**
     * Manages the player's spawn system, whether it's in the hub, or in the world where he died
     *
     * @param e PlayerRespawnEvent
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (Config.getBoolean("respawn_in_lobby")) {
            e.getPlayer().setHealth(20);
            e.getPlayer().setFoodLevel(20);
            e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
        } else {
            Player player = e.getPlayer();
            World world = player.getWorld();
            e.setRespawnLocation(world.getSpawnLocation());
        }
    }
}
