package fr.kenda.worldbyplayer.schedulers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.CreationSettings;
import fr.kenda.worldbyplayer.managers.WorldsManager;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerCreation extends BukkitRunnable {

    private final Player player;
    private final CreationSettings creationSettings;
    private final WorldsManager worldsManager;
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    private int timer;

    /**
     * Constructor of timer
     *
     * @param player           Player
     * @param creationSettings CreationSettings
     * @param worldsManager    WorldsManager
     */
    public TimerCreation(Player player, CreationSettings creationSettings, WorldsManager worldsManager) {
        this.player = player;
        this.creationSettings = creationSettings;
        this.worldsManager = worldsManager;
        timer = Config.getInt("timer");
    }

    /**
     * TImer before creating world
     */
    @Override
    public void run() {
        WorldByPlayer.getInstance().getServer().getOnlinePlayers().forEach(p -> p.sendMessage(prefix + Messages.getMessage("world_created_in", "{x}", String.valueOf(timer))));
        if (timer == 0) {
            worldsManager.createWorld(player, creationSettings);
            cancel();
        }
        timer--;
    }
}
