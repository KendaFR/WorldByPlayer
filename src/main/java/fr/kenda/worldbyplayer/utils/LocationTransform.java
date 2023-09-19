package fr.kenda.worldbyplayer.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationTransform {

    /**
     * Serializes a location to create a string
     *
     * @param location Location
     * @return String
     */
    public static String serializeCoordinate(Location location) {
        return String.format("%.1f;%.1f;%.1f;%.0f;%.0f", location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Deserializes a string to create a Location
     *
     * @param world              String
     * @param serializedLocation String
     * @return Location
     */
    public static Location deserializeCoordinate(String world, String serializedLocation) {
        String[] parts = serializedLocation.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace(',', '.'); // Replace ',' with '.'
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);

        return new Location(world.isEmpty() ? Bukkit.getWorlds().get(0) : Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Deserializes a string to create a Location in the player's world
     *
     * @param player             Player
     * @param serializedLocation String
     * @return Location
     */
    public static Location deserializeCoordinate(Player player, String serializedLocation) {
        String[] parts = serializedLocation.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace(',', '.'); // Replace ',' with '.'
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);

        return new Location(player.getWorld(), x, y, z, yaw, pitch);
    }
}
