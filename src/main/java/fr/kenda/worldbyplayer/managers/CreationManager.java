package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.utils.ECreationStatus;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CreationManager {

    private static final HashMap<Player, ECreationStatus> statusCreation = new HashMap<>();
    private static final HashMap<Player, CreationSettings> settingsByPlayer = new HashMap<>();
    private final String prefix = WorldByPlayer.getInstance().getPrefix();

    /**
     * Setup a creation managing
     *
     * @param player Player
     */
    public void setup(Player player) {
        statusCreation.put(player, ECreationStatus.NAME);
        player.sendMessage(prefix + "§a§m===========================");
        player.sendMessage(Messages.getMessage("choose_name"));
        settingsByPlayer.put(player, new CreationSettings());
    }

    /**
     * Complete world generation
     *
     * @param player Player
     */
    public void endGenerating(Player player) {
        statusCreation.remove(player);
        player.sendMessage(Messages.getMessage("end_creation", "{world_name}", settingsByPlayer.get(player).getName()));
        player.sendMessage(prefix + "§a§m===========================");
        WorldByPlayer.getInstance().getWorldManager().createWorld(player, settingsByPlayer.get(player));
        settingsByPlayer.remove(player);

    }

    /**
     * Check if player is in creation mode
     *
     * @param player Player
     * @return Boolean
     */
    public boolean isInCreation(Player player) {
        return statusCreation.get(player) != null;
    }

    /**
     * Get the settings of creation world
     *
     * @param player Player
     * @return CreationSettings
     */
    public CreationSettings getSettingsCreationByPlayer(Player player) {
        return settingsByPlayer.get(player);
    }

    /**
     * Get the status of creation of world
     *
     * @param player Player
     * @return ECreationStatus
     */
    public ECreationStatus getStatusCreation(Player player) {
        return statusCreation.get(player);
    }


    /**
     * Set the next step of creation
     *
     * @param player Player
     */
    public void nextStep(Player player) {
        int nextStatus = statusCreation.get(player).ordinal() + 1;
        ECreationStatus nextEnum = ECreationStatus.values()[nextStatus];
        if (nextEnum == ECreationStatus.END)
            endGenerating(player);
        else
            statusCreation.put(player, nextEnum);
        switch (nextEnum) {
            case NAME -> player.sendMessage(Messages.getMessage("choose_name"));
            case SEED -> player.sendMessage(Messages.getMessage("choose_seed"));
        }
    }

    public void remove(Player player) {
        statusCreation.remove(player);
        settingsByPlayer.remove(player);
    }
}
