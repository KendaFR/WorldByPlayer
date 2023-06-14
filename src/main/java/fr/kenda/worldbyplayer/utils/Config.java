package fr.kenda.worldbyplayer.utils;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Config {
    private static final WorldByPlayer INSTANCE = WorldByPlayer.getInstance();
    private static final FileConfiguration CONFIG = INSTANCE.getConfig();

    public static int getInt(String path) {
        return INSTANCE.getConfig().getInt(path);
    }

    /**
     * Return a material from config file
     *
     * @param path String
     * @return Material
     */
    public static Material getMaterial(String path) {
        Material mat = Material.getMaterial(CONFIG.getString(path));
        if (mat != null) return mat;
        return Material.BARRIER;
    }

    /**
     * Return a list of String from config file
     *
     * @param path String
     * @return List<String>
     */
    public static List<String> getList(String path) {
        List<String> lores = CONFIG.getStringList(path);
        List<String> colorLores = new ArrayList<>();
        lores.forEach(s -> colorLores.add(Messages.transformColor(s)));
        return colorLores;
    }

    /**
     * Return list and replace args by value
     * @param path String
     * @param args String...
     * @return List<String>
     */
    public static List<String> getList(String path, String... args) {
        List<String> lores = CONFIG.getStringList(path);
        List<String> colorLores = new ArrayList<>();
        lores.forEach(s -> {
            int size = args.length - 1;
            for (int i = 0; i < size; i++) {
                s = s.replace(args[i], args[i + 1]);
            }
            colorLores.add(Messages.transformColor(s));
        });
        return colorLores;
    }

    /**
     * Return a name from config file
     *
     * @param path String
     * @return String
     */
    public static String getName(String path) {
        String configStr = CONFIG.getString(path);
        if (configStr == null) return "";
        return Messages.transformColor(configStr);
    }
}
