package fr.kenda.worldbyplayer;

import fr.kenda.worldbyplayer.managers.*;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldByPlayer extends JavaPlugin {
    private static WorldByPlayer instance;
    private String prefix = "";
    private FileManager fileManager;
    private WorldsManager worldManager;
    private GuiManager guiManager;
    private CreationManager creationManager;

    public static WorldByPlayer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        saveDefaultConfig();
        prefix = Messages.getPrefix();

        Bukkit.getConsoleSender().sendMessage("§a##################################### \n" +
                "§a\t\t#         Made By Kenda            #\n" +
                "§a\t\t#     Loading Plugin " + prefix + "...   #\n" +
                "§a\t\t#####################################");

        fileManager = new FileManager();
        fileManager.register();

        worldManager = new WorldsManager();

        guiManager = new GuiManager();
        guiManager.register();

        new CommandManager().register();

        creationManager = new CreationManager();

        new EventsManager().register();

        worldManager.register();
        Bukkit.getWorlds().forEach(World::save);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§c##################################### \n" +
                "§c\t\t#         Made By Kenda            #\n" +
                "§c\t\t#     Thanks for buying Plugin :D ...#\n" +
                "§c\t\t#####################################");

    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Get FileManager to manage file
     *
     * @return FileManager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Get the WorldsManager to manage world
     *
     * @return WorldsManager
     */
    public WorldsManager getWorldManager() {
        return worldManager;
    }

    /**
     * Get the Gui Manager
     *
     * @return GuiManager
     */
    public GuiManager getGuiManager() {
        return guiManager;
    }

    public CreationManager getCreationManager() {
        return creationManager;
    }
}
