package fr.kenda.worldbyplayer.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemProtect implements Listener {

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
}
