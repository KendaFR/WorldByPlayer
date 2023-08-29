package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.gui.WorldGui;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldCmd implements CommandExecutor {
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String prefix = instance.getPrefix();

    private final WorldsManager worldsManager = instance.getWorldManager();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§cYou cannot make this command here.");
            return true;
        }
        switch (args.length) {
            case 0 -> {
                DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                if (dataWorld == null) {
                    player.sendMessage(prefix + Messages.getMessage("no_world"));
                    break;
                }
                World world = player.getWorld();
                if (world != Bukkit.getWorld(player.getName())) {
                    player.sendMessage(prefix + Messages.getMessage("not_in_own_world"));
                    break;
                }
                WorldGui worldGui = new WorldGui(6 * 9);
                worldGui.setTitle(Config.getString("gui.world.title_inventory", "{world}", dataWorld.getName()));
                worldGui.create(player);
            }
            case 1 -> {
                String sub = args[0];
                if (sub.equalsIgnoreCase("info")) {
                    DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                    List<String> list = Config.getList("world_info");
                    List<String> replacedList = new ArrayList<>();

                    for (String s : list) {
                        String stringBuilder = s.replace("{name}", ChatColor.translateAlternateColorCodes('&', dataWorld.getName()))
                                .replace("{owner}", ChatColor.translateAlternateColorCodes('&', dataWorld.getOwner()))
                                .replace("{seed}", ChatColor.translateAlternateColorCodes('&', String.valueOf(dataWorld.getSeed())))
                                .replace("{players_allowed}", ChatColor.translateAlternateColorCodes('&', dataWorld.getAllowedPlayerString()));
                        replacedList.add(stringBuilder);
                    }
                    player.sendMessage("§f======================");
                    replacedList.forEach(player::sendMessage);
                    player.sendMessage("§f======================");
                }
            }
            case 2 -> {
                DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                if (dataWorld == null) {
                    player.sendMessage(prefix + Messages.getMessage("no_world"));
                    return true;
                }
                String target = args[1];
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("invite")) {
                    if (target.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(prefix + Messages.getMessage("not_invite_yourself"));
                        break;
                    }
                    if (!dataWorld.getPlayersAllowed().contains(target)) {
                        dataWorld.addPlayerToWorld(target);
                        player.sendMessage(prefix + Messages.getMessage("player_add_to_world", "{player}", target));
                    } else
                        player.sendMessage(prefix + Messages.getMessage("player_already_invited", "{player}", target));
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    dataWorld.removePlayerFromWorld(target);
                    player.sendMessage(prefix + Messages.getMessage("player_remove_to_world", "{player}", target));
                }
            }
            default -> sendHelp(player);
        }
        return true;

    }

    private void sendHelp(Player player) {
        player.sendMessage("§c============ " + prefix.trim() + " ===========");
        player.sendMessage("§c/world: §7Shows the world customization menu.");
        player.sendMessage("§c/world <add/remove> <player>: §7Add or remove a member to join the world.");
        player.sendMessage("§c/world help: §7Displays plugin help commands.");
        player.sendMessage("§c/world info: §7Show world informations.");
        player.sendMessage("§c============ " + prefix.trim() + " ===========");
    }
}
