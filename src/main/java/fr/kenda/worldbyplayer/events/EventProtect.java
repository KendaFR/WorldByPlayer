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

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player player)) return;
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        World lobbyWorld = Bukkit.getWorld("world");
        e.setCancelled(player.getWorld() == lobbyWorld);
    }

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
