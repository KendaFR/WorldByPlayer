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

    public Gui(String title, Player owner, int size, ItemStack[] content) {
        this.owner = owner;
        this.title = title;
        this.size = size;

        Bukkit.getPluginManager().registerEvents(this, WorldByPlayer.getInstance());

        if(title != null)
            inventory = Bukkit.createInventory(owner, size, title);
    }

    public Gui(String title, int size) {
        this(title, null, size, null);
    }

    public Gui(String title, Player owner, int size) {
        this(title, owner, size, null);
    }

    public Gui(int size) {
        this(null, size);
    }

    public void create(Player player) {
        owner = player;
        if(inventory == null)
            inventory = Bukkit.createInventory(owner, size, title);
        owner.openInventory(inventory);
        updateContent(setMenu());
    }

    public void updateContent(ItemStack[] content) {
        inventory.setContents(content);
    }

    public void setTitle(String title){
        this.title = title;
    }
    public abstract ItemStack[] setMenu();

    @EventHandler
    public abstract void onClick(InventoryClickEvent e);


}
