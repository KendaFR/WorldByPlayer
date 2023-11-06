package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import fr.kenda.worldbyplayer.utils.SkullBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AccessGui extends Gui {

    private final WorldsManager worldsManager = WorldByPlayer.getInstance().getWorldManager();
    private final List<DataWorld> dataWorlds = new ArrayList<>();

    /**
     * Create a gui with title, owner, and number of row
     *
     * @param title  Title of inventory
     * @param player owner of inventory
     * @param row    row need in inventory
     */
    public AccessGui(String title, Player player, int row) {
        super(title, player, row);
    }

    /**
     * Main inventory menu
     *
     * @return ItemStack in inventory
     */
    @Override
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];
        //final ConfigurationSection section = configWorld.getConfigurationSection("worlds");

        String shortcut = "gui.access.";
        content[size - 1] = new ItemBuilder(Config.getMaterial(shortcut + "exit.material")).setName(Config.getString(shortcut + "exit.name")).toItemStack();

        int index = 0;
        /*for (String key : section.getKeys(false)) {
            final String keyName = "worlds." + key + ".";
            if (Objects.requireNonNull(configWorld.getList(keyName + ".playersAllowed")).contains(owner.getName())) {
                String nameOfWorld = Messages.transformColor(configWorld.getString(keyName + "name"));
                List<String> lores = Config.getList(shortcut + "lores", "{owner}", key, "{seed}", configWorld.getString(keyName + "seed"));
                content[index] = new SkullBuilder(key).setName(Config.getString(shortcut + "name", "{world_name}", nameOfWorld)).setLores(lores).toItemStack();
                index++;
            }
        }*/
        for (DataWorld dataWorld : worldsManager.getWorldsList()) {
            if (dataWorld.getPlayersAllowed().contains(owner.getName())) {
                String nameOfWorld = dataWorld.getName();
                String ownerData = dataWorld.getOwner();
                List<String> lores = Config.getList(shortcut + "lores", "{owner}", ownerData, "{seed}", String.valueOf(dataWorld.getSeed()), "{creation}", dataWorld.getDateOfCreation(), "{online}", String.valueOf(dataWorld.getAllPlayers().size()));
                content[index] = new SkullBuilder(ownerData).setName(Config.getString(shortcut + "name", "{world_name}", nameOfWorld)).setLores(lores).toItemStack();
                index++;
                dataWorlds.add(dataWorld);
            }
        }

        if (index == 0)
            content[4] = new ItemBuilder(Config.getMaterial(shortcut + "no_world.material")).setName(Config.getString(shortcut + "no_world.name"))
                    .setLore(Config.getList(shortcut + "no_world.lores")).toItemStack();
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

        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);

        if (clickedSlot == (size - 1)) {
            close();
            return;
        }
        if (dataWorlds.size() > 0) {
            DataWorld worldOfPlayer = dataWorlds.get(clickedSlot);
            if (worldOfPlayer == null) return;
            SavePlayerUtils.SaveInventoryLobby(player);
            final FileConfiguration savedPlayers = WorldByPlayer.getInstance().getFileManager().getConfigFrom("saved_players");
            SavePlayerUtils.loadLocationInDimension(player, worldOfPlayer.getWorld(), savedPlayers);

        }
    }

}
