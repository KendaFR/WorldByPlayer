package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.Config;
import fr.kenda.worldbyplayer.utils.ItemBuilder;
import fr.kenda.worldbyplayer.utils.Messages;
import fr.kenda.worldbyplayer.utils.SkullBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

enum EInventoryStatus {
    MEMBER, GAMERULE, PLAYER_MODIFY, HOUR, PLAYERS_ALLOWED
}

@SuppressWarnings("null")
public class WorldGui extends Gui {

    public final Map<EInventoryStatus, Integer> slotStatus = Map.of(
            EInventoryStatus.MEMBER, 0,
            EInventoryStatus.GAMERULE, 2,
            EInventoryStatus.HOUR, 3,
            EInventoryStatus.PLAYERS_ALLOWED, 4
    );
    private final String shortcut = "gui.world.";
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    private final int separatorLine = 2;
    private EInventoryStatus statusInventory = EInventoryStatus.MEMBER;
    private Player playerModify = null;
    private DataWorld dataWorld = null;

    public WorldGui(int size) {
        super(size);
    }

    @Override
    public ItemStack[] mainMenu() {
        statusInventory = EInventoryStatus.MEMBER;
        return memberMenu();
    }

    private ItemStack[] memberMenu() {
        ItemStack[] content = new ItemStack[size];

        dataWorld = WorldByPlayer.getInstance().getWorldManager().getDataWorldFromPlayerWorldOwner(owner);
        if (dataWorld == null)
            return new ItemStack[size];

        setMenu(content);
        setPatternSeparatorTemplate(content);


        int startMember = separatorLine * 9;
        for (Player p : Bukkit.getWorld(owner.getName()).getPlayers()) {
            content[startMember] = new SkullBuilder(p).setLores(Config.getList(shortcut + "lores_player", "{heal}", String.valueOf((int) p.getHealth()),
                            "{food}", String.valueOf(p.getFoodLevel()),
                            "{gamemode}", p.getGameMode().name()))
                    .addLine("§c")
                    .addLine(Messages.getMessage("click_modify_player"))
                    .toItemStack();
            startMember++;
        }

        return content;
    }

    /**
     * Set main menu row
     *
     * @param content
     */
    private void setMenu(ItemStack[] content) {
        content[8] = new ItemBuilder(Config.getMaterial(shortcut + "exit_material"))
                .setName(Config.getString(shortcut + "name_exit")).toItemStack();


        content[slotStatus.get(EInventoryStatus.MEMBER)] = new SkullBuilder(owner).setName(Config.getString(shortcut + "members_list_name")).toItemStack();
        content[1] = new ItemBuilder(Config.getMaterial(shortcut + "title_material")).setName(Config.getString(shortcut + "name_title"))
                .setLore(ChatColor.translateAlternateColorCodes('&', dataWorld.getName())).toItemStack();


        content[slotStatus.get(EInventoryStatus.GAMERULE)] = new ItemBuilder(Config.getMaterial(shortcut + "gamerule_material"))
                .setName(Config.getString(shortcut + "gamerule_name"))
                .setLore(Config.getString(shortcut + "gamerule_description")).toItemStack();

        long tickInDay = owner.getWorld().getTime();
        long hourWorld = ((tickInDay / 1000L) + 6L) % 24;

        content[slotStatus.get(EInventoryStatus.HOUR)] = new ItemBuilder(Config.getMaterial(shortcut + "hour_material"))
                .setName(Config.getString(shortcut + "hour_name")).setLore(Config.getString(shortcut + "hour_description",
                        "{hour}", hourWorld > 12 ? hourWorld - 12 + "PM" : hourWorld + "AM")).toItemStack();

        content[slotStatus.get(EInventoryStatus.PLAYERS_ALLOWED)] = new ItemBuilder(Material.SKELETON_SKULL).setName(Config.getString(shortcut + "players_allowed_name"))
                .setLore(Config.getString(shortcut + "players_allowed_description")).toItemStack();
    }

    private ItemStack[] gameruleMenu() {
        ItemStack[] content = new ItemStack[size];
        setMenu(content);
        setPatternSeparatorTemplate(content);

        int index = separatorLine * 9;
        for (String gameruleName : Bukkit.getWorld(owner.getName()).getGameRules()) {
            String value = Bukkit.getWorld(owner.getName()).getGameRuleValue(gameruleName);
            if (isNumeric(value)) {
                List<String> lores = new ArrayList<>(List.of("§fValue: §a" + value));
                lores.addAll(Messages.getMessageList("change_value_gamerule_numeric"));
                content[index] = new ItemBuilder(Material.GRASS_BLOCK).setName("§c" + gameruleName).setLore(lores).toItemStack();
            } else
                content[index] = new ItemBuilder(Material.GRASS_BLOCK).setName("§c" + gameruleName).setLore("§fValue: " + (value.equalsIgnoreCase("true") ? "§a" : "§c") + value, Messages.getMessage("change_value_gamerule")).toItemStack();
            index++;
        }
        return content;
    }

    private ItemStack[] hourMenu() {
        ItemStack[] content = new ItemStack[size];
        setMenu(content);
        setPatternSeparatorTemplate(content);

        int index = separatorLine * 9;
        for (int i = 0; i < 24; i++) {
            String hour = i > 12 ? i - 12 + "PM" : i + "AM";
            content[index] = new ItemBuilder(Material.COMMAND_BLOCK).setName(Messages.getMessage("hour_item", "{hour}", hour)).setLore(Messages.getMessage("hour_change_inventory", "{hour}", hour)).toItemStack();
            index++;
        }
        return content;
    }

    private ItemStack[] playersAllowedMenu() {
        ItemStack[] content = new ItemStack[size];
        setMenu(content);
        setPatternSeparatorTemplate(content);

        if (dataWorld == null) return content;

        int index = separatorLine * 9;
        if (dataWorld.getPlayersAllowed().isEmpty()) {
            content[index] = new ItemBuilder(Config.getMaterial(shortcut + "no_player_material")).setName(Config.getString(shortcut + "no_player_title"))
                    .setLore(Config.getString(shortcut + "no_player_description")).toItemStack();
        } else {
            for (String friends : dataWorld.getPlayersAllowed())
                content[index++] = new SkullBuilder(friends).setLore(Messages.getMessage("remove_player")).toItemStack();
        }
        return content;
    }

    /**
     * Pattern of glass pane
     *
     * @param content
     */
    private void setPatternSeparatorTemplate(ItemStack[] content) {
        for (int i = (separatorLine - 1) * 9; i < separatorLine * 9; i++) {
            content[i] = new ItemBuilder(Config.getMaterial(shortcut + "separator"))
                    .setName(Config.getString(shortcut + "name_separator"))
                    .toItemStack();
        }
    }


    /**
     * Refresh inventory with itemStack[]
     */
    private void refreshInventory() {
        switch (statusInventory) {
            case MEMBER -> updateContent(memberMenu());
            case PLAYER_MODIFY -> updateContent(playerModify());
            case GAMERULE -> updateContent(gameruleMenu());
            case HOUR -> updateContent(hourMenu());
            case PLAYERS_ALLOWED -> updateContent(playersAllowedMenu());
        }
    }


    /**
     * Inventory Modify the playerm
     *
     * @return
     */
    private ItemStack[] playerModify() {
        ItemStack[] content = new ItemStack[size];
        setPatternSeparatorTemplate(content);
        content[8] = new ItemBuilder(Config.getMaterial(shortcut + "back_material"))
                .setName(Config.getString(shortcut + "back")).toItemStack();

        boolean isSurvival = playerModify.getGameMode() == GameMode.SURVIVAL;
        boolean isCreative = playerModify.getGameMode() == GameMode.CREATIVE;
        boolean isAdventure = playerModify.getGameMode() == GameMode.ADVENTURE;
        boolean isSpectator = playerModify.getGameMode() == GameMode.SPECTATOR;

        List<String> gamemodes = new ArrayList<>();
        for (GameMode gm : GameMode.values())
            gamemodes.add(playerModify.getGameMode() == gm ? "§a" + gm : "§c" + gm);


        content[4] = new SkullBuilder(playerModify).toItemStack();
        content[18] = new ItemBuilder(Material.POTION).setName(Messages.getMessage("refill_heal_gui")).toItemStack();
        content[19] = new ItemBuilder(Material.COOKED_CHICKEN).setName(Messages.getMessage("refill_hunger_gui")).toItemStack();
        content[20] = new ItemBuilder(Material.COMMAND_BLOCK).setName(Messages.getMessage("change_gamemode_gui"))
                .setLore(gamemodes).toItemStack();

        return content;
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory) return;

        e.setCancelled(true);

        switch (clickedSlot) {
            case 0 -> statusInventory = EInventoryStatus.MEMBER;
            case 2 -> statusInventory = EInventoryStatus.GAMERULE;
            case 3 -> statusInventory = EInventoryStatus.HOUR;
            case 4 -> statusInventory = EInventoryStatus.PLAYERS_ALLOWED;
            case 8 -> {
                if (statusInventory == EInventoryStatus.PLAYER_MODIFY) {
                    updateContent(mainMenu());
                } else {
                    player.closeInventory();
                }
            }
        }

        refreshInventory();


        if (!(clickedSlot >= separatorLine * 9 && clickedSlot < size)) return;


        switch (statusInventory) {
            case MEMBER -> {
                int clickedTarget = clickedSlot - (separatorLine * 9);
                if (clickedTarget > Bukkit.getWorld(owner.getName()).getPlayers().size()) return;

                Player clickedPlayer = Bukkit.getWorld(owner.getName()).getPlayers().get(clickedTarget);
                if (clickedPlayer == null) return;

                ClickType action = e.getClick();
                if (action == ClickType.LEFT || action == ClickType.RIGHT) {
                    statusInventory = EInventoryStatus.PLAYER_MODIFY;
                    playerModify = clickedPlayer;
                    refreshInventory();
                }
            }
            case PLAYER_MODIFY -> {
                switch (clickedSlot) {
                    case 18 -> {
                        playerModify.setHealth(20);
                        owner.sendMessage(prefix + Messages.getMessage("refill_heal_to", "{target}", playerModify.getName()));
                        playerModify.sendMessage(prefix + Messages.getMessage("refill_heal"));

                    }
                    case 19 -> {
                        playerModify.setFoodLevel(20);
                        owner.sendMessage(prefix + Messages.getMessage("refill_hunger_to", "{target}", playerModify.getName()));
                        playerModify.sendMessage(prefix + Messages.getMessage("refill_hunger"));
                    }
                    case 20 -> {
                        int newValue = playerModify.getGameMode().ordinal() + 1;
                        GameMode gm = GameMode.values()[newValue > GameMode.values().length - 1 ? 0 : newValue];
                        playerModify.setGameMode(gm);
                        playerModify.sendMessage(prefix + Messages.getMessage("change_gamemode_to", "{gamemode}", playerModify.getGameMode().toString()));
                        owner.sendMessage(prefix + Messages.getMessage("change_gamemode", "{gamemode}", gm.toString()));
                        updateContent(playerModify());
                    }
                }
            }
            case GAMERULE -> {
                World world = dataWorld.getWorld();
                int index = world.getGameRules().length - (clickedSlot - separatorLine * 9);
                int gameruleIndex = world.getGameRules().length - index;

                String gameruleName = world.getGameRules()[gameruleIndex];

                String value = world.getGameRuleValue(gameruleName);
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    if (value.equalsIgnoreCase("true")) world.setGameRuleValue(gameruleName, "false");
                    else world.setGameRuleValue(gameruleName, "true");
                }
                //Faire une condition si la variable "value" est numérique
                else if (isNumeric(value)) {
                    int valueNumeric = Integer.parseInt(value);
                    ClickType clickType = e.getClick();
                    switch (clickType) {
                        case LEFT -> valueNumeric++;
                        case RIGHT -> valueNumeric--;
                        case SHIFT_LEFT -> valueNumeric += 10;
                        case SHIFT_RIGHT -> valueNumeric -= 10;
                    }
                    if (gameruleName.equalsIgnoreCase("randomTickSpeed") && valueNumeric >= 1000) {
                        valueNumeric = 1000;
                        player.sendMessage(Messages.getMessage("gamerule_tick_speed", "{limit}", String.valueOf(valueNumeric)));
                    }
                    world.setGameRuleValue(gameruleName, String.valueOf(valueNumeric));
                }
                refreshInventory();
            }
            case HOUR -> {
                World world = dataWorld.getWorld();
                int clicked = (clickedSlot - (separatorLine * 9));
                World currentWorld = player.getWorld();
                long time = 18000 + (clicked * 1000L);
                currentWorld.setTime(time % 24000);
                player.sendMessage(prefix + Messages.getMessage("hour_changed", "{hour}", clicked > 12 ? clicked - 12 + "PM" : clicked + "AM"));

                refreshInventory();
            }
            case PLAYERS_ALLOWED -> {
                ClickType clickType = e.getClick();
                if (clickType == ClickType.SHIFT_LEFT) {
                    int clicked = clickedSlot - 18;
                    String target = dataWorld.getPlayersAllowed().get(clicked);
                    dataWorld.removePlayerFromWorld(target);
                    player.sendMessage(prefix + Messages.getMessage("player_removed", "{target}", target));
                }
                refreshInventory();
            }
        }
    }

    private boolean isNumeric(String value) {
        // Vérifie si la chaîne est numérique en essayant de la convertir en entier.
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}