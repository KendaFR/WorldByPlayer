package fr.kenda.worldbyplayer.gui;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.datas.DataWorld;
import fr.kenda.worldbyplayer.utils.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

enum EInventoryStatus {
    MEMBER, GAMERULE, PLAYER_MODIFY, HOUR, PLAYERS_ALLOWED, SETSPAWN, DELETE, INVENTORY_SEE, PVP
}

@SuppressWarnings("all")
public class WorldConfigGui extends Gui {

    public final Map<EInventoryStatus, Integer> slotStatus = Map.of(
            EInventoryStatus.MEMBER, 0,
            EInventoryStatus.GAMERULE, 2,
            EInventoryStatus.HOUR, 3,
            EInventoryStatus.PLAYERS_ALLOWED, 4,
            EInventoryStatus.SETSPAWN, 5,
            EInventoryStatus.DELETE, 6,
            EInventoryStatus.PVP, 7
    );

    private final WorldByPlayer instance = WorldByPlayer.getInstance();
    private final String shortcut = "gui.world.";
    private final String prefix = instance.getPrefix();
    private final int separatorLine = 2;
    private final FileConfiguration config = instance.getConfig();
    private EInventoryStatus statusInventory = EInventoryStatus.MEMBER;
    private Player playerModify = null;
    private String playerModifyName = null;
    private DataWorld dataWorld = null;
    private boolean isInModifyPlayer = false;
    private String nameWorld = "";
    private int page = 1;

    /**
     * Create world menu
     *
     * @param player
     * @param row    number of row
     */
    public WorldConfigGui(Player player, int row) {
        super(player, row);
        nameWorld = owner.getWorld().getName().contains("_") ? owner.getWorld().getName().split("_")[0] : owner.getWorld().getName();
    }

    /**
     * Main Menu
     *
     * @return content of inventory
     */
    @Override
    public ItemStack[] mainMenu() {
        isInModifyPlayer = false;
        statusInventory = EInventoryStatus.MEMBER;
        return memberMenu();
    }

    /**
     * Member Menu
     *
     * @return content of inventory
     */
    private ItemStack[] memberMenu() {
        ItemStack[] content = new ItemStack[size];

        dataWorld = instance.getWorldManager().getDataWorldFromWorldName(nameWorld);
        if (dataWorld == null)
            return new ItemStack[size];

        setMenu(content);
        setPatternSeparatorTemplate(content);


        int startMember = separatorLine * 9;
        World world = Bukkit.getWorld(owner.getName());
        ArrayList<Player> playerName = dataWorld.getAllPlayers();
        for (Player p : playerName) {
            if (instance.getAdminManager().isInModeAdmin(p)) continue;
            String nameWorld = p.getWorld().getName();
            String dim = nameWorld.contains("_") ? nameWorld.split("_")[1] : "world";
            content[startMember] = new SkullBuilder(p).setLores(Config.getList(shortcut + "lores_player", "{heal}", String.valueOf((int) p.getHealth()),
                            "{food}", String.valueOf(p.getFoodLevel()),
                            "{gamemode}", p.getGameMode().name(),
                            "{dimension}", dim))
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

        content[slotStatus.get(EInventoryStatus.PVP)] = new ItemBuilder(Config.getMaterial(shortcut + "pvp_material"))
                .setName(Config.getString(shortcut + "pvp_name"))
                .setLore(Config.getString(shortcut + "pvp_description", "{value}", String.valueOf(owner.getWorld().getPVP()))).toItemStack();
    }


    /**
     * Menu of gamerule
     *
     * @return content of inventory
     */
    private ItemStack[] gameruleMenu() {
        ItemStack[] content = new ItemStack[size];
        setMenu(content);
        setPatternSeparatorTemplate(content);

        World world = Bukkit.getWorld(nameWorld);

        int index = separatorLine * 9;

        for (int i = (page == 1 ? 0 : 35); i < (page == 1 ? size - (separatorLine * 9) - 1 : world.getGameRules().length); i++) {
            String gameruleName = world.getGameRules()[i];
            String value = Bukkit.getWorld(nameWorld).getGameRuleValue(gameruleName);
            if (isNumeric(value)) {
                List<String> lores = new ArrayList<>(List.of("§fValue: §a" + value));
                lores.addAll(Messages.getMessageList("change_value_gamerule_numeric"));
                content[index] = new ItemBuilder(Material.GRASS_BLOCK).setName("§c" + gameruleName).setLore(lores).toItemStack();
            } else
                content[index] = new ItemBuilder(Material.GRASS_BLOCK).setName("§c" + gameruleName).setLore("§fValue: " + (value.equalsIgnoreCase("true") ? "§a" : "§c") + value, Messages.getMessage("change_value_gamerule")).toItemStack();

            index++;
        }
        String skullName = (page == 1) ? "MHF_ArrowRight" : "MHF_ArrowLeft";
        String buttonName = (page == 1) ? "next_page" : "prev_page";
        int slot = (page == 1) ? 35 : 27;
        content[slot + (separatorLine * 9)] = new SkullBuilder(skullName).setName(Messages.getMessage(buttonName)).toItemStack();

        return content;
    }

    /**
     * Hour Menu
     *
     * @return content of inventory
     */
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

    /**
     * Menu of players allowed
     *
     * @return content of inventory
     */
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
     * Menu of inventory see
     *
     * @param target target player
     * @return content of inventory
     */
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


    /**
     * Manage click events in inventory
     *
     * @param e InventoryClickEvent
     */
    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        int clickedSlot = e.getSlot();
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != inventory || e.getInventory() == owner.getInventory()) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        if (clickedSlot >= 0 && clickedSlot <= 8) {
            switch (clickedSlot) {
                case 0:
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.MEMBER;
                        refreshInventory();
                    }
                    break;
                case 2:
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.GAMERULE;
                        refreshInventory();
                    }
                    break;
                case 3:
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.HOUR;
                        refreshInventory();
                    }
                    break;
                case 4:
                    if (!isInModifyPlayer) {
                        statusInventory = EInventoryStatus.PLAYERS_ALLOWED;
                        refreshInventory();
                    }
                    break;
                case 5:
                    Location loc = player.getLocation();
                    player.getWorld().setSpawnLocation(loc);
                    player.sendMessage(prefix + Messages.getMessage("set_spawn", "{location}", LocationTransform.serializeCoordinate(loc)));
                    refreshInventory();
                    break;
                case 6:
                    dataWorld.deleteWorld(dataWorld.getWorld());
                    close();
                    break;
                case 7:
                    World world = owner.getWorld();
                    world.setPVP(!world.getPVP());
                    owner.sendMessage(prefix + Messages.getMessage("pvp_in_world", "{value}", String.valueOf(world.getPVP())));
                    refreshInventory();
                    break;
                case 8:
                    switch (statusInventory) {
                        case PLAYER_MODIFY:
                            isInModifyPlayer = false;
                            updateContent(mainMenu());
                            break;
                        case INVENTORY_SEE:
                            statusInventory = EInventoryStatus.PLAYER_MODIFY;
                            updateContent(playerModify());
                            break;
                        default:
                            close();
                            break;
                    }
                    break;
            }
            return;
        }


        switch (statusInventory) {
            case MEMBER -> {
                int clickedTarget = clickedSlot - (separatorLine * 9);
                if (clickedTarget >= dataWorld.getAllPlayers().size()) return;

                Player clickedPlayer = dataWorld.getAllPlayers().get(clickedTarget);
                if (clickedPlayer == null) break;

                ClickType action = e.getClick();
                if (action == ClickType.LEFT || action == ClickType.RIGHT) {
                    statusInventory = EInventoryStatus.PLAYER_MODIFY;
                    playerModify = clickedPlayer.getWorld() != Bukkit.getWorlds().get(0) ? clickedPlayer : null;
                    if (playerModify == null) break;
                    playerModifyName = playerModify.getName();
                    isInModifyPlayer = true;
                }
                break;
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
                break;
            }
            case GAMERULE -> {
                World world = dataWorld.getWorld();
                int clicked = clickedSlot - separatorLine * 9;

                if (clicked == 35 && page == 1) {
                    page++;
                    break;
                } else if (clicked == 27 && page == 2) {
                    page--;
                    break;
                }
                clicked = page == 2 ? clicked + 35 : clicked;

                String[] gameRules = world.getGameRules(); // Obtenez la liste des règles du jeu une seule fois
                String gameruleName = gameRules[clicked];

                String value = world.getGameRuleValue(gameruleName);

                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    // Utilisez les valeurs boolean plutôt que de comparer des chaînes
                    boolean currentValue = Boolean.parseBoolean(value);
                    world.setGameRuleValue(gameruleName, String.valueOf(!currentValue));
                    dataWorld.getNether().setGameRuleValue(gameruleName, String.valueOf(!currentValue));
                    dataWorld.getEnd().setGameRuleValue(gameruleName, String.valueOf(!currentValue));
                } else if (isNumeric(value)) {
                    int valueNumeric = Integer.parseInt(value);
                    ClickType clickType = e.getClick();
                    // Utilisez un switch pour clarifier le traitement des différents cas
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
                break;
            }
            case HOUR -> {
                World world = dataWorld.getWorld();
                int clicked = (clickedSlot - (separatorLine * 9));
                World currentWorld = player.getWorld();
                long time = 18000 + (clicked * 1000L);
                currentWorld.setTime(time % 24000);
                player.sendMessage(prefix + Messages.getMessage("hour_changed", "{hour}", clicked > 12 ? clicked - 12 + "PM" : clicked + "AM"));
                break;
            }
            case PLAYERS_ALLOWED -> {
                ClickType clickType = e.getClick();
                if (clickType == ClickType.SHIFT_LEFT) {
                    int clicked = clickedSlot - 18;
                    String target = dataWorld.getPlayersAllowed().get(clicked);
                    dataWorld.removePlayerFromWorld(target);
                    player.sendMessage(prefix + Messages.getMessage("player_removed", "{target}", target));
                    Player p = Bukkit.getPlayer(target);
                    if (p != null)
                        p.sendMessage(prefix + Messages.getMessage("removed_from_world", "{world}", dataWorld.getName(), "{owner}", owner.getName()));
                }
                break;
            }
        }
        refreshInventory();
    }

    /**
     * Clear inventory of player modify
     */
    private void clearInventory() {
        if (isInWorldAndValid()) {
            playerModify.getInventory().clear();
            playerModify.sendMessage(prefix + Messages.getMessage("clear_inventory_player"));
            owner.sendMessage(prefix + Messages.getMessage("clear_inventory_target", "{target}", playerModifyName));
        } else owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    /**
     * See inventory of player modify
     */
    private void inventorySee() {
        statusInventory = EInventoryStatus.INVENTORY_SEE;
        updateContent(inventorySeeMenu(playerModify));
    }


    /**
     * teleport target to player
     */
    private void teleportPlayerToMe() {
        if (isInWorldAndValid()) {
            playerModify.teleport(owner.getLocation());
            playerModify.sendMessage(prefix + Messages.getMessage("teleported_to", "{target}", owner.getName()));
            owner.sendMessage(prefix + Messages.getMessage("teleported_player_to_me", "{target}", playerModifyName));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_player", "{target}", playerModifyName));
    }

    /**
     * Teleport to target
     */
    private void teleportTo() {
        if (isInWorldAndValid()) {
            owner.teleport(playerModify.getLocation());
            owner.sendMessage(prefix + Messages.getMessage("teleported_to", "{target}", playerModifyName));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    /**
     * Change gamemode of player modify
     */
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

    /**
     * Refill hunger of target
     */
    private void refillHunger() {
        if (isInWorldAndValid()) {
            playerModify.setFoodLevel(20);
            owner.sendMessage(prefix + Messages.getMessage("refill_hunger_to", "{target}", playerModifyName));
            playerModify.sendMessage(prefix + Messages.getMessage("refill_hunger"));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }


    /**
     * Refill health of target
     */
    private void refillHealth() {
        if (isInWorldAndValid()) {
            playerModify.setHealth(20);
            owner.sendMessage(prefix + Messages.getMessage("refill_heal_to", "{target}", playerModifyName));
            playerModify.sendMessage(prefix + Messages.getMessage("refill_heal"));
        } else
            owner.sendMessage(prefix + Messages.getMessage("offline_world_player", "{target}", playerModifyName));
    }

    /**
     * Checks if the string is numeric
     *
     * @param value String
     * @return Boolean
     */
    private boolean isNumeric(String value) {
        // Vérifie si la chaîne est numérique en essayant de la convertir en entier.
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * Check if target is in world and if is valid when action making on it
     *
     * @return
     */
    private boolean isInWorldAndValid() {
        String basedWorld = playerModify.getWorld().getName();
        String world = basedWorld.contains("_") ? basedWorld.split("_")[0] : basedWorld;
        return playerModify != null &&
                Bukkit.getPlayer(playerModifyName) != null &&
                playerModify.getWorld().getName().contains(world);
    }
}