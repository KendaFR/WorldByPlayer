package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        Player player = e.getPlayer();
        if (WorldByPlayer.getInstance().getCreationManager().isInCreation(player)) return;


        World world = player.getWorld();
        if (Config.getBoolean("chat.global-chat-active")) {
            e.setCancelled(true);
            DataWorld dataWorld = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromWorld(world);
            String msgChat = Config.getString("chat.global-chat",
                    "{player}", player.getName(),
                    "{message}", msg,
                    "{world}", (dataWorld == null ? "LOBBY" : dataWorld.getName()));

            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', msgChat)));
        } else {
            e.setCancelled(true);
            String msgChat = Config.getString("chat.chat-world",
                    "{player}", player.getName(),
                    "{message}", msg);

            world.getPlayers().forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', msgChat)));
        }
    }

}
