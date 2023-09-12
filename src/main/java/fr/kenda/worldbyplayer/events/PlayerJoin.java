package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.LocationTransform;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;


public class PlayerJoin implements Listener {

    /**
     * Gives lobby inventory when player joins
     *
     * @param player Player who joins
     */
    public static void giveLobbyInventory(Player player) {
        Location location = LocationTransform.deserializeCoordinate("", Config.getString("lobby.coordinates"));
        location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt((int) location.getX(), (int) location.getZ()) + 1.5);
        player.teleport(location);

        //Create item to give to player
        Material mat = Config.getMaterial("navigation.item");
        String name = Config.getString("navigation.name");
        List<String> lores = Config.getList("navigation.lores");
        ItemBuilder navigation = new ItemBuilder(mat).setName(name);
        if (lores.size() > 0)
            navigation.setLore(lores);

        ItemStack nav = navigation.toItemStack();
        player.getInventory().clear();
        player.getInventory().setItem(4, nav);
    }

    @SuppressWarnings("all")
    /**
     * Manages the player when he joins the server. (teleports to hub, manages inventory, resets life, etc.)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setExp(0);
        player.setLevel(0);

        giveLobbyInventory(player);

        //"Clear" player (remove effect, clear inventory, set life ...)
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);

        DataWorld dataWorld = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromPlayerWorldOwner(player);
        if (dataWorld == null) return;
        if (dataWorld.isInWarning())
            player.sendMessage(WorldByPlayer.getInstance().getPrefix() + Messages.getMessage("time_left", "{days}", String.valueOf(dataWorld.getTimeEnd())));
    }
}
