package fr.kenda.worldbyplayer.commands;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.gui.WorldGui;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldCmd implements CommandExecutor {
    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String prefix = instance.getPrefix();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage(prefix + "§cYou cannot execute this command here");
            return false;
        }
        if(player.getWorld() == Bukkit.getWorlds().get(0)){
            player.sendMessage(prefix + Messages.getMessage("not_in_own_world"));
            return false;
        }
        String nameWorld = player.getWorld().getName().split("_")[0];
        DataWorld dataWorld = instance.getWorldManager().getDataWorldFromWorldName(nameWorld);
        if(dataWorld == null){
            player.sendMessage(prefix + Messages.getMessage("no_world"));
            return false;
        }
        WorldGui worldGui = new WorldGui(Config.getString("gui.worldlist.title"), player, 1);
        worldGui.create();
        return false;
    }
}
