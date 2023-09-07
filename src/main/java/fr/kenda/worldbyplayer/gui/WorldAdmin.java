package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

enum WorldAdminStatus {
    MAIN_MENU, WORLDLIST
}

public class WorldAdmin extends Gui {

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final ArrayList<DataWorld> worldList = instance.getWorldManager().getWorldsList();
    private final String prefix = instance.getPrefix();
    private WorldAdminStatus status = WorldAdminStatus.MAIN_MENU;

    public WorldAdmin(String title, Player owner, int row) {
        super(title, owner, row);
    }

    @Override
    public ItemStack[] mainMenu() {
        status = WorldAdminStatus.MAIN_MENU;
        ItemStack[] content = new ItemStack[size];

        String shortcutPurge = "gui.admin.auto-purge.";
        for (int i = 0; i < size; i++) {
            content[i] = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&c").toItemStack();
        }
        content[3] = new ItemBuilder(Config.getMaterial(shortcutPurge + "material"))
                .setName(Config.getString(shortcutPurge + "name"))
                .setLore(Config.getList(shortcutPurge + "lores"))
                .toItemStack();

        String shortcutWorld = "gui.admin.worldList.";
        content[5] = new ItemBuilder(Config.getMaterial(shortcutWorld + "material"))
                .setName(Config.getString(shortcutWorld + "name"))
                .setLore(Config.getList(shortcutWorld + "lores"))
                .toItemStack();
        return content;
    }

    public ItemStack[] worldListMenu() {
        status = WorldAdminStatus.WORLDLIST;
        ItemStack[] content = new ItemStack[size];

        int slot = 0;
        String shortcutWorld = "gui.admin.world.";
        for (DataWorld dataWorld : worldList) {
            Player p = Bukkit.getPlayer(dataWorld.getOwner());
            SkullBuilder skull;
            if (p != null) skull = new SkullBuilder(p);
            else skull = new SkullBuilder(dataWorld.getOwner());
            content[slot++] = skull.setName(Config.getString(shortcutWorld + "name", "{worldName}", dataWorld.getName())).setLores(Config.getList(shortcutWorld + "lores",
                    "{owner}", dataWorld.getOwner(),
                    "{online}", String.valueOf(Objects.requireNonNull(Bukkit.getWorld(dataWorld.getOwner())).getPlayers().size()),
                    "{dateCreation}", dataWorld.getDateOfCreation())).toItemStack();
        }

        String shortcutBack = "gui.admin.back.";
        content[size - 1] = new ItemBuilder(Config.getMaterial(shortcutBack + "material"))
                .setName(Config.getString(shortcutBack + "name")).toItemStack();
        return content;
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        if (e.getInventory() != inventory || e.getInventory() == owner.getInventory()) return;


        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        switch (status) {
            case MAIN_MENU -> {
                switch (clickedSlot) {
                    case 3 -> instance.getWorldManager().autoPurge(owner.getName());
                    case 5 -> {
                        setSize(5);
                        updateContent(worldListMenu());
                    }
                }
            }
            case WORLDLIST -> {
                if (clickedSlot == size - 1) {
                    setSize(1);
                    updateContent(mainMenu());
                } else {
                    ClickType action = e.getClick();
                    DataWorld dataWorld = worldList.get(clickedSlot);
                    if (dataWorld == null) return;
                    switch (action) {
                        case LEFT -> {
                            instance.getAdminManager().getAdminModeList().add(owner);
                            owner.teleport(dataWorld.getWorld().getSpawnLocation());
                            close();
                        }
                        case SHIFT_RIGHT -> {
                            String name = dataWorld.getName();
                            dataWorld.deleteWorld(dataWorld.getWorld());
                            close();
                            owner.sendMessage(prefix + Messages.getMessage("deleted_world", "{world}", name));
                        }
                    }
                }
            }
        }
    }
}
