package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.gui.NavigationGui;
import fr.kenda.worldbyplayer.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class OpenInventory implements Listener {

    /**
     * Opens navigation menu based on click and item
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (player.getWorld() != Bukkit.getWorlds().get(0))
            return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (item.getType() != Config.getMaterial("navigation.item"))
            return;
        if (item.getItemMeta() == null || !item.hasItemMeta()) return;

        String name = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "§cNaN";
        if (!name.equalsIgnoreCase(Config.getString("navigation.name"))) return;

        NavigationGui navigation = new NavigationGui(player, Config.getString("gui.navigation.title"), 1);
        navigation.create();
    }
}
