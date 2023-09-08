package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.managers.CreationManager;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Random;

public class ChatCreatingWorld implements Listener {

    private final CreationManager creationManager = WorldByPlayer.getInstance().getCreationManager();


    /**
     * Event in chat to avoid sending messages, but retrieves messages when creating the world
     * @param e AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        if (creationManager.isInCreation(player)) {
            e.setCancelled(true);
            CreationSettings settings = creationManager.getSettingsCreationByPlayer(player);
            switch (creationManager.getStatusCreation(player)) {
                case NAME -> settings.setName(message);
                case SEED -> {
                    if (!message.equalsIgnoreCase("NONE")) {
                        int parsed;
                        try {
                            parsed = Integer.parseInt(message);
                            settings.setSeed(parsed);
                        } catch (NumberFormatException ex) {
                            player.sendMessage(Messages.getMessage("value_seed_invalid"));
                            parsed = new Random().nextInt(Integer.MAX_VALUE);
                            settings.setSeed(parsed);
                        }
                    } else {
                        int seed = new Random().nextInt(Integer.MAX_VALUE);
                        settings.setSeed(seed);
                    }
                }
            }
            Bukkit.getScheduler().runTask(WorldByPlayer.getInstance(), () -> creationManager.nextStep(player));
        }
    }
}
