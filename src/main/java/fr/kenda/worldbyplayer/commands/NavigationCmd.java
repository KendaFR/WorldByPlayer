package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.gui.NavigationGui;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NavigationCmd implements CommandExecutor {
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "Only player can execute this command");
            return false;
        }
        if(player.getWorld() == Bukkit.getWorlds().get(0)) {
            NavigationGui navigation = new NavigationGui(player, Config.getString("gui.navigation.title"), 1);
            navigation.create();
        }
        else
            player.sendMessage(prefix + Messages.getMessage("not_in_hub"));
        return false;
    }
}
