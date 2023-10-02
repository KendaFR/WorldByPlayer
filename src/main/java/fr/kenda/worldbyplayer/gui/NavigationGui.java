package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class NavigationGui extends Gui {

    private final String shortcutConfig = "gui.navigation.";
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final WorldsManager worldsManager = instance.getWorldManager();
    private final FileConfiguration config = instance.getConfig();

    /**
     * Create Navigation Menu
     *
     * @param owner Owner of inventory
     * @param title Title of inventory
     * @param row   number of row
     */
    public NavigationGui(Player owner, String title, int row) {
        super(title, owner, row);
    }

    /**
     * Main Menu
     *
     * @return content of inventory
     */
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];
        for (int i = 0; i < 9; i++)
            content[i] = new ItemBuilder(Config.getMaterial(shortcutConfig + "color_glass"))
                    .setName(Config.getString(shortcutConfig + "text_glass")).toItemStack();

        int slotFreeWorld = Config.getInt(shortcutConfig + "free.slot");
        if (worldsManager == null)
            return content;


        //region FreeWorld
        World freeWorld = worldsManager.getFreeWorld();
        if (freeWorld == null)
            return content;

        final int online = freeWorld.getPlayers().size();
        final int max = Config.getInt("worlds.max-players");

        String name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(shortcutConfig + "free.name")));

        String s = max == -1 ? "∞" : String.valueOf(max);
        content[slotFreeWorld] = new ItemBuilder(Config.getMaterial(shortcutConfig + "free.material"))
                .setName(name).setLore(Config.getList(shortcutConfig + "free.lores",
                        "{online}", String.valueOf(online),
                        "{maxplayers}", s))
                .toItemStack();
        //endregion

        //region owner
        final int slotOwnWorld = Config.getInt(shortcutConfig + "ownworld.slot");

        Player player = owner.getPlayer();
        if (worldsManager.playerHasWorld(player)) {

            DataWorld worldPlayer = worldsManager.getDataWorldFromPlayerWorldOwner(player);

            String key = shortcutConfig + "ownworld.exist.";
            String nameWorld = "§f" + ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(key + "name")).replace("{name_world}", worldPlayer.getName()));

            content[slotOwnWorld] = new ItemBuilder(Config.getMaterial(key + "material"))
                    .setName(nameWorld)
                    .toItemStack();
        } else {
            String key = shortcutConfig + "ownworld.create.";
            content[slotOwnWorld] = new ItemBuilder(Config.getMaterial(key + "material")).setName(Config.getString(key + "name"))
                    .setLore(Config.getList(key + "lores")).toItemStack();
        }

        //endregion

        int slotAccessWorld = Config.getInt(shortcutConfig + "access.slot");
        String accessName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(shortcutConfig + "access.name")));
        content[slotAccessWorld] = new ItemBuilder(Config.getMaterial(shortcutConfig + "access.material")).setName(accessName)
                .setLore(Config.getList("access.lores")).toItemStack();

        return content;
    }

    /**
     * Manage click events in inventory
     *
     * @param e InventoryClickEvent
     */
    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory || e.getInventory() == owner.getInventory()) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        final int freeSlot = Config.getInt(shortcutConfig + "free.slot");
        final int ownSlot = Config.getInt(shortcutConfig + "ownworld.slot");
        final int accessSlot = Config.getInt(shortcutConfig + "access.slot");

        if (clickedSlot == freeSlot) {
            World free = Bukkit.getWorld(Config.getString("worlds.nameMap"));
            if (free == null) return;
            final FileConfiguration savedPlayers = instance.getFileManager().getConfigFrom("saved_players");
            SavePlayerUtils.loadDimension(player, free, savedPlayers);
            if (instance.getCreationManager().isInCreation(player))
                instance.getCreationManager().remove(player);
        }
        if (clickedSlot == ownSlot) {
            if (!worldsManager.playerHasWorld(player)) {
                instance.getCreationManager().setup(owner);
            } else {
                DataWorld dataWorld = instance.getWorldManager().getDataWorldFromPlayerWorldOwner(owner);
                final FileConfiguration savedPlayers = instance.getFileManager().getConfigFrom("saved_players");
                SavePlayerUtils.loadLocationInDimension(player, dataWorld.getWorld(), savedPlayers);
            }
            close();
            return;
        }
        if (clickedSlot == accessSlot) {
            final FileConfiguration worldsConfig = instance.getFileManager().getConfigFrom("worlds");
            int size = worldsConfig.getConfigurationSection("worlds") != null ?
                    Objects.requireNonNull(worldsConfig.getConfigurationSection("worlds")).getKeys(false).size() : 0;

            AccessGui accessGui = new AccessGui(Config.getString("gui.access.title"), player, 6);
            int line = size / 9;
            if (size == 0)
                line = 1; // Si size est égal à 0, on ajoute une ligne
            else if (size % 9 != 0)
                line += 1; // Ajouter une ligne si la division a un reste
            else
                line += 2; // Si size est un multiple de 9, on ajoute deux lignes

            accessGui.setSize(line);
            accessGui.create();
            if (instance.getCreationManager().isInCreation(player))
                instance.getCreationManager().remove(player);
        }
    }
}
