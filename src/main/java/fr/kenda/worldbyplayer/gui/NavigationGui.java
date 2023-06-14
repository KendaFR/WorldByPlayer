package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NavigationGui extends Gui {

    private final String shortcutConfig = "gui.navigation.";

    public NavigationGui(String title, int size) {
        super(title, size);
        updateContent(setMainMenu());
    }

    private ItemStack[] setMainMenu() {
        ItemStack[] content = new ItemStack[9];
        for (int i = 0; i < 9; i++)
            content[i] = new ItemBuilder(Config.getMaterial(shortcutConfig + "color_glass"))
                    .setName(Config.getName(shortcutConfig + "text_glass")).toItemStack();

        int slotFreeWorld = Config.getInt(shortcutConfig + "free_world.slot");
        WorldsManager worldsManager =  WorldByPlayer.getInstance().getWorldManager();
        if(worldsManager == null) { System.out.println("worlds Manager is null"); return content; }
        World freeWorld = worldsManager.getWorld(Config.getName("worlds.free"));
        if(freeWorld == null) { System.out.println("World free est null"); return content; }

        content[slotFreeWorld] = new ItemBuilder(Config.getMaterial(shortcutConfig + "free_world.material"))
                .setName(Config.getName(shortcutConfig + "free_world.name"))
                .setLore(Config.getList(shortcutConfig + "free_world.lores",
                        "{players}", String.valueOf(freeWorld.getPlayers().size()),
                        "{maxplayers}", String.valueOf(Config.getInt("worlds.max-players")))).toItemStack();
        return content;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        if (e.getInventory() != inventory) return;
        e.setCancelled(true);
        /*if (clickedSlot == Config.getInt(shortcutConfig + "free_world.slot")) {

        }*/


    }

}
