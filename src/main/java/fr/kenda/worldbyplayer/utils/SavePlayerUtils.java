package fr.kenda.worldbyplayer.utils;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SavePlayerUtils {
    /**
     * Save all data of player in config file
     *
     * @param player        Player
     * @param world         World where he played
     * @param configuration Configuration file
     */
    public static void savePlayerData(final Player player, final World world, final FileConfiguration configuration) {
        World mainWorld = world;
        String dimension = "world";
        if (world.getName().contains("_")) {
            mainWorld = Bukkit.getWorld(world.getName().split("_")[0]);
            dimension = world.getName().split("_")[1];
            if (mainWorld == null) {
                return;
            }
        }

        // Save inventory
        saveInventory(player, mainWorld, configuration);
        saveHeal(player, mainWorld, configuration);
        saveFood(player, mainWorld, configuration);
        //saveLocationInDimension(player, mainWorld, dimension, configuration);

        // Save armor
        saveArmor(player, mainWorld, configuration);
        saveEffects(player, mainWorld, configuration);
        saveGamemode(player, mainWorld, configuration);
        saveExperience(player, mainWorld, configuration);
        saveDimension(player, mainWorld, dimension, configuration);
        save(configuration);
    }

    private static void saveFood(Player player, World mainWorld, FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + mainWorld.getName() + ".food", player.getFoodLevel());
        save(configuration);
    }

    private static void saveHeal(Player player, World mainWorld, FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + mainWorld.getName() + ".heal", !player.isDead() ? player.getHealth() : 20);
        save(configuration);
    }

    public static void saveLocationInDimension(Player player, World mainWorld, String dimension, FileConfiguration configuration) {
        String main = mainWorld.getName().contains("_") ? mainWorld.getName().split("_")[0] : mainWorld.getName();
        configuration.set(player.getName() + ".worlds." + main + ".location." + dimension, LocationTransform.serializeCoordinate(player.getLocation()));
        save(configuration);
    }

    public static void saveDimension(Player player, World world, String dimension, FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + world.getName() + ".dimension", dimension);
        save(configuration);
    }

    /**
     * Load all player data from file
     *
     * @param player        Player
     * @param world         World where he played
     * @param configuration config file
     */
    public static void loadPlayerData(final Player player, final World world, final FileConfiguration configuration) {

        String worldName = world.getName().contains("_") ? world.getName().split("_")[0] : world.getName();
        String shortcut = player.getName() + ".worlds." + worldName + ".";

        //loadLocation(player, worldName, world.getName().contains("_") ? world.getName().split("_")[1] : "world", configuration);

        // Load inventory
        loadInventory(player.getInventory(), configuration.getString(shortcut + "inventory"));

        // Load armor
        loadArmor(player, configuration.getString(shortcut + "armor"));

        loadEffects(player, worldName, configuration);

        // Load gamemode
        String gamemode = configuration.getString(shortcut + "gamemode");

        if (gamemode != null)
            loadGamemode(player, GameMode.valueOf(gamemode));
        else
            loadGamemode(player, GameMode.SURVIVAL);

        loadExperience(player, worldName, configuration);
        loadHeal(player, worldName, configuration);
        loadFood(player, worldName, configuration);
    }

    private static void loadHeal(Player player, String worldName, FileConfiguration configuration) {
        double heal = configuration.getDouble(player.getName() + ".worlds." + worldName + ".heal");
        player.setHealth(heal == 0 ? 20 : heal);
    }

    private static void loadFood(Player player, String worldName, FileConfiguration configuration) {
        int food = configuration.getInt(player.getName() + ".worlds." + worldName + ".food");
        player.setFoodLevel(food == 0 ? 20 : food);
    }

    public static void loadDimension(Player player, World world, FileConfiguration configuration) {
        String playerKey = player.getName();
        String worldName = world.getName();
        String dimension = configuration.getString(playerKey + ".worlds." + worldName + ".dimension");

        if (dimension == null) return;

        if (dimension.equalsIgnoreCase("world")) {
            Location teleport = new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
            player.teleport(teleport);
        } else {
            World worldLoad = Bukkit.getWorld(worldName + "_" + dimension);
            if (worldLoad != null) {
                Location teleportLocation = new Location(worldLoad, 0, worldLoad.getHighestBlockYAt(0, 0), 0);
                player.teleport(teleportLocation);
            }
        }
    }


    /**
     * Save armor data
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void saveArmor(final Player player, final World world, final FileConfiguration configuration) {
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        try (final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             final BukkitObjectOutputStream data = new BukkitObjectOutputStream(byteOutput)) {

            data.writeInt(armorContents.length);
            for (ItemStack itemStack : armorContents) {
                data.writeObject(itemStack);
            }

            data.close(); // Close the BukkitObjectOutputStream

            // Convert the serialized data to Base64
            String serializedData = Base64.getEncoder().encodeToString(byteOutput.toByteArray());

            // Store the Base64 encoded data in the configuration
            configuration.set(player.getName() + ".worlds." + world.getName() + ".armor", serializedData);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load armor data
     *
     * @param player        Player
     * @param encodedString an encoded string from config file
     */
    private static void loadArmor(final Player player, final String encodedString) {
        if (encodedString == null) return;
        try (final BukkitObjectInputStream data = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(encodedString)))) {
            final int armorSize = data.readInt();
            ItemStack[] armorContents = new ItemStack[armorSize];
            for (int i = 0; i < armorSize; i++) {
                armorContents[i] = (ItemStack) data.readObject();
            }
            player.getInventory().setArmorContents(armorContents);
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save inventory of player
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void saveInventory(final Player player, final World world, final FileConfiguration configuration) {
        Inventory inventory = player.getInventory();
        try (final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             final BukkitObjectOutputStream data = new BukkitObjectOutputStream(byteOutput)) {

            data.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                data.writeObject(inventory.getItem(i));
            }

            data.close(); // Close the BukkitObjectOutputStream

            // Convert the serialized data to Base64
            String serializedData = Base64.getEncoder().encodeToString(byteOutput.toByteArray());

            // Store the Base64 encoded data in the configuration
            configuration.set(player.getName() + ".worlds." + world.getName() + ".inventory", serializedData);

            save(configuration);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load inventory from config file
     *
     * @param inventory     Inventory of player
     * @param encodedString encoded string in config file
     */
    private static void loadInventory(final Inventory inventory, final String encodedString) {
        if (encodedString == null) return;
        try (final BukkitObjectInputStream data = new BukkitObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(encodedString)))) {
            final int invSize = data.readInt();
            for (int i = 0; i < invSize; i++) {
                inventory.setItem(i, (ItemStack) data.readObject());
            }
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save config in file
     *
     * @param configuration config file
     */
    private static void save(final FileConfiguration configuration) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), "saved_players.yml"); // Chemin absolu du fichier
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save gamemode of player
     *
     * @param player        OPlayer
     * @param from          World
     * @param configuration config file
     */
    private static void saveGamemode(final Player player, final World from, final FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + from.getName() + ".gamemode", player.getGameMode().toString());
        save(configuration);
    }

    /**
     * Load gamemode to player
     *
     * @param player   Player
     * @param gamemode Gamemode
     */
    private static void loadGamemode(final Player player, final GameMode gamemode) {
        player.setGameMode(gamemode);
    }

    /**
     * Save effects of player
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void saveEffects(final Player player, final World world, final FileConfiguration configuration) {
        Collection<PotionEffect> effects = player.getActivePotionEffects();

        List<Map<String, Object>> serializedEffects = new ArrayList<>();
        for (PotionEffect effect : effects) {
            Map<String, Object> serializedEffect = new HashMap<>();
            serializedEffect.put("type", effect.getType().getName());
            serializedEffect.put("duration", effect.getDuration());
            serializedEffect.put("amplifier", effect.getAmplifier());
            serializedEffect.put("ambient", effect.isAmbient());
            serializedEffect.put("particles", effect.hasParticles());
            serializedEffect.put("icon", effect.hasIcon());

            serializedEffects.add(serializedEffect);
        }

        configuration.set(player.getName() + ".worlds." + world.getName() + ".effects", serializedEffects);
        save(configuration);
    }

    /**
     * Load all effects to player
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void loadEffects(final Player player, final String world, final FileConfiguration configuration) {
        List<Map<?, ?>> serializedEffects = configuration.getMapList(player.getName() + ".worlds." + world + ".effects");

        for (Map<?, ?> serializedEffect : serializedEffects) {
            PotionEffectType type = PotionEffectType.getByName((String) serializedEffect.get("type"));
            int duration = (int) serializedEffect.get("duration");
            int amplifier = (int) serializedEffect.get("amplifier");
            boolean ambient = (boolean) serializedEffect.get("ambient");
            boolean particles = (boolean) serializedEffect.get("particles");
            boolean icon = (boolean) serializedEffect.get("icon");

            assert type != null;
            PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
            player.addPotionEffect(effect);
        }
    }

    /**
     * Save experience of player
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void saveExperience(final Player player, final World world, final FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + world.getName() + ".experience", player.getExp());
        configuration.set(player.getName() + ".worlds." + world.getName() + ".level", player.getLevel());
        save(configuration);
    }

    /**
     * Load experience to player
     *
     * @param player        Player
     * @param world         world where he played
     * @param configuration config file
     */
    private static void loadExperience(final Player player, final String world, final FileConfiguration configuration) {
        float experience = (float) configuration.getDouble(player.getName() + ".worlds." + world + ".experience", 0.0);
        int level = configuration.getInt(player.getName() + ".worlds." + world + ".level", 0);
        player.setExp(experience);
        player.setLevel(level);
    }

    public static void loadLocationInDimension(Player player, World world, FileConfiguration savedPlayers) {
        String nameWorld = world.getName().contains("_") ? world.getName().split("_")[0] : world.getName();
        String dimension = savedPlayers.getString(player.getName() + ".worlds." + nameWorld + ".dimension");
        if (dimension == null) { //En logique n'est jamais null
            World defaultWorld = Bukkit.getWorld(nameWorld);
            if (defaultWorld == null) return;
            player.teleport(new Location(defaultWorld, 0, defaultWorld.getHighestBlockYAt(0, 0), 0));
        } else {
            String dimensionLocation = savedPlayers.getString(player.getName() + ".worlds." + nameWorld + ".location." + dimension);
            Location loc;
            String worldDim = dimension.equalsIgnoreCase("world") ? nameWorld : nameWorld + "_" + dimension;
            loc = LocationTransform.deserializeCoordinate(worldDim, Objects.requireNonNullElse(dimensionLocation, "0;0;0;0;0"));
            player.teleport(loc);
        }
    }

    public static void loadLocationInDimension(Player player, World world, int dimension, FileConfiguration savedPlayers) {
        String nameWorld = world.getName().contains("_") ? world.getName().split("_")[0] : world.getName();
        String locationKey = "";

        switch (dimension) {
            case 0 -> locationKey = "world";
            case 1 -> locationKey = "nether";
            case 2 -> locationKey = "end";
        }

        String location = savedPlayers.getString(player.getName() + ".worlds." + nameWorld + ".location." + locationKey);
        if (location == null) {
            player.teleport(world.getSpawnLocation());
            return;
        }

        Location loc = LocationTransform.deserializeCoordinate(nameWorld + (dimension == 0 ? "" : "_" + locationKey), location);
        player.teleport(loc);
    }


    public static void loadLocation(Player player, World currentWorld, FileConfiguration savedPlayers) {
        String nameWorld = currentWorld.getName().contains("_") ? currentWorld.getName().split("_")[0] : currentWorld.getName();
        String dimension = currentWorld.getName().contains("_") ? currentWorld.getName().split("_")[1] : "world";
        if (dimension == null) return;
        String locString = savedPlayers.getString(player.getName() + ".worlds." + nameWorld + ".location." + dimension);
        if (locString == null) {
            player.teleport(currentWorld.getSpawnLocation());
            return;
        }
        Location loc = LocationTransform.deserializeCoordinate(player, locString);
        player.teleport(loc);
    }

    public static void resetPlayer(Player player, World world, FileConfiguration savedPlayers) {
        String nameWorld = world.getName().contains("_") ? world.getName().split("_")[0] : world.getName();
        savedPlayers.set(player.getName() + ".worlds." + nameWorld, null);
        save(savedPlayers);
    }
}