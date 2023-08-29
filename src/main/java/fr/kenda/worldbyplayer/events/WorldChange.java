package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.managers.FileManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {

    private final FileManager fileManager = WorldByPlayer.getInstance().getFileManager();

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        final Player player = e.getPlayer();
        final World from = e.getFrom();
        final World current = player.getWorld();
        final World lobby = Bukkit.getWorlds().get(0);

        final FileConfiguration savedPlayers = fileManager.getConfigFrom("saved_players");


        //world player
        if (current != lobby) {
            player.getInventory().clear();
            String worldFree = Config.getString("worlds.nameMap");
            DataWorld currentWorld = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromPlayerWorldOwner(current.getName());
            player.sendMessage(WorldByPlayer.getInstance().getPrefix() + Messages.getMessage("teleported_in", "{world}",
                    currentWorld == null ? worldFree : currentWorld.getName()));

            if (savedPlayers != null)
                SavePlayerUtils.loadPlayerData(player, current, savedPlayers);
            if (!current.getName().equalsIgnoreCase(player.getName())) return;
            DataWorld dw = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromPlayerWorldOwner(player);
            //is own world
            if (dw != null && current == dw.getWorld())
                dw.updateTimeLastLogin();


            return;
        }

        //Lobby
        if (savedPlayers != null) {
            SavePlayerUtils.savePlayerData(player, from, savedPlayers);

            PlayerJoin.giveLobbyInventory(player);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setExp(0);
            player.setLevel(0);
        }
    }
}
