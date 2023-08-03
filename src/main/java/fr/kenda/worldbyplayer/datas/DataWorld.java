package fr.kenda.worldbyplayer.datas;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.LocationTransform;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataWorld {
    private final World world;
    private final String owner;
    private final String nameWorld;
    private final int seed;
    private final List<String> playersAllowed = new ArrayList<>();
    private String name;

    public DataWorld(World world, String owner, String name, int seed) {
        this.world = world;
        this.owner = owner;
        this.name = name;
        this.seed = seed;
        this.nameWorld = world.getName();

        if (!exist(owner))
            save();
    }

    private boolean exist(String owner) {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        return configWorld.get("worlds." + this.owner) != null;
    }

    public void save() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        configWorld.set(key + "name", name);
        configWorld.set(key + "seed", seed);
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);
    }

    public void addPlayerToWorld(String player) {
        if (owner == null) return;
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        playersAllowed.add(player);
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);
    }

    public void removePlayerFromWorld(String target) {
        if (owner == null) return;
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        playersAllowed.remove(target);
        configWorld.set(key + "playersAllowed", playersAllowed);
        saveConfig(configWorld);

        kickPlayerFromWorld(target);
    }

    public void kickPlayerFromWorld(String target) {
        for (Player p : world.getPlayers()) {
            if (p.getName().equalsIgnoreCase(target)) {
                String worldName = Config.getString("lobby.world");
                Location location = LocationTransform.deserializeCoordinate(worldName, Config.getString("lobby.coordinates"));
                location.setY(location.getWorld().getHighestBlockYAt((int) location.getX(), (int) location.getZ()) + 1.5);
                p.teleport(location);
                p.sendMessage(WorldByPlayer.getInstance().getPrefix() + Messages.getMessage("removed_from_world", "{world}", getName(), "{owner}", getOwner()));
            }
        }
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

    public List<String> getPlayersAllowed() {
        return playersAllowed;
    }

    public String getNameWorld() {
        return nameWorld;
    }


}
