package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class FileManager implements IManager {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final HashMap<String, FileConfiguration> files = new HashMap<>();

    @Override
    public void register() {
        createFile("messages");
        createFile("worlds");
        createFile("saved_players");
    }

    /**
     * Create file
     *
     * @param fileName String
     */
    public void createFile(String fileName) {
        instance.saveResource(fileName + ".yml", false);
        final File file = new File(instance.getDataFolder(), fileName + ".yml");
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        files.put(fileName, configFile);
    }

    /**
     * Get configuration from file
     *
     * @param fileName String
     * @return FileConfiguration
     */
    public FileConfiguration getConfigFrom(String fileName) {
        return files.get(fileName);
    }

/*
    public void saveFile(String fileName) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), fileName + ".yml"); // Chemin absolu du fichier
            getConfigFrom(fileName).save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

/*
    private ProfileFile getProfile(String playerName) {
        return profiles.get(playerName);
    }
    public void createProfile(Player player) {
        if (!existProfile(player))
            profiles.put(player.getName(), new ProfileFile(player));
    }

    public ProfileFile getProfile(Player player) {
        return profiles.get(player.getName());
    }

    public boolean existProfile(Player player) {
        return profiles.get(player.getName()) != null;
    }*/
}
