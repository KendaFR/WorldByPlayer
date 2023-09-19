package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WorldGui extends Gui {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();

    public WorldGui(String title, Player player, int row) {
        super(title, player, row);
    }

    @Override
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];
        String shortcut = "gui.worldlist.";
        for (int i = 0; i < size; i++)
            content[i] = new ItemBuilder(Config.getMaterial(shortcut + "background_glass")).toItemStack();

        String nameWorld = owner.getWorld().getName().contains("_") ? owner.getWorld().getName().split("_")[0] : owner.getWorld().getName();
        final DataWorld dataWorld = instance.getWorldManager().getDataWorldFromWorldName(nameWorld);
        if (dataWorld == null) return content;


        Material material3;
        Material material6;
        String name3;
        String name6;

        if (owner.getWorld() == dataWorld.getWorld()) {
            material3 = Config.getMaterial(shortcut + "nether.material");
            material6 = Config.getMaterial(shortcut + "end.material");
            name3 = Config.getString(shortcut + "nether.name");
            name6 = Config.getString(shortcut + "end.name");
        } else if (owner.getWorld() == dataWorld.getNether()) {
            material3 = Config.getMaterial(shortcut + "world.material");
            material6 = Config.getMaterial(shortcut + "end.material");
            name3 = Config.getString(shortcut + "world.name");
            name6 = Config.getString(shortcut + "end.name");
        } else {
            material3 = Config.getMaterial(shortcut + "world.material");
            material6 = Config.getMaterial(shortcut + "nether.material");
            name3 = Config.getString(shortcut + "world.name");
            name6 = Config.getString(shortcut + "nether.name");
        }

        content[3] = new ItemBuilder(material3).setName(name3).toItemStack();
        content[5] = new ItemBuilder(material6).setName(name6).toItemStack();

        return content;
    }

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        final FileConfiguration savedPlayers = instance.getFileManager().getConfigFrom("saved_players");
        String nameWorld = owner.getWorld().getName().contains("_") ? owner.getWorld().getName().split("_")[0] : owner.getWorld().getName();
        if (e.getInventory() != inventory || e.getInventory() == owner.getInventory()) return;


        e.setCancelled(true);

        final DataWorld dataWorld = instance.getWorldManager().getDataWorldFromWorldName(nameWorld);
        if (dataWorld == null) return;
        String dimension = owner.getWorld().getName().contains("_") ? owner.getWorld().getName().split("_")[1] : "world";
        String mainWorld = owner.getWorld().getName().contains("_") ? owner.getWorld().getName().split("_")[0] : owner.getWorld().getName();


        World main = Bukkit.getWorld(mainWorld);
        if(main == null) return;
        switch (clickedSlot) {
            case 3 -> {
                SavePlayerUtils.savePlayerData(owner, main, savedPlayers);
                SavePlayerUtils.saveLocationInDimension(owner, main, dimension, savedPlayers);
                if (owner.getWorld() == dataWorld.getWorld())
                    owner.teleport(dataWorld.getNether().getSpawnLocation());
                else if (owner.getWorld() == dataWorld.getNether())
                    owner.teleport(dataWorld.getWorld().getSpawnLocation());
                else
                    owner.teleport(dataWorld.getWorld().getSpawnLocation());
                SavePlayerUtils.loadLocation(owner, owner.getWorld(), savedPlayers);
            }
            case 5 -> {
                SavePlayerUtils.savePlayerData(owner, main, savedPlayers);
                SavePlayerUtils.saveLocationInDimension(owner, main, dimension, savedPlayers);
                if (owner.getWorld() == dataWorld.getWorld()) owner.teleport(dataWorld.getEnd().getSpawnLocation());
                else if (owner.getWorld() == dataWorld.getNether())
                    owner.teleport(dataWorld.getEnd().getSpawnLocation());
                else owner.teleport(dataWorld.getNether().getSpawnLocation());
            }
        }
    }
}
