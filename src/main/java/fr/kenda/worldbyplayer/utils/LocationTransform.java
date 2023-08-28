package fr.kenda.worldbyplayer.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationTransform {

    /**
     * Can serialize a location to create a string
     *
     * @param location Location
     * @return String
     */
    public static String serializeCoordinate(final Location location) {
        double x = Math.round(location.getX() * 10.0) / 10.0;
        double y = Math.round(location.getY() * 10.0) / 10.0;
        double z = Math.round(location.getZ() * 10.0) / 10.0;
        float yaw = Math.round(location.getYaw());
        float pitch = Math.round(location.getPitch());

        return String.format("%.1f; %.1f; %.1f; %.0f; %.0f", x, y, z, yaw, pitch);
    }

    /**
     * Deserialize a string to create a Location
     *
     * @param world              String
     * @param serializedLocation String
     * @return Location
     */
    public static Location deserializeCoordinate(final String world, final String serializedLocation) {
        String[] parts = serializedLocation.split(";");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace(',', '.'); // Remplace ',' par '.'
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        float yaw = Float.parseFloat(parts[3]);
        float pitch = Float.parseFloat(parts[4]);

        return new Location((world.isEmpty() ? Bukkit.getWorld("world") : Bukkit.getWorld(world)), x, y, z, yaw, pitch);
    }

}
