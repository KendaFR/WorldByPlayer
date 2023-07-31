package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.utils.ECreationStatus;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CreationManager {

    private static final HashMap<Player, ECreationStatus> statusCreation = new HashMap<>();
    private static final HashMap<Player, ECreationStatus> statusModify = new HashMap<>();
    private static final HashMap<Player, CreationSettings> settingsByPlayer = new HashMap<>();
    private final String prefix = WorldByPlayer.getInstance().getPrefix();

    public void setup(Player player) {
        statusCreation.put(player, ECreationStatus.NAME);
        player.sendMessage(prefix + "§a§m===========================");
        player.sendMessage(Messages.getMessage("choose_name"));
        settingsByPlayer.put(player, new CreationSettings());
    }

    public void setPlayerModify(Player player, ECreationStatus status, boolean active) {
        if (active && status != null)
            statusModify.put(player, status);
        else statusModify.remove(player);
    }

    public void endGenerating(Player player) {
        statusCreation.remove(player);
        player.sendMessage(Messages.getMessage("end_creation", "{world_name}", settingsByPlayer.get(player).getName()));
        player.sendMessage(prefix + "§a§m===========================");
        WorldByPlayer.getInstance().getWorldManager().createWorld(player, settingsByPlayer.get(player));
        settingsByPlayer.remove(player);

    }

    public boolean isInModification(Player player) {
        return statusModify.get(player) != null;
    }

    public boolean isInCreation(Player player) {
        return statusCreation.get(player) != null;
    }

    public CreationSettings getSettingsCreationByPlayer(Player player) {
        return settingsByPlayer.get(player);
    }

    public ECreationStatus getStatusCreation(Player player) {
        return statusCreation.get(player);
    }

    public ECreationStatus getStatusModify(Player player) {
        return statusModify.get(player);
    }

    public void nextStep(Player player) {
        int nextStatus = statusCreation.get(player).ordinal() + 1;
        ECreationStatus nextEnum = ECreationStatus.values()[nextStatus];
        if (nextEnum == ECreationStatus.END)
            endGenerating(player);
        else
            statusCreation.put(player, nextEnum);
        switch (nextEnum) {
            case NAME -> player.sendMessage(Messages.getMessage("choose_name"));
            case DESCRIPTION -> player.sendMessage(Messages.getMessage("choose_description"));
            case SEED -> player.sendMessage(Messages.getMessage("choose_seed"));
        }
    }
}
