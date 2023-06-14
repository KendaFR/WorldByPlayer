package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.database.Table;
import fr.kenda.worldbyplayer.database.table.User;
import fr.kenda.worldbyplayer.database.table.Worlds;
import fr.kenda.worldbyplayer.utils.ETable;

import java.util.HashMap;

public class TableManager implements IManager {

    private final HashMap<String, Table> tables = new HashMap<>();

    @Override
    public void register() {
        createTable(ETable.USER.getName(), new User(ETable.USER.getName()));
        createTable(ETable.WORLDS.getName(), new Worlds(ETable.WORLDS.getName()));
    }

    public void createTable(String name, Table table) {
        table.createTable();
        tables.put(name, table);
    }

    public Table getTableByName(String table) {
        return tables.get(table);
    }

}
