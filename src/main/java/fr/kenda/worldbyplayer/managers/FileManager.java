package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.files.ProfileFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

public class FileManager implements IManager {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final HashMap<String, FileConfiguration> files = new HashMap<>();
    private final HashMap<String, ProfileFile> profiles = new HashMap<>();

    @Override
    public void register() {
        createFile("messages");
        createFile("worlds");
        loadProfiles();
    }

    private void loadProfiles() {
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
    }
}
