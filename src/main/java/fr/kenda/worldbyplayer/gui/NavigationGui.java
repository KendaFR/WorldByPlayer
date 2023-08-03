package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.managers.FileManager;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NavigationGui extends Gui {

    private final String shortcutConfig = "gui.navigation.";
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final WorldsManager worldsManager = instance.getWorldManager();
    private final FileManager fileManager = instance.getFileManager();
    private final FileConfiguration config = instance.getConfig();
    private final String prefix = instance.getPrefix();

    public NavigationGui(String title, int size) {
        super(title, size);
    }

    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[9];
        for (int i = 0; i < 9; i++)
            content[i] = new ItemBuilder(Config.getMaterial(shortcutConfig + "color_glass"))
                    .setName(Config.getString(shortcutConfig + "text_glass")).toItemStack();

        int slotFreeWorld = Config.getInt(shortcutConfig + "free.slot");
        if (worldsManager == null)
            return content;


        World freeWorld = worldsManager.getFreeWorld();
        if (freeWorld == null)
            return content;

        final int online = freeWorld.getPlayers().size();
        final int max = Config.getInt("worlds.max-players");

        String name = ChatColor.translateAlternateColorCodes('&', config.getString(shortcutConfig + "free.name"));

        String s = max == -1 ? "∞" : String.valueOf(max);
        content[slotFreeWorld] = new ItemBuilder(Config.getMaterial(shortcutConfig + "free.material"))
                .setName(name).setLore(Config.getList(shortcutConfig + "free.lores",
                        "{online}", String.valueOf(online),
                        "{maxplayers}", s))
                .toItemStack();


        final int slotOwnWorld = Config.getInt(shortcutConfig + "ownworld.slot");

        Player player = owner.getPlayer();
        if (worldsManager.playerHasWorld(player)) {

            DataWorld worldPlayer = worldsManager.getDataWorldFromPlayerWorldOwner(player);

            String key = shortcutConfig + "ownworld.exist.";
            String nameWorld = "§f" + ChatColor.translateAlternateColorCodes('&', config.getString(key + "name").replace("{name_world}", worldPlayer.getName()));

            content[slotOwnWorld] = new ItemBuilder(Config.getMaterial(key + "material"))
                    .setName(nameWorld)
                    .toItemStack();
        } else {
            String key = shortcutConfig + "ownworld.create.";
            content[slotOwnWorld] = new ItemBuilder(Config.getMaterial(key + "material")).setName(Config.getString(key + "name"))
                    .setLore(Config.getList(key + "lores")).toItemStack();
        }
        return content;
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory) return;

        e.setCancelled(true);

        int freeSlot = Config.getInt(shortcutConfig + "free.slot");
        int ownSlot = Config.getInt(shortcutConfig + "ownworld.slot");

        /*if (clickedSlot == freeSlot) {

        }*/
        if (clickedSlot == ownSlot) {
            if (!worldsManager.playerHasWorld(player))
                instance.getCreationManager().setup(owner);
            else {
                DataWorld dataWorld = instance.getWorldManager().getDataWorldFromPlayerWorldOwner(owner);
                World world = dataWorld.getWorld();
                /**
                 * TODO replace by file last location
                 */
                player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0));
                player.sendMessage(prefix + Messages.getMessage("teleported_in", "{world}", dataWorld.getName()));
            }
            owner.closeInventory();
        }
    }
}
