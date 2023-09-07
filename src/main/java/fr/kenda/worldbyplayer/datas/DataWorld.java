package fr.kenda.worldbyplayer.datas;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ETimeUnit;
import fr.kenda.worldbyplayer.utils.LocationTransform;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class DataWorld {
    private final World world;
    private final String owner;
    private final int seed;
    private final List<String> playersAllowed;
    private String name;

    public DataWorld(World world, String owner, String name, int seed, List<String> playersAllowed) {
        this.world = world;
        this.owner = owner;
        this.name = name;
        this.seed = seed;
        this.playersAllowed = playersAllowed == null ? new ArrayList<>() : playersAllowed;


        if (!exist())
            save();

    }

    private static void deleteWorldFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            // Gérer le cas où la suppression du fichier a échoué.
                            System.err.println("La suppression du fichier " + file.getAbsolutePath() + " a échoué.");
                        }
                    }
                }
            }
            boolean deletedFolder = folder.delete();
            if (!deletedFolder) {
                // Gérer le cas où la suppression du dossier a échoué.
                System.err.println("La suppression du dossier " + folder.getAbsolutePath() + " a échoué.");
            }
        }
    }


    private boolean exist() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        return configWorld.get("worlds." + this.owner) != null;
    }

    public void save() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        configWorld.set(key + "name", name);
        configWorld.set(key + "seed", seed);
        configWorld.set(key + "playersAllowed", playersAllowed);
        configWorld.set(key + "date_creation", System.currentTimeMillis());
        configWorld.set(key + "timeSuppressWorld", calculateDeletionTime());
        saveConfig(configWorld);
    }

    public String getDateOfCreation() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        String key = "worlds." + owner + ".";
        long millis = configWorld.getLong(key + "date_creation");

        String[] countryShortcut = Config.getString("country-reference").split("-");
        String country = countryShortcut[1].toUpperCase();
        String language = countryShortcut[0].replace("-", "");

        // Créez un objet Date à partir du timestamp en millisecondes
        Date date = new Date(millis);

        // Créez un objet DateFormat avec le Locale approprié
        Locale locale = new Locale(language, country);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

        return dateFormat.format(date);
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
                location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt((int) location.getX(), (int) location.getZ()) + 1.5);
                p.teleport(location);
                p.sendMessage(WorldByPlayer.getInstance().getPrefix() + Messages.getMessage("removed_from_world", "{world}", getName(), "{owner}", getOwner()));
            }
        }
    }

    public void updateTimeLastLogin() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        configWorld.set("worlds." + owner + "." + "timeSuppressWorld", calculateDeletionTime());
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

    /**
     * Get the name of world when player set in creation
     *
     * @return String
     */
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

    public int getSeed() {
        return seed;
    }

    public String getAllowedPlayerString() {
        StringBuilder allowedString = new StringBuilder("[");
        if (playersAllowed == null) return "[]";
        int size = playersAllowed.size();
        for (int i = 0; i < size; i++) {
            allowedString.append(playersAllowed.get(i));
            if (i + 1 < size) allowedString.append(",");
        }
        allowedString.append("]");
        return allowedString.toString();
    }

    public void deleteWorld(World worldToDelete) {
        final String prefix = WorldByPlayer.getInstance().getPrefix();
        // Exclude all players from the world and teleport them to the main world (world 0)
        for (Player player : worldToDelete.getPlayers()) {
            String worldName = Config.getString("lobby.world");
            Location location = LocationTransform.deserializeCoordinate(worldName, Config.getString("lobby.coordinates"));
            location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt((int) location.getX(), (int) location.getZ()) + 1.5);
            player.teleport(location); // Change "world" to the name of your main world
            player.sendMessage(prefix + Messages.getMessage("returns_lobby_delete"));
        }
        Player owner = Bukkit.getPlayer(this.owner);
        if (owner != null) owner.sendMessage(prefix + Messages.getMessage("world_deleted"));

        WorldByPlayer.getInstance().getWorldManager().deleteWorldConfig(world);

        // Unload and remove the world from Bukkit
        Bukkit.unloadWorld(worldToDelete, false);
        Bukkit.getWorlds().remove(worldToDelete);

        // Delete the world folder from the server directory
        File worldFolder = worldToDelete.getWorldFolder();
        deleteWorldFolder(worldFolder);

    }

    private long calculateDeletionTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long timeToAdd = Config.getInt("world-life") * ETimeUnit.DAYS.toMillis();
        return currentTimeMillis + timeToAdd;
    }

    public int getTimeEnd() {
        return ETimeUnit.remainingTimeBetween(getTimeEndToMillis(), System.currentTimeMillis());
    }

    public long getTimeEndToMillis() {
        FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
        return configWorld.getLong("worlds." + owner + "." + "timeSuppressWorld");
    }

    public boolean isInWarning() {
        int warningDays = Config.getInt("days-warning");
        long timeEnd = getTimeEndToMillis();
        return ETimeUnit.remainingTimeBetween(timeEnd, System.currentTimeMillis()) < warningDays;

    }
}
