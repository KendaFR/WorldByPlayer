package fr.kenda.worldbyplayer.utils;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.GameMode;
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
    public static void savePlayerData(final Player player, final World world, final FileConfiguration configuration) {
        // Save inventory
        saveInventory(player, world, configuration);
        // Save armor
        saveArmor(player, world, configuration);

        saveEffects(player, world, configuration);

        saveGamemode(player, world, configuration);

        saveExperience(player, world, configuration);

        save(configuration);
    }

    public static void loadPlayerData(final Player player, final World world, final FileConfiguration configuration) {

        String shortcut = player.getName() + ".worlds." + world.getName() + ".";
        // Load inventory
        loadInventory(player.getInventory(), configuration.getString(shortcut + "inventory"));

        // Load armor
        loadArmor(player, configuration.getString(shortcut + "armor"));

        loadEffects(player, world, configuration);

        // Load gamemode
        String gamemode = configuration.getString(shortcut + "gamemode");
        loadGamemode(player, GameMode.valueOf(gamemode));

        loadExperience(player, world, configuration);
    }

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

    private static void save(final FileConfiguration configuration) {
        try {
            File file = new File(WorldByPlayer.getInstance().getDataFolder(), "saved_players.yml"); // Chemin absolu du fichier
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveGamemode(final Player player, final World from, final FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + from.getName() + ".gamemode", player.getGameMode().toString());
        save(configuration);
    }

    private static void loadGamemode(final Player player, final GameMode gamemode) {
        player.setGameMode(gamemode == null ? GameMode.SURVIVAL : gamemode);
    }

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

    private static void loadEffects(final Player player, final World world, final FileConfiguration configuration) {
        List<Map<?, ?>> serializedEffects = configuration.getMapList(player.getName() + ".worlds." + world.getName() + ".effects");

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

    private static void saveExperience(final Player player, final World world, final FileConfiguration configuration) {
        configuration.set(player.getName() + ".worlds." + world.getName() + ".experience", player.getExp());
        configuration.set(player.getName() + ".worlds." + world.getName() + ".level", player.getLevel());
        save(configuration);
    }

    private static void loadExperience(final Player player, final World world, final FileConfiguration configuration) {
        float experience = (float) configuration.getDouble(player.getName() + ".worlds." + world.getName() + ".experience", 0.0);
        int level = configuration.getInt(player.getName() + ".worlds." + world.getName() + ".level", 0);
        player.setExp(experience);
        player.setLevel(level);
    }
}
