package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

enum EInventoryStatus {
    MEMBER, GAMERULE, PLAYER_MODIFY, HOUR, PLAYERS_ALLOWED, SETSPAWN, DELETE, INVENTORY_SEE
}

@SuppressWarnings("all")
public class WorldGui extends Gui {

    public final Map<EInventoryStatus, Integer> slotStatus = Map.of(
            EInventoryStatus.MEMBER, 0,
            EInventoryStatus.GAMERULE, 2,
            EInventoryStatus.HOUR, 3,
            EInventoryStatus.PLAYERS_ALLOWED, 4,
            EInventoryStatus.SETSPAWN, 5,
            EInventoryStatus.DELETE, 6
    );

    private final String shortcut = "gui.world.";
    private final String prefix = WorldByPlayer.getInstance().getPrefix();
    private final int separatorLine = 2;
    private final FileConfiguration config = WorldByPlayer.getInstance().getConfig();
    private EInventoryStatus statusInventory = EInventoryStatus.MEMBER;
    private Player playerModify = null;
    private String playerModifyName = null;
    private DataWorld dataWorld = null;
    private boolean isInModifyPlayer = false;

    public WorldGui(int size) {
        super(size);
    }

    @Override
    public ItemStack[] mainMenu() {
        isInModifyPlayer = false;
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
        for (Player p : Objects.requireNonNull(Bukkit.getWorld(owner.getName())).getPlayers()) {
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
     * @param content ItemStack[]
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

        content[slotStatus.get(EInventoryStatus.SETSPAWN)] = new ItemBuilder(Config.getMaterial(shortcut + "setspawn_material"))
                .setName(Config.getString(shortcut + "setspawn_name"))
                .setLore(Config.getString(shortcut + "setspawn_description", "{spawn}",
                        LocationTransform.serializeCoordinate(dataWorld.getWorld().getSpawnLocation()))).toItemStack();

        content[slotStatus.get(EInventoryStatus.DELETE)] = new ItemBuilder(Config.getMaterial(shortcut + "delete_material"))
                .setName(Config.getString(shortcut + "delete_name"))
                .setLore(Config.getString(shortcut + "delete_description")).toItemStack();
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

    private ItemStack[] inventorySeeMenu(Player target) {
        ItemStack[] content = new ItemStack[size];
        setPatternSeparatorTemplate(content);
        content[8] = new ItemBuilder(Config.getMaterial(shortcut + "back_material"))
                .setName(Config.getString(shortcut + "name_back")).toItemStack();
        content[4] = new SkullBuilder(playerModify).toItemStack();

        int indexInventory = 3 * 9;
        ItemStack[] inventory = target.getInventory().getContents();
        for (int i = 0; i < inventory.length - 5; i++) {
            ItemStack item = inventory[i];
            if (item == null) continue;
            content[indexInventory++] = item;
        }

        int indexArmor = 2 * 9;
        ItemStack[] armor = target.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item == null) continue;
            content[indexArmor++] = item;
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
     * Inventory Modify the player
     *
     * @return
     */
    private ItemStack[] playerModify() {
        ItemStack[] content = new ItemStack[size];
        setPatternSeparatorTemplate(content);
        content[8] = new ItemBuilder(Config.getMaterial(shortcut + "back_material"))
                .setName(Config.getString(shortcut + "name_back")).toItemStack();

        boolean isSurvival = playerModify.getGameMode() == GameMode.SURVIVAL;
        boolean isCreative = playerModify.getGameMode() == GameMode.CREATIVE;
        boolean isAdventure = playerModify.getGameMode() == GameMode.ADVENTURE;
        boolean isSpectator = playerModify.getGameMode() == GameMode.SPECTATOR;

        List<String> gamemodes = new ArrayList<>();
        for (GameMode gm : GameMode.values())
            gamemodes.add(playerModify.getGameMode() == gm ? "§a" + gm : "§c" + gm);


        int startIndex = 18;

        content[4] = new SkullBuilder(playerModify).toItemStack();

        for (String key : config.getConfigurationSection("gui.members").getKeys(false)) {
            if (key.equalsIgnoreCase("gamemode")) {
                List<String> lores = Config.getList("gui.members." + key + ".lores");
                lores.addAll(gamemodes);
                content[startIndex++] = new ItemBuilder(Config.getMaterial("gui.members." + key + ".material"))
                        .setName(Config.getString("gui.members." + key + ".name"))
                        .setLore(lores).toItemStack();
            } else
                content[startIndex++] = new ItemBuilder(Config.getMaterial("gui.members." + key + ".material"))
                        .setName(Config.getString("gui.members." + key + ".name"))
                        .setLore(Config.getList("gui.members." + key + ".lores")).toItemStack();
        }

        return content;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory) return;

        e.setCancelled(true);

        if (clickedSlot >= 0 && clickedSlot <= 8) {
            switch (clickedSlot) {
                case 0 -> {
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.MEMBER;
                        refreshInventory();
                        return;
                    }
                }
                case 2 -> {
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.GAMERULE;
                        refreshInventory();
                        return;
                    }
                }
                case 3 -> {
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.HOUR;
                        refreshInventory();
                        return;
                    }
                }
                case 4 -> {
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.PLAYERS_ALLOWED;
                        refreshInventory();
                        return;
                    }
                }
                case 8 -> {
                    switch (statusInventory) {
                        case PLAYER_MODIFY -> {
                            isInModifyPlayer = false;
                            updateContent(mainMenu());
                            return;
                        }
                        case INVENTORY_SEE -> {
                            statusInventory = EInventoryStatus.PLAYER_MODIFY;
                            updateContent(playerModify());
                            return;
                        }
                        default -> {
                            player.closeInventory();
                            return;
                        }
                    }
                }
            }
        } else {
            switch (statusInventory) {
                case MEMBER -> {
                    if (isInModifyPlayer) return;
                    int clickedTarget = clickedSlot - (separatorLine * 9);
                    if (clickedTarget >= Bukkit.getWorld(owner.getName()).getPlayers().size()) return;

                    Player clickedPlayer = Bukkit.getWorld(owner.getName()).getPlayers().get(clickedTarget);
                    if (clickedPlayer == null) break;

                    ClickType action = e.getClick();
                    if (action == ClickType.LEFT || action == ClickType.RIGHT) {
                        statusInventory = EInventoryStatus.PLAYER_MODIFY;
                        playerModify = clickedPlayer.getWorld() != Bukkit.getWorlds().get(0) ? clickedPlayer : null;
                        if (playerModify == null) break;
                        playerModifyName = playerModify.getName();
                        isInModifyPlayer = true;
                    }
                }
                case PLAYER_MODIFY -> {
                    switch (clickedSlot) {
                        case 18 -> refillHealth();
                        case 19 -> refillHunger();
                        case 20 -> changeGamemode();
                        case 21 -> teleportTo();
                        case 22 -> teleportPlayerToMe();
                        case 23 -> inventorySee();
                        case 24 -> clearInventory();
                    }
                }
                case GAMERULE -> {
                    if (isInModifyPlayer) return;
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
                }
                case HOUR -> {
                    if (isInModifyPlayer) return;
                    World world = dataWorld.getWorld();
                    int clicked = (clickedSlot - (separatorLine * 9));
                    World currentWorld = player.getWorld();
                    long time = 18000 + (clicked * 1000L);
                    currentWorld.setTime(time % 24000);
                    player.sendMessage(prefix + Messages.getMessage("hour_changed", "{hour}", clicked > 12 ? clicked - 12 + "PM" : clicked + "AM"));

                }
                case PLAYERS_ALLOWED -> {
                    if (isInModifyPlayer) return;
                    ClickType clickType = e.getClick();
                    if (clickType == ClickType.SHIFT_LEFT) {
                        int clicked = clickedSlot - 18;
                        String target = dataWorld.getPlayersAllowed().get(clicked);
                        dataWorld.removePlayerFromWorld(target);
                        player.sendMessage(prefix + Messages.getMessage("player_removed", "{target}", target));
                    }
                }
                default -> {
                    if (clickedSlot == EInventoryStatus.SETSPAWN.ordinal()) {
                        Location loc = player.getLocation();
                        dataWorld.getWorld().setSpawnLocation(loc);
                        player.sendMessage(prefix + Messages.getMessage("set_spawn", "{location}", LocationTransform.serializeCoordinate(loc)));
                        refreshInventory();
                    } else if (clickedSlot == EInventoryStatus.DELETE.ordinal()) {
                        dataWorld.deleteWorld(dataWorld.getWorld());
                        return;
                    }
                    if (!(clickedSlot >= separatorLine * 9 && clickedSlot < size)) return;
                }
            }
            refreshInventory();
        }
    }

    private void clearInventory() {
        if (isInWorldAndValid()) {
            playerModify.getInventory().clear();
            playerModify.sendMessage(prefix + Messages.getMessage("clear_inventory_player"));
            owner.sendMessage(prefix + Messages.getMessage("clear_inventory_target", "{target}", playerModifyName));
        } else owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    private void inventorySee() {
        statusInventory = EInventoryStatus.INVENTORY_SEE;
        updateContent(inventorySeeMenu(playerModify));
    }


    private void teleportPlayerToMe() {
        if (isInWorldAndValid()) {
            playerModify.teleport(owner.getLocation());
            playerModify.sendMessage(prefix + Messages.getMessage("teleported_to", "{target}", owner.getName()));
            owner.sendMessage(prefix + Messages.getMessage("teleported_player_to_me", "{target}", playerModifyName));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_player", "{target}", playerModifyName));
    }

    private void teleportTo() {
        if (isInWorldAndValid()) {
            owner.teleport(playerModify.getLocation());
            owner.sendMessage(prefix + Messages.getMessage("teleported_to", "{target}", playerModifyName));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    private void changeGamemode() {
        if (isInWorldAndValid()) {
            int newValue = playerModify.getGameMode().ordinal() + 1;
            GameMode gm = GameMode.values()[newValue > GameMode.values().length - 1 ? 0 : newValue];
            playerModify.setGameMode(gm);
            playerModify.sendMessage(prefix + Messages.getMessage("change_gamemode_to", "{gamemode}", playerModify.getGameMode().toString()));
            owner.sendMessage(prefix + Messages.getMessage("change_gamemode", "{target}", playerModifyName, "{gamemode}", gm.toString()));
            updateContent(playerModify());
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    private void refillHunger() {
        if (isInWorldAndValid()) {
            playerModify.setFoodLevel(20);
            owner.sendMessage(prefix + Messages.getMessage("refill_hunger_to", "{target}", playerModifyName));
            playerModify.sendMessage(prefix + Messages.getMessage("refill_hunger"));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }


    private void refillHealth() {
        if (isInWorldAndValid()) {
            playerModify.setHealth(20);
            owner.sendMessage(prefix + Messages.getMessage("refill_heal_to", "{target}", playerModifyName));
            playerModify.sendMessage(prefix + Messages.getMessage("refill_heal"));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
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


    private boolean isInWorldAndValid() {
        return playerModify != null &&
                Bukkit.getPlayer(playerModifyName) != null &&
                playerModify.getWorld().getName().equalsIgnoreCase(dataWorld.getWorld().getName());
    }
}