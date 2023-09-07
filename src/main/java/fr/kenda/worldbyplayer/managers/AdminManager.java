package fr.kenda.worldbyplayer.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AdminManager implements IManager {

    private ArrayList<Player> adminModeList;

    @Override
    public void register() {
        adminModeList = new ArrayList<>();
    }

    public boolean isInModeAdmin(Player player) {
        return adminModeList.contains(player);
    }

    public ArrayList<Player> getAdminModeList() {
        return adminModeList;
    }
}
