package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.managers.FileManager;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.Permission;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final FileManager fileManager = instance.getFileManager();
    private final WorldsManager worldsManager = instance.getWorldManager();

    /**
     * Manages player information when changing worlds (hub world different from player world)
     * @param e PlayerChangedWorldEvent
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        final Player player = e.getPlayer();
        final World from = e.getFrom();
        final World currentWorld = player.getWorld();
        final World lobby = Bukkit.getWorlds().get(0);
        final String prefix = instance.getPrefix();

        final FileConfiguration savedPlayers = fileManager.getConfigFrom("saved_players");


        //world player
        if (currentWorld != lobby) {
            if (player.hasPermission(Permission.PERMISSION) && instance.getAdminManager().isInModeAdmin(player)) {
                player.getInventory().clear();
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(prefix + Messages.getMessage("teleported_admin"));
            } else {
                player.getInventory().clear();
                String worldFree = Config.getString("worlds.nameMap");
                DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(currentWorld.getName());
                player.sendMessage(prefix + Messages.getMessage("teleported_in", "{world}",
                        dataWorld == null ? worldFree : dataWorld.getName()));

                if (savedPlayers != null)
                    SavePlayerUtils.loadPlayerData(player, currentWorld, savedPlayers);
                if (!currentWorld.getName().equalsIgnoreCase(player.getName())) return;
                DataWorld dw = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                //is own world
                if (dw != null && currentWorld == dw.getWorld())
                    dw.updateTimeLastLogin();
            }
            return;
        }

        //Lobby
        if (savedPlayers != null) {
            DataWorld dataWorld = worldsManager.getDataWorldFromWorld(from);
            if (dataWorld != null && dataWorld.getPlayersAllowed().contains(player.getName()))
                SavePlayerUtils.savePlayerData(player, from, savedPlayers);

            PlayerJoin.giveLobbyInventory(player);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setExp(0);
            player.setLevel(0);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
            player.setFoodLevel(20);
            if (player.hasPermission(Permission.PERMISSION))
                instance.getAdminManager().getAdminModeList().remove(player);

        }
    }
}
