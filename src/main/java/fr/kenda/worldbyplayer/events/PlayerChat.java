package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.utils.Config;
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

        if (!Config.getBoolean("chat.global-chat")) {
            e.setCancelled(true);
            World world = player.getWorld();
            String msgChat = Config.getString("chat.chat-world", "{player}", player.getName(), "{message}", msg);
            world.getPlayers().forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', msgChat)));
        }
    }
}
