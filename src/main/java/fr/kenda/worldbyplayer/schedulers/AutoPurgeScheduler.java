package fr.kenda.worldbyplayer.schedulers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;

public class AutoPurgeScheduler implements Runnable {

    private int timer = Config.getInt("auto-purge");

    @Override
    public void run() {
        if (timer == 0) {
            WorldByPlayer.getInstance().getWorldManager().autoPurge();
            timer = Config.getInt("auto-purge");
        }
        timer--;
    }
}
