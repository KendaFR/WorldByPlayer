package fr.kenda.worldbyplayer;

import fr.kenda.worldbyplayer.managers.*;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldByPlayer extends JavaPlugin {

    private static WorldByPlayer instance;
    private String prefix = "";

    private FileManager fileManager;
    private WorldsManager worldManager;
    private GuiManager guiManager;
    private DatabaseManager databaseManager;
    private TableManager tableManager;

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

        databaseManager = new DatabaseManager();
        databaseManager.register();

        tableManager = new TableManager();
        tableManager.register();

        worldManager = new WorldsManager();
        worldManager.register();

        guiManager = new GuiManager();
        guiManager.register();

        new EventsManager().register();



    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§c##################################### \n" +
                "§c\t\t#         Made By Kenda            #\n" +
                "§c\t\t#     Thanks for buying Plugin :D ...#\n" +
                "§c\t\t#####################################");

        databaseManager.closePool();
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

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TableManager getTableManager() {
        return tableManager;
    }
}
