package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.schedulers.AutoPurgeScheduler;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WorldsManager implements IManager {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final FileConfiguration config = instance.getConfig();

    private final String prefix = instance.getPrefix();

    private final ArrayList<DataWorld> worldsList = new ArrayList<>();

    /**
     * Save configuration in file
     *
     * @param configuration FileConfiguration
     * @param fileName      String
     */
    private static void save(final FileConfiguration configuration, final String fileName) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), fileName + ".yml"); // Chemin absolu du fichier
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all dataWorld
     *
     * @return ArrayList<DataWorld>
     */
    public ArrayList<DataWorld> getWorldsList() {
        return worldsList;
    }

    /**
     * Create free world and load all worlds register in files
     */
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


        for (String key : Objects.requireNonNull(worldConfig.getConfigurationSection("worlds")).getKeys(false)) {

            String keyWorld = key.replaceAll("[^a-zA-Z0-9]", "");
            int seed = worldConfig.getInt("worlds." + key + ".seed");
            World world = createWorld(keyWorld, World.Environment.NORMAL, seed);
            World nether = createWorld(keyWorld, World.Environment.NETHER, seed);
            World end = createWorld(keyWorld, World.Environment.THE_END, seed);

            String name = worldConfig.getString("worlds." + key + ".name");
            List<String> playersAllowed = worldConfig.getStringList("worlds." + key + ".playersAllowed");
            DataWorld dataWorld = new DataWorld(world, nether, end, key, name, seed, playersAllowed);
            worldsList.add(dataWorld);
        }
    }
    private World createWorld(String name, World.Environment environment, int seed) {
        String worldName = switch (environment) {
            case THE_END -> name + "_end";
            case NETHER -> name + "_nether";
            default -> name;
        };

        return new WorldCreator(worldName)
                .environment(environment)
                .seed(seed)
                .createWorld();
    }

    /**
     * Create free world
     */
    private void createFreeWorld() {
        String freeWorldName = config.getString("worlds.nameMap");
        if (freeWorldName == null) {
            Bukkit.getConsoleSender().sendMessage(prefix + "§cAn error occurred when creating the free world. Please regenerate the configuration and try again.");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", freeWorldName));
        if (getFreeWorld() == null) {
            int seed = new Random().nextInt();
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("creating_world", "{world}", freeWorldName));
             createWorld(freeWorldName, World.Environment.NORMAL, seed);
             createWorld(freeWorldName, World.Environment.NETHER, seed);
             createWorld(freeWorldName, World.Environment.THE_END, seed);
            Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("world_created", "{world}", freeWorldName));
        } else {
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
     * Get the Data world from a world
     *
     * @param world World to check
     * @return DataWorld found or null
     */
    public DataWorld getDataWorldFromWorld(World world) {
        return worldsList.stream()
                .filter(dataWorld -> dataWorld.getWorld() == world)
                .findFirst()
                .orElse(null);
    }
    /**
     * Get the Data world from a world name
     *
     * @param worldName  world name to check
     * @return DataWorld found or null
     */
    public DataWorld getDataWorldFromWorldName(String worldName) {
        return worldsList.stream()
                .filter(dataWorld -> dataWorld.getWorld().getName().equalsIgnoreCase(worldName))
                .findFirst()
                .orElse(null);
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
        String playerName = player.getName().replaceAll("[^a-zA-Z0-9]", "");
        player.sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", settings.getName()));

        if (Bukkit.getWorld(playerName) == null) {
            Bukkit.getOnlinePlayers().forEach(p ->  p.sendMessage(prefix + Messages.getMessage("world_creation_prevention", "{player}", player.getName())));
            int seed = settings.getSeed();
            World normal = createWorld(playerName, World.Environment.NORMAL, seed);
            World nether = createWorld(playerName, World.Environment.NETHER, seed);
            World end = createWorld(playerName, World.Environment.THE_END, seed);

            DataWorld dataWorld = new DataWorld(normal, nether, end, player.getName(), settings.getName(), settings.getSeed(), null);
            worldsList.add(dataWorld);
            player.sendMessage(prefix + Messages.getMessage("world_created", "{world}", settings.getName()));
        } else {
            player.sendMessage(Messages.getMessage("creation_failure"));
        }
    }

    /**
     * Delete the world in config file
     *
     * @param world World to delete
     */
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

    /**
     * Start the system of auto purge, every day (24 hours before plugin started)
     */
    public void startAutoPurge() {
        autoPurge(null);
        Bukkit.getScheduler().runTaskTimer(instance, new AutoPurgeScheduler(), 20, 20);
    }

    /**
     * Starts a purge of worlds not played for X days (configurable)
     *
     * @param playerStarted A player name if it's a forced purge, or empty if it's console
     */
    public void autoPurge(String playerStarted) {
        try {
            FileConfiguration worldConfig = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
            worldConfig.load(new File(instance.getDataFolder(), "worlds.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage(prefix + (playerStarted == null ? Messages.getMessage("auto_purge_started") : Messages.getMessage("auto_purge_forced_by", "{player}", playerStarted)));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.getName().equalsIgnoreCase(playerStarted) && p.hasPermission(Permission.PERMISSION)) {
                p.sendMessage(prefix + (playerStarted == null ? Messages.getMessage("auto_purge_started") : Messages.getMessage("auto_purge_forced_by", "{player}", playerStarted)));
            }
        });
        int numberSuppressed = 0;
        for (int i = 0; i < worldsList.size(); i++) {
            DataWorld dataWorld = worldsList.get(i);
            if (dataWorld == null) continue;
            if (dataWorld.getTimeEndToMillis() < System.currentTimeMillis()) {
                World world = dataWorld.getWorld();
                Bukkit.getConsoleSender().sendMessage(prefix + Messages.getMessage("purge_suppress", "{owner}", dataWorld.getOwner()));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.hasPermission(Permission.PERMISSION)) {
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
            if (p.hasPermission(Permission.PERMISSION)) {
                p.sendMessage(prefix + Messages.getMessage("auto_purge_end", "{number}", String.valueOf(totalSuppress)));
            }
        });
    }
}
