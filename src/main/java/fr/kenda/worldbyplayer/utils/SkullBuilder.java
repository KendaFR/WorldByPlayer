package fr.kenda.worldbyplayer.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SkullBuilder {

    private final Player owner;
    private String name;
    private List<String> lores = new ArrayList<>();

    public SkullBuilder(Player owner) {
        this.owner = owner;
        this.name = "§e" + owner.getName();
    }

    public SkullBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SkullBuilder setLore(String line) {
        lores.add(line);
        return this;
    }

    public SkullBuilder setLores(List<String> lines) {
        lores = lines;
        return this;
    }

    public SkullBuilder addLine(String line, int index) {
        lores.add(index, line);
        return this;
    }

    public SkullBuilder addLine(String line) {
        lores.add(line);
        return this;
    }

    public ItemStack toItemStack() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(owner);
        if (name != null) skullMeta.setDisplayName(name);
        if (lores.size() > 0)
            skullMeta.setLore(lores);
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
