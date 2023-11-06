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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;


public class WorldChange implements Listener {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final FileManager fileManager = instance.getFileManager();
    private final WorldsManager worldsManager = instance.getWorldManager();

    /**
     * Manages player information when changing worlds (hub world different from player world)
     *
     * @param e PlayerChangedWorldEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        final Player player = e.getPlayer();
        final World from = e.getFrom();
        final World currentWorld = player.getWorld();
        final World lobby = Bukkit.getWorlds().get(0);
        final String prefix = instance.getPrefix();
        final String nameWorld = currentWorld.getName().contains("_") ? currentWorld.getName().split("_")[0] : currentWorld.getName();
        final String fromWorld = from.getName().contains("_") ? from.getName().split("_")[0] : from.getName();

        final FileConfiguration savedPlayers = fileManager.getConfigFrom("saved_players");

        if (nameWorld.equalsIgnoreCase(fromWorld)) {
            String[] nameParts = currentWorld.getName().split("_");
            int dim = nameParts.length == 1 ? 0 : (nameParts.length > 1 && nameParts[1].equalsIgnoreCase("nether")) ? 1 : 2;

            SavePlayerUtils.loadLocationInDimension(player, currentWorld, dim, savedPlayers);
            return;
        }

        //world player
        if (!nameWorld.equalsIgnoreCase(lobby.getName())) {
            if (player.hasPermission(Permission.PERMISSION) && instance.getAdminManager().isInModeAdmin(player)) {
                player.getInventory().clear();
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(prefix + Messages.getMessage("teleported_admin"));
            } else {
                player.getInventory().clear();
                String worldFree = Config.getString("worlds.nameMap");
                String named = currentWorld.getName().contains("_") ? currentWorld.getName().split("_")[0] : currentWorld.getName();
                DataWorld dataWorld = instance.getWorldManager().getDataWorldFromWorldName(named);
                player.sendMessage(prefix + Messages.getMessage("teleported_in", "{world}",
                        dataWorld == null ? worldFree : dataWorld.getName()));

                if (savedPlayers != null) {
                    SavePlayerUtils.loadLocation(player, currentWorld, savedPlayers);
                    SavePlayerUtils.loadPlayerData(player, currentWorld, savedPlayers);
                }

                if (!nameWorld.equalsIgnoreCase(player.getName())) return;
                DataWorld dw = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                //its own world
                if (dw != null && currentWorld == dw.getWorld())
                    dw.updateTimeLastLogin();
            }
            return;
        }

        //Lobby
        if (savedPlayers != null) {
            DataWorld dataWorld = worldsManager.getDataWorldFromWorld(from);
            if (dataWorld != null && dataWorld.getPlayersAllowed().contains(player.getName())) {
                SavePlayerUtils.savePlayerData(player, from, savedPlayers);
            }

            player.getInventory().clear();
            SavePlayerUtils.LoadInventoryLobby(player);
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
