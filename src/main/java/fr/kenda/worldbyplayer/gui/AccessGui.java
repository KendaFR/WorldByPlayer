package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AccessGui extends Gui {

    private final FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");

    public AccessGui(String title, int size) {
        super(title, size);
    }

    @Override
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];

        if (configWorld.getConfigurationSection("worlds") == null) {
            //content[4]
        }
        for (String key : configWorld.getConfigurationSection("worlds").getKeys(false)) {

        }

        return content;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }
}
