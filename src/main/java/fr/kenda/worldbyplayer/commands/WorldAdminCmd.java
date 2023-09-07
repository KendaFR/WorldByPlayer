package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.gui.WorldAdmin;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldAdminCmd implements CommandExecutor {
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String prefix = instance.getPrefix();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§cYou cannot make this command here.");
            return true;
        }
        if (!player.hasPermission(Permission.PERMISSION)) {
            player.sendMessage(prefix + Messages.getMessage("no_permission"));
            return false;
        }
        new WorldAdmin(Config.getString("gui.admin.title"), player, 1).create();
        return false;
    }
}
