package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.schedulers.AutoPurgeScheduler;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WorldsManager implements IManager {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final FileConfiguration config = instance.getConfig();
    private final FileConfiguration messageConfig = instance.getFileManager().getConfigFrom("messages");

    private final String prefix = instance.getPrefix();

    public ArrayList<DataWorld> worldsList = new ArrayList<>();

    private static void save(final FileConfiguration configuration, final String fileName) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), fileName + ".yml"); // Chemin absolu du fichier
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register() {

        createFreeWorld();
        loadAllWorlds();
    }

    /**
     * Load all world created by all players
     */
    private void loadAllWorlds() {
        FileConfiguration worldConfig = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");

        if (worldConfig.getConfigurationSection("worlds") == null) return;

        for (String key : worldConfig.getConfigurationSection("worlds").getKeys(false)) {
            WorldCreator creator = new WorldCreator(key);
            World world = creator.createWorld();
            String name = worldConfig.getString("worlds." + key + ".name");
            int seed = worldConfig.getInt("worlds." + key + ".seed");
            assert world != null;
            DataWorld dataWorld = new DataWorld(world, key, name, seed);
            worldsList.add(dataWorld);
        }
    }

    private void createFreeWorld() {
        String freeWorldName = config.getString("worlds.nameMap");
        if (freeWorldName == null) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§cAn error occurred when creating the free world. Please regenerate the configuration and try again.");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", freeWorldName));
        if (getFreeWorld() == null) {
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("creating_world", "{world}", freeWorldName));
            WorldCreator creator = new WorldCreator(freeWorldName);
            World world = creator.createWorld();
            //DataWorld dataWorld = new DataWorld(world, null, config.getString("worlds.name"), true);
            //worldsList.add(dataFreeWorld);
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("world_created", "{world}", freeWorldName));
        } else {
            //worldsList.add(new DataFreeWorld(freeWorldName, WorldByPlayer.SECURITYNAME));
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("world_loaded", "{world}", freeWorldName));
        }
    }

    /**
     * Return the FreeWorld
     *
     * @return World
     */
    public World getFreeWorld() {
        return Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.nameMap"))); // Retourner null si aucun monde correspondant n'est trouvé
    }

    /**
     * Check if player has a world
     *
     * @param player Player
     * @return boolean
     */
    public boolean playerHasWorld(Player player) {
        for (DataWorld dataWorld : worldsList) {
            if (dataWorld.getOwner() != null && dataWorld.getOwner().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the dataWorld of player world
     *
     * @param player Player
     * @return DataWorld
     */
    public DataWorld getDataWorldFromPlayerWorldOwner(Player player) {
        for (DataWorld dataWorld : worldsList)
            if (dataWorld.getOwner() != null && dataWorld.getOwner().equals(player.getName()))
                return dataWorld;
        return null;
    }

    /**
     * Return the dataWorld of player world
     *
     * @param playerName playerName
     * @return DataWorld
     */
    public DataWorld getDataWorldFromPlayerWorldOwner(String playerName) {
        for (DataWorld dataWorld : worldsList)
            if (dataWorld.getOwner() != null && dataWorld.getOwner().equals(playerName))
                return dataWorld;
        return null;
    }

    /**
     * Create world for player with settings
     *
     * @param player   Player
     * @param settings CreationSettings
     */
    public void createWorld(Player player, CreationSettings settings) {
        String playerName = player.getName();
        player.sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", settings.getName()));

        if (Bukkit.getWorld(playerName) == null) {
            WorldCreator creator = new WorldCreator(playerName);
            creator.seed(settings.getSeed());
            World world = creator.createWorld();
            assert world != null;
            DataWorld dataWorld = new DataWorld(world, player.getName(), settings.getName(), settings.getSeed());
            worldsList.add(dataWorld);
            player.sendMessage(prefix + Messages.getMessage("world_created", "{world}", settings.getName()));
        } else {
            player.sendMessage(Messages.getMessage("creation_failure"));
        }
    }

    public void deleteWorldConfig(World world) {
        FileConfiguration worldConfig = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        worldConfig.set("worlds." + world.getName(), null);
        save(worldConfig, "worlds");


        FileConfiguration saved_players = WorldByPlayer.getInstance().getFileManager().getConfigFrom("saved_players");
        for (String key : saved_players.getKeys(false)) {
            ConfigurationSection worlds = saved_players.getConfigurationSection(key + ".worlds");
            if (worlds == null) return;
            for (String worldName : worlds.getKeys(false)) {
                if (worldName.equalsIgnoreCase(world.getName()))
                    saved_players.set(key + ".worlds." + worldName, null);
            }
        }
        save(saved_players, "saved_players");
        worldsList.removeIf(dataWorld -> dataWorld.getWorld() == world);

    }

    public void startAutoPurge() {
        autoPurge();
        Bukkit.getScheduler().runTaskTimer(instance, new AutoPurgeScheduler(), 20, 20);
    }

    public void autoPurge() {
        Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("auto_purge_started"));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("admin")) {
                p.sendMessage(prefix + Messages.getMessage("auto_purge_started"));
            }
        });
        int numberSuppressed = 0;
        for (int i = 0; i < worldsList.size(); i++) {
            DataWorld dataWorld = worldsList.get(i);
            if (dataWorld == null) return;
            if (dataWorld.getTimeEndToMillis() < System.currentTimeMillis()) {
                World world = dataWorld.getWorld();
                Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("purge_suppress", "{owner}", dataWorld.getOwner()));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.hasPermission("admin")) {
                        Messages.getMessage("purge_suppress", "{owner}", dataWorld.getOwner());
                    }
                });
                dataWorld.deleteWorld(world);
                deleteWorldConfig(world);
                i--;
                numberSuppressed++;
            }
        }
        final int totalSuppress = numberSuppressed;
        Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("auto_purge_end", "{number}", String.valueOf(totalSuppress)));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("admin")) {
                p.sendMessage(prefix + Messages.getMessage("auto_purge_end", "{number}", String.valueOf(totalSuppress)));
            }
        });
    }
}
