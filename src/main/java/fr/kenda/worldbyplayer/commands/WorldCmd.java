package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.gui.WorldGui;
import fr.kenda.worldbyplayer.managers.GuiManager;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCmd implements CommandExecutor {
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String prefix = instance.getPrefix();

    private final WorldsManager worldsManager = instance.getWorldManager();
    private final GuiManager guiManager = instance.getGuiManager();


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
                WorldGui worldGui = (WorldGui) guiManager.getGui("world");
                worldGui.setTitle(Config.getString("title_inventory", "{world}", dataWorld.getName()));
                worldGui.create(player);
            }
            case 2 -> {
                DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
                if (dataWorld == null) {
                    player.sendMessage(prefix + Messages.getMessage("no_world"));
                    return true;
                }
                String target = args[1];
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("invite")) {
                    dataWorld.addPlayerToWorld(target);
                    player.sendMessage(prefix + Messages.getMessage("player_add_to_world", "{player}", target));
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
        player.sendMessage("§c============ " + prefix.trim() + " ===========");
    }
}
