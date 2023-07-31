package fr.kenda.worldbyplayer.datas;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataWorld {
    private final World world;
    private final String owner;
    private final String nameWorld;
    private final int seed;
    private final List<String> playersAllowed = new ArrayList<>();
    private String name;
    private List<String> description;

    public DataWorld(World world, String owner, String name, int seed, List<String> description) {
        this.world = world;
        this.owner = owner;
        this.name = name;
        this.seed = seed;
        this.description = description;
        this.nameWorld = world.getName();

        if (!exist(owner))
            saveWorldFolder();
    }

    private boolean exist(String owner) {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        return configWorld.get("worlds." + this.owner) != null;
    }

    private void saveWorldFolder() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        configWorld.set(key + "name", name);
        configWorld.set(key + "description", description);
        configWorld.set(key + "seed", seed);
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);
    }

    private void addPlayerToJoin(Player player) {
        if (owner == null) return;
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        playersAllowed.add(player.getName());
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);

    }

    private void saveConfig(FileConfiguration config) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), "worlds.yml"); // Chemin absolu du fichier
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        return world;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getDescription() {
        List<String> coloredDescription = new ArrayList<>();
        for (String desc : description)
            coloredDescription.add(ChatColor.translateAlternateColorCodes('&', desc));
        return coloredDescription;
    }

    public void setDescription(String description) {
        this.description.clear();
        this.description = Collections.singletonList(description);
    }

    public String getDescriptionString() {
        StringBuilder descriptionString = new StringBuilder();
        for (String str : description)
            descriptionString.append(str);
        return descriptionString.toString();
    }

    public List<String> getPlayersAllowed() {
        return playersAllowed;
    }

    public String getNameWorld() {
        return nameWorld;
    }
}
