package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SavePlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HubCmd implements CommandExecutor {
    private final String prefix = WorldByPlayer.getInstance().getPrefix();

    /**
     * Command /hub
     * Teleport player to hub, save data and restore heal and hunger
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return Boolean
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§cYou cannot make this command here.");
            return false;
        }
        World main = Bukkit.getWorlds().get(0);
        if (player.getWorld() == main) {
            player.sendMessage(prefix + Messages.getMessage("already_in_lobby"));
            return false;
        }
        final FileConfiguration savedPlayers = WorldByPlayer.getInstance().getFileManager().getConfigFrom("saved_players");
        SavePlayerUtils.saveLocation(player, player.getWorld(), savedPlayers);

        player.teleport(Config.getLocationLobby());
        player.sendMessage(prefix + Messages.getMessage("back_to_lobby"));

        return false;
    }
}
