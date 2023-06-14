package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.database.table.User;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ETable;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.LocationTransform;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class PlayerJoin implements Listener {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();

    @SuppressWarnings("all")
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        FileConfiguration config = instance.getConfig();
        Player player = e.getPlayer();

        //Create location to teleport player
        String worldName = config.getString("lobby.world");
        Location location = LocationTransform.deserializeCoordinate(worldName, config.getString("lobby.coordinates"));
        player.teleport(location);

        //Create item to give to player
        Material mat = Config.getMaterial("navigation.item");
        String name = Config.getName("navigation.name");
        List<String> lores = Config.getList("navigation.lores");
        ItemBuilder navigation = new ItemBuilder(mat).setName(name);
        if (lores.size() > 0)
            navigation.setLore(lores);

        ItemStack nav = navigation.toItemStack();
        player.getInventory().clear();
        player.getInventory().setItem(4, nav);

        //"Clear" player (remove effect, clear inventory, set life ...)
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);

        User user = (User) WorldByPlayer.getInstance().getTableManager().getTableByName(ETable.USER.getName());
        if (user != null)
            user.insertUser(player);
    }
}
