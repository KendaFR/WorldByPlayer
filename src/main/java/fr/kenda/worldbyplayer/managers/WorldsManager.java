package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.database.table.Worlds;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ETable;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

public class WorldsManager implements IManager {

    private final String prefix = WorldByPlayer.getInstance().getPrefix();

    private final HashMap<String, World> worlds = new HashMap<>();

    /**
     * Delete folder of world if exist
     *
     * @param worldFolder File
     * @return Boolean
     */
    private static boolean deleteWorld(File worldFolder) {
        if (!worldFolder.exists()) {
            return true; // The world file does not exist, therefore considered deleted
        }

        File[] files = worldFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file); // Delete recursive subfolders
                } else {
                    file.delete(); // Delete files
                }
            }
        }
        return worldFolder.delete(); // return if delete folder and file
    }

    @Override
    public void register() {
        String nameFreeMap = Config.getName("worlds.free");
        nameFreeMap = nameFreeMap.replaceAll("&.", "");

        createWorld(nameFreeMap);
    }

    /**
     * Return a world by name
     *
     * @param name String
     * @return World
     */
    public World getWorld(String name) {
        return worlds.get(name);
    }

    /**
     * Create world (console only)
     *
     * @param name String
     */
    public void createWorld(final String name) {
        WorldCreator worldCreator = new WorldCreator(name);

        Bukkit.getConsoleSender().sendMessage(prefix, Messages.getMessage("attempt_create", "{world}", name));
        if (Bukkit.getWorld(name) == null) {
            worldCreator.createWorld();
            worlds.put(name, Bukkit.getWorld(name));
            Worlds worlds = (Worlds) WorldByPlayer.getInstance().getTableManager().getTableByName(ETable.WORLDS.getName());
            worlds.insertWorld(null, name, true);
            Bukkit.getConsoleSender().sendMessage(prefix, Messages.getMessage("world_created", "{world}", name));
        }
        else
        {
            worlds.put(name, Bukkit.getWorld(name));
            Bukkit.getConsoleSender().sendMessage(prefix, Messages.getMessage("world_loaded", "{world}", name));
        }
    }

    /**
     * Player create world
     *
     * @param player Player
     * @param name   String
     */
    public void playerCreateWorld(final Player player, final String name) {
        WorldCreator worldCreator = new WorldCreator(name);

        player.sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", name));
        if (Bukkit.getWorld(name) == null) {
            worldCreator.createWorld();
            player.sendMessage(prefix, Messages.getMessage("world_created", "{world}", name));
        } else
            player.sendMessage(prefix, Messages.getMessage("creation_failure"));
    }

    /**
     * Delete world by name (console only)
     *
     * @param name String
     */
    public void deleteWorld(final String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            Bukkit.unloadWorld(world, false); // Unload world without save

            File worldFolder = world.getWorldFolder(); //Get world folder in server
            boolean deleted = deleteWorld(worldFolder);
            if (deleted)
                Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("deleted_world", "{world}", name));
            else
                Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("delete_failure"));
        } else
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("world_not_exist"));
    }

    /**
     * Delete world by name for player
     *
     * @param name String
     */
    public void playerDeleteWorld(final Player player, final String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            Bukkit.unloadWorld(world, false); // Unload world without save

            File worldFolder = world.getWorldFolder(); //Get world folder in server
            boolean deleted = deleteWorld(worldFolder);
            if (deleted)
                player.sendMessage(prefix + Messages.getMessage("deleted_world", "{world}", name));
            else
                player.sendMessage(prefix + Messages.getMessage("delete_failure"));

        } else
            player.sendMessage(prefix + Messages.getMessage("world_not_exist"));
    }
}
