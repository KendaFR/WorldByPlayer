package fr.kenda.worldbyplayer.files;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataPlayerPerWorld;
import fr.kenda.worldbyplayer.utils.LocationTransform;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ProfileFile {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final File dataFolder = instance.getDataFolder();
    private final Player player;
    private final HashMap<World, DataPlayerPerWorld> datasPerWorld;
    private File subFolder;
    private FileConfiguration configPlayer;

    public ProfileFile(Player player) {
        this.player = player;
        datasPerWorld = new HashMap<>();

    }

    /**
     * Create file in folder to save datas from player
     */
    public void create() {
        if (dataFolder.exists())
            dataFolder.mkdir();

        //Création du sous dossier "players"
        File playersFolder = new File(dataFolder, "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
            subFolder = playersFolder;
        }

        // Création du fichier portant le nom du joueur
        File playerFile = new File(playersFolder, player.getName() + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                configPlayer = YamlConfiguration.loadConfiguration(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get file player
     *
     * @return File
     */
    private File getFile() {
        return new File(subFolder, player.getName() + ".yml");
    }

    /**
     * Get Map DatasPerWorld
     *
     * @return HashMap<World, DataPlayerPerWorld>
     */
    public HashMap<World, DataPlayerPerWorld> getDatasPerWorld() {
        return datasPerWorld;
    }

    /**
     * Get Player datas from a world with world name
     *
     * @param name String
     * @return DataPlayerPerWorld
     */
    public DataPlayerPerWorld getDataByWorldName(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) return null;
        return datasPerWorld.get(world);
    }

    /**
     * Save datas from player and save profile in file
     *
     * @param player Player
     */
    public void saveDatas(Player player) {
        String worldName = player.getWorld().getName();
        DataPlayerPerWorld datasFromWorld = getDataByWorldName(worldName);
        if (datasFromWorld == null)
            datasPerWorld.put(Bukkit.getWorld(worldName), new DataPlayerPerWorld(player));
        else
            datasFromWorld.saveData(player);

        saveProfile(player);
    }

    /**
     * Save profile in each world
     *
     * @param player Player
     */
    public void saveProfile(Player player) {
        datasPerWorld.forEach((world, dataPlayerPerWorld) -> {
            String worldName = world.getName();
            String key = "worlds." + worldName + ".";
            configPlayer.set(key + "life", dataPlayerPerWorld.getLife());
            configPlayer.set(key + "experience", dataPlayerPerWorld.getExperience());
            configPlayer.set(key + "food", dataPlayerPerWorld.getFood());
            configPlayer.set(key + "inventory", dataPlayerPerWorld.getInventory().toString());
            configPlayer.set(key + "location", LocationTransform.serializeCoordinate(dataPlayerPerWorld.getLocation()));
        });
        try {
            configPlayer.save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
