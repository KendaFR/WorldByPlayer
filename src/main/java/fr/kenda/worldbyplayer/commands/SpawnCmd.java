package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCmd implements CommandExecutor {
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(prefix  + "Only player can execute this command");
            return false;
        }
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(prefix + Messages.getMessage("send_spawn"));
        return false;
    }
}
