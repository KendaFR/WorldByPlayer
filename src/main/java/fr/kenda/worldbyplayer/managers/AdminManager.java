package fr.kenda.worldbyplayer.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AdminManager implements IManager {

    private ArrayList<Player> adminModeList;

    /**
     * Register Admin manager
     */
    @Override
    public void register() {
        adminModeList = new ArrayList<>();
    }

    /**
     * Check if player is in admin mode
     *
     * @param player Player
     * @return Boolean
     */
    public boolean isInModeAdmin(Player player) {
        return adminModeList.contains(player);
    }

    /**
     * Return the list of player is in admin
     *
     * @return ArrayList<Player></Player>
     */
    public ArrayList<Player> getAdminModeList() {
        return adminModeList;
    }
}
