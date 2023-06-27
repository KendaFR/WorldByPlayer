package fr.kenda.worldbyplayer.datas;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataWorld {
    private final World world;
    private final Player owner;
    private final String name;
    private final String nameWorld;
    private final int seed;
    private final List<String> description;
    private final List<String> playersAllowed = new ArrayList<>();

    public DataWorld(World world, Player owner, String name, int seed, List<String> description) {
        this.world = world;
        this.owner = owner;
        this.name = name;
        this.seed = seed;
        this.description = description;
        this.nameWorld = world.getName();

        if (!exist(owner))
            createWorldInFolder();
    }

    private boolean exist(Player owner) {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        return configWorld.get("worlds." + this.owner.getName()) != null;
    }

    private void createWorldInFolder() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner.getName() + ".";
        configWorld.set(key + "name", name);
        configWorld.set(key + "description", description);
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);
    }

    private void addPlayerToJoin(Player player) {
        if (owner == null) return;
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner.getName() + ".";
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

    public World getWorld() {
        return world;
    }

    public Player getOwner() {
        return owner;
    }

    public List<String> getDescription() {
        return description;
    }
    public String getDescriptionString(){
        StringBuilder descriptionString = new StringBuilder();
        for(String str : description)
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
