package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Gui implements Listener {

    protected Inventory inventory;
    protected String title;
    protected Player owner;
    protected int size;


    public Gui(String title, int row) {
        this(title, null, row);
    }

    public Gui(String title, Player owner, int row) {
        this.owner = owner;
        this.title = title;
        this.size = row * 9;

        Bukkit.getPluginManager().registerEvents(this, WorldByPlayer.getInstance());
    }

    public Gui(int row) {
        this(null, row);
    }

    public void create(Player player) {
        if (owner == null) owner = player;
        inventory = Bukkit.createInventory(owner, size, title);
        owner.openInventory(inventory);

        updateContent(mainMenu());
    }

    public void create() {
        inventory = Bukkit.createInventory(owner, size, title);
        owner.openInventory(inventory);

        updateContent(mainMenu());
    }

    public void updateContent(ItemStack[] content) {
        inventory.setContents(content);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract ItemStack[] mainMenu();

    public void close() {
        owner.closeInventory();
    }

    protected void setSize(int row) {
        this.size = row * 9;
        create();
    }

    @EventHandler
    public abstract void onClick(InventoryClickEvent e);

}
