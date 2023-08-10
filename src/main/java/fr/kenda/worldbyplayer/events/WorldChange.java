package fr.kenda.worldbyplayer.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World from = e.getFrom();
        World current = player.getWorld();
        if (from == Bukkit.getWorlds().get(0)) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }
        if (current == Bukkit.getWorlds().get(0)) {
            PlayerJoin.giveLobbyInventory(player);
        }
    }
}
