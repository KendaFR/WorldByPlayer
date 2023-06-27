package fr.kenda.worldbyplayer.events;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.managers.CreationManager;
import fr.kenda.worldbyplayer.utils.ChatUtils;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatCreatingWorld implements Listener {

    private final CreationManager creationManager = WorldByPlayer.getInstance().getCreationManager();


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (creationManager.isInCreation(player)) {
            e.setCancelled(true);
            String message = e.getMessage();
            CreationSettings settings = creationManager.getSettingsCreationByPlayer(player);
            switch (creationManager.getStatusCreation(player)) {
                case NAME:
                    settings.setName(message);
                    break;
                case DESCRIPTION:
                    List<String> formattedMessage = separateString(message);
                    settings.setDescription(formattedMessage);
                    break;
                case SEED:
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
                        break;
                    } else {
                        int seed = new Random().nextInt(Integer.MAX_VALUE);
                        settings.setSeed(seed);
                    }
                    break;
                default:
                    break;
            }
            Bukkit.getScheduler().runTask(WorldByPlayer.getInstance(), () -> creationManager.nextStep(player));
        }
    }
    private List<String> separateString(String message) {
        String separated = ChatUtils.separateLine(message, 20);
        return Arrays.asList(separated.split("\n"));
    }
}
