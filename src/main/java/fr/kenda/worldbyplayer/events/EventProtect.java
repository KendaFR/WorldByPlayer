package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventProtect implements Listener {

    /**
     * Remove item drop in the hub
     * @param e PlayerDropItemEvent
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Remove pickup item in the hub
     * @param e EntityPickupItemEvent
     */
    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Remove damage between entities if the player is in the hub, or in a world where pvp is enabled
     * @param e EntityDamageByEntityEvent
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld || !player.getWorld().getPVP());
    }

    /**
     * Remove damage if the player is in the hub
     * @param e EntityDamageEvent
     */
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Remove level change in hunger
     * @param e FoodLevelChangeEvent
     */
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Remove block breaking in hub
     * @param e BlockBreakEvent
     */
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Delete block pose in hub
     * @param e BlockPlaceEvent
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    /**
     * Manages the player's spawn system, whether it's in the hub, or in the world where he died
     * @param e PlayerRespawnEvent
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (Config.getBoolean("respawn_in_lobby"))
            e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
        else {
            Player player = e.getPlayer();
            World world = player.getWorld();
            e.setRespawnLocation(world.getSpawnLocation());
        }
    }
}
