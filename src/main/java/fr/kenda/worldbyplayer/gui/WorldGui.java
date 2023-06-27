package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.List;

public class WorldGui extends Gui{

    private final String shortcut = "gui.world.";
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    private final int separatorLine = 1;

    public WorldGui(int size) {
        super(size);
    }

    @Override
    public ItemStack[] setMenu() {
        DataWorld dataWorld = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromPlayerWorldOwner(owner);
        if(dataWorld == null)
            return new ItemStack[size];

            ItemStack[] content = new ItemStack[size];
        for(int i = separatorLine*9; i < 18; i++){
            content[i] = new ItemBuilder(Config.getMaterial(shortcut + "separator"))
                    .setName(Config.getString(shortcut+ "name_separator"))
                    .toItemStack();
        }
        content[0] = getSkullOfOwner();
        content[1] = new ItemBuilder(Config.getMaterial(shortcut + "title_material")).setName(Config.getString(shortcut + "name_title"))
                .setLore(dataWorld.getName()).toItemStack();

        content[2] = new ItemBuilder(Config.getMaterial(shortcut + "description_material"))
                .setName(Config.getString(shortcut + "name_description"))
                .setLore(dataWorld.getDescription()).toItemStack();

        content[8] = new ItemBuilder(Config.getMaterial(shortcut + "exit_material"))
                .setName(Config.getString(shortcut + "name_exit")).toItemStack();

        int startMember = (separatorLine + 1) * 9;
        for(Player p : Bukkit.getWorld(owner.getName()).getPlayers()){
            content[startMember] = getSkullAndInfoFromPlayer(p);
        }
        return content;
    }

    private ItemStack getSkullOfOwner() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(owner);
        skullMeta.setDisplayName("§e" + owner.getName());
        skull.setItemMeta(skullMeta);
        return skull;
    }

    private ItemStack getSkullAndInfoFromPlayer(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName("§e" + player.getName());
        List<String> lores = Config.getList(shortcut + "lores_player", "{heal}", String.valueOf((int)player.getHealth()),
                "{food}", String.valueOf(player.getFoodLevel()));
        lores.add("§c");
        lores.add(Messages.getMessage("refill_heal_gui"));
        skullMeta.setLore(lores);
        skull.setItemMeta(skullMeta);
        return skull;
    }



    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory) return;

        e.setCancelled(true);

        if (clickedSlot == 8) {
            player.closeInventory();
            return;
        }
            System.out.println("ClickedSlot -> " + clickedSlot);
        if(clickedSlot >= (separatorLine + 1) * 9 && clickedSlot < size){
            int clickedTarget = clickedSlot - (separatorLine + 1) * 9;
            System.out.println("ClickedTarget -> " + clickedTarget);
            Player clickedPlayer = Bukkit.getWorld(owner.getName()).getPlayers().get(clickedTarget);
            if(clickedPlayer == null) return;

            ClickType action = e.getClick();
            if(action == ClickType.LEFT){
                clickedPlayer.setHealth(20);
                clickedPlayer.sendMessage(prefix + Messages.getMessage("refill_heal"));
                player.sendMessage(prefix + Messages.getMessage("refill_heal_to", "{target}", clickedPlayer.getName()));
            }
        }
            
    }
}
