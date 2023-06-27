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

    private final WorldsManager worldsManager  = instance.getWorldManager();
    private final GuiManager guiManager  = instance.getGuiManager();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(prefix + "§cYou cannot make this command here.");
            return false;
        }
        World world = player.getWorld();
        if(world != Bukkit.getWorld(player.getName())){
            player.sendMessage(prefix + Messages.getMessage("not_in_own_world"));
            return false;
        }
        DataWorld dataWorld = worldsManager.getDataWorldFromPlayerWorldOwner(player);
       WorldGui worldGui = (WorldGui) guiManager.getGui("world");
       worldGui.setTitle(Config.getString("title_world", "{world}", dataWorld.getName()));
       worldGui.create(player);
        return false;
    }
}
