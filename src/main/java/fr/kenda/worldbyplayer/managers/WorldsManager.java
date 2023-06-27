package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class WorldsManager implements IManager {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final FileConfiguration config = instance.getConfig();
    private final FileConfiguration messageConfig = instance.getFileManager().getConfigFrom("messages");

    private final String prefix = instance.getPrefix();

    public ArrayList<DataWorld> worldsList = new ArrayList<>();

    @Override
    public void register() {

        createFreeWorld();
        loadAllWorlds();

    }

    private void loadAllWorlds() {
        FileConfiguration worldConfig = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");

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

    public World getFreeWorld() {
        return Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.nameMap"))); // Retourner null si aucun monde correspondant n'est trouvé
    }

    public boolean playerHasWorld(Player player) {
        for (DataWorld dataWorld : worldsList) {
            if (dataWorld.getOwner() == null) continue;
            if (dataWorld.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the dataWorld of player world
     * @param player Player
     * @return DataWorld
     */
    public DataWorld getDataWorldFromPlayerWorldOwner(Player player) {
        for (DataWorld dataWorld : worldsList)
            if (dataWorld.getOwner().equals(player))
                return dataWorld;
        return null;
    }

    public void createWorld(Player player, CreationSettings settings) {
        String playerName = player.getName();
        player.sendMessage(prefix + Messages.getMessage("attempt_create", "{world}", settings.getName()));
        if (Bukkit.getWorld(playerName) == null) {
            WorldCreator creator = new WorldCreator(playerName);
            creator.seed(settings.getSeed());
            World world = creator.createWorld();
            DataWorld dataWorld = new DataWorld(world, player, settings.getName(), settings.getSeed(), settings.getDescription());
            worldsList.add(dataWorld);
            player.sendMessage(prefix + Messages.getMessage("world_created", "{world}", settings.getName()));
            //World createdWorld = Bukkit.getWorld(playerName);
            //player.teleport(new Location(createdWorld, 0, createdWorld.getHighestBlockYAt(0, 0), 0));
        }
        else
            player.sendMessage(Messages.getMessage("creation_failure"));
    }
}
