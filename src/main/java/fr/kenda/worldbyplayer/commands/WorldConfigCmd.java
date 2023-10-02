package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.gui.WorldConfigGui;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WorldConfigCmd implements CommandExecutor {
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String prefix = instance.getPrefix();

    private final WorldsManager worldsManager = instance.getWorldManager();


    /**
     * Command /world
     * Displays the player's world management inventory
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return Boolean
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§cYou cannot make this command here.");
            return true;
        }
        if (instance.getCreationManager().isInCreation(player)) {
            player.sendMessage(prefix + Messages.getMessage("in_creation_world"));
            return false;
        }
        String nameWorld = player.getWorld().getName().contains("_") ? player.getWorld().getName().split("_")[0] : player.getWorld().getName();

        switch (args.length) {
            case 0 -> {
                String playerNameRegex = player.getName().replaceAll("[^a-zA-Z0-9]", "");

                DataWorld dataWorld = worldsManager.getDataWorldFromWorldName(nameWorld);
                if (dataWorld == null) {
                    player.sendMessage(prefix + Messages.getMessage("no_world"));
                    return false;
                }
                if (!dataWorld.getOwnerRegex().contains(playerNameRegex)) {
                    player.sendMessage(prefix + Messages.getMessage("not_in_own_world"));
                    return false;
                }
                WorldConfigGui worldGui = new WorldConfigGui(player, 6);
                worldGui.setTitle(Config.getString("gui.world.title_inventory", "{world}", dataWorld.getName()));
                worldGui.create();
            }
            case 1 -> {
                String sub = args[0];
                if (sub.equalsIgnoreCase("info")) {
                    DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                    if (dataWorld == null) {
                        player.sendMessage(prefix + Messages.getMessage("no_world"));
                        return false;
                    }
                    List<String> list = Config.getList("world_info");
                    List<String> replacedList = new ArrayList<>();

                    for (String s : list) {
                        String stringBuilder = s.replace("{name}", ChatColor.translateAlternateColorCodes('&', dataWorld.getName()))
                                .replace("{owner}", ChatColor.translateAlternateColorCodes('&', dataWorld.getOwner()))
                                .replace("{seed}", ChatColor.translateAlternateColorCodes('&', String.valueOf(dataWorld.getSeed())))
                                .replace("{players_allowed}", ChatColor.translateAlternateColorCodes('&', dataWorld.getAllowedPlayerString()))
                                .replace("{dateCreation}", ChatColor.translateAlternateColorCodes('&', dataWorld.getDateOfCreation()));
                        replacedList.add(stringBuilder);
                    }
                    player.sendMessage("§f======================");
                    replacedList.forEach(player::sendMessage);
                    player.sendMessage("§f======================");
                } else sendHelp(player);
                return false;
            }
            case 2 -> {
                DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                if (dataWorld == null) {
                    player.sendMessage(prefix + Messages.getMessage("no_world"));
                    return false;
                }
                String target = args[1];
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("invite")) {
                    if (target.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(prefix + Messages.getMessage("not_invite_yourself"));
                        return false;
                    }
                    if (!dataWorld.getPlayersAllowed().contains(target)) {
                        dataWorld.addPlayerToWorld(target);
                        player.sendMessage(prefix + Messages.getMessage("player_add_to_world", "{player}", target));
                        Player p = Bukkit.getPlayer(target);
                        if (p != null)
                            p.sendMessage(prefix + Messages.getMessage("invited_by", "{player}", player.getName(), "{name}", dataWorld.getName()));
                        return false;
                    } else
                        player.sendMessage(prefix + Messages.getMessage("player_already_invited", "{player}", target));
                    return false;
                }
                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("kick")) {
                    dataWorld.removePlayerFromWorld(target);
                    player.sendMessage(prefix + Messages.getMessage("player_remove_to_world", "{player}", target));
                    return false;
                }
            }
            default -> sendHelp(player);
        }
        return true;

    }

    /**
     * Send the help message
     *
     * @param player Player who executes the command
     */
    private void sendHelp(@NotNull Player player) {
        player.sendMessage("§c============ " + prefix.trim() + " ===========");
        player.sendMessage("§c/world: §7Shows the world customization menu.");
        player.sendMessage("§c/world <add/invite> <player>: §7Add a member to join the world.");
        player.sendMessage("§c/world <remove/kick> <player>: §7Remove a member to your world.");
        player.sendMessage("§c/world help: §7Displays plugin help commands.");
        player.sendMessage("§c/world info: §7Show world informations.");
        player.sendMessage("§c/world spawn: §7Teleport to world spawn.");
        player.sendMessage("§c============ " + prefix.trim() + " ===========");
    }
}
