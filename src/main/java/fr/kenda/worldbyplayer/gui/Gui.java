package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Gui implements Listener {

    protected Inventory inventory;
    private final String title;
    private final Player owner;
    private final int size;
    private final ItemStack[] content;

    public Gui(String title, Player owner, int size, ItemStack[] content) {
        this.title = title;
        this.owner = owner;
        this.size = size;
        this.content = content;

        Bukkit.getPluginManager().registerEvents(this, WorldByPlayer.getInstance());

        inventory = Bukkit.createInventory(owner, size, title);
    }

    public Gui(String title, int size) {
        this(title, null, size, null);
    }

    public Gui(String title, Player owner, int size) {
        this(title, owner, size, null);
    }

    public void create() {
        owner.openInventory(inventory);
    }

    public void create(Player player) {
        owner = player;
        owner.openInventory(inventory);
    }

    public void updateContent(ItemStack[] content) {
        inventory.setContents(content);
    }


    public void onClick(InventoryClickEvent e) {

    }
}
