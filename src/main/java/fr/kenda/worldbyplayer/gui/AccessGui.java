package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SkullBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AccessGui extends Gui {

    private final FileConfiguration configWorld = WorldByPlayer.getInstance().getFileManager().getConfigFrom("worlds");
    private final String shortcut = "gui.access.";

    public AccessGui(String title, int size) {
        super(title, size);
    }

    @Override
    public ItemStack[] mainMenu() {
        ItemStack[] content = new ItemStack[size];
        final ConfigurationSection section = configWorld.getConfigurationSection("worlds");

        content[size - 1] = new ItemBuilder(Config.getMaterial(shortcut + "back.material")).setName(Config.getString(shortcut + "back.name")).toItemStack();

        if (section == null) {
            content[4] = new ItemBuilder(Config.getMaterial(shortcut + "no_world.material")).setName(Config.getString(shortcut + "no_world.name"))
                    .setLore(Config.getList(shortcut + "no_world.lores")).toItemStack();
            return content;
        }
        int index = 0;
        for (String key : section.getKeys(false)) {
            final String keyName = "worlds." + key + ".";
            if (configWorld.getList(keyName + ".playersAllowed").contains(owner.getName())) {
                String nameOfWorld = Messages.transformColor(configWorld.getString(keyName + "name"));
                List<String> lores = Config.getList(shortcut + "lores", "{owner}", key, "{seed}", configWorld.getString(keyName + "seed"));
                content[index] = new SkullBuilder(key).setName(Config.getString(shortcut + "name", "{world_name}", nameOfWorld)).setLores(lores).toItemStack();
                index++;
            }
        }

        return content;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }
}
