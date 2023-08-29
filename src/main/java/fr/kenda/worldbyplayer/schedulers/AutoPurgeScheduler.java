package fr.kenda.worldbyplayer.schedulers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.ETimeUnit;

public class AutoPurgeScheduler implements Runnable {

    private int timer = ETimeUnit.DAYS.getToSecond();

    @Override
    public void run() {
        if (timer == 0) {
            WorldByPlayer.getInstance().getWorldManager().autoPurge();
            timer = ETimeUnit.DAYS.getToSecond();
        }
        timer--;
    }
}
