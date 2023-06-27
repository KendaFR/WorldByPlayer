package fr.kenda.worldbyplayer.datas;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DataPlayerPerWorld {

    private double life;
    private float experience;
    private int food;
    private Inventory inventory;
    private Location location;

    public DataPlayerPerWorld(Player player) {
        life = player.getHealth();
        experience = player.getExp();
        food = player.getFoodLevel();
        inventory = player.getInventory();
        location = player.getLocation();
    }

    public double getLife() {
        return life;
    }

    public float getExperience() {
        return experience;
    }

    public int getFood() {
        return food;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }

    public void saveData(Player player) {
        life = player.getHealth();
        experience = player.getExp();
        food = player.getFoodLevel();
        inventory = player.getInventory();
        location = player.getLocation();
    }
}
