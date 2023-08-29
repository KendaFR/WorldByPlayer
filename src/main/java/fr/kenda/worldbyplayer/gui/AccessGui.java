package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SkullBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class AccessGui extends Gui {

    private final FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");

    public AccessGui(String title, int size) {
        super(title, size);
    }

    @Override
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];
        final ConfigurationSection section = configWorld.getConfigurationSection("worlds");

        String shortcut = "gui.access.";
        content[size - 1] = new ItemBuilder(Config.getMaterial(shortcut + "exit.material")).setName(Config.getString(shortcut + "exit.name")).toItemStack();

        int index = 0;
        for (String key : section.getKeys(false)) {
            final String keyName = "worlds." + key + ".";
            if (Objects.requireNonNull(configWorld.getList(keyName + ".playersAllowed")).contains(owner.getName())) {
                String nameOfWorld = Messages.transformColor(configWorld.getString(keyName + "name"));
                List<String> lores = Config.getList(shortcut + "lores", "{owner}", key, "{seed}", configWorld.getString(keyName + "seed"));
                content[index] = new SkullBuilder(key).setName(Config.getString(shortcut + "name", "{world_name}", nameOfWorld)).setLores(lores).toItemStack();
                index++;
            }
        }
        if (index == 0)
            content[4] = new ItemBuilder(Config.getMaterial(shortcut + "no_world.material")).setName(Config.getString(shortcut + "no_world.name"))
                    .setLore(Config.getList(shortcut + "no_world.lores")).toItemStack();
        return content;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory) return;

        if (clickedSlot == (size - 1))
            player.closeInventory();
    }

}
