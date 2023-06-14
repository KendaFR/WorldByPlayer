package fr.kenda.worldbyplayer.database.table;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.database.Table;
import fr.kenda.worldbyplayer.managers.DatabaseManager;
import fr.kenda.worldbyplayer.utils.ETable;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Worlds extends Table {

    private final DatabaseManager dbManager = WorldByPlayer.getInstance().getDatabaseManager();


    public Worlds(String tableName){
        super(tableName);
    }
    @Override
    public void createTable() {
        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement state = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "id INT AUTO_INCREMENT NOT NULL," +
                    "owner INT NULL," +
                    "name VARCHAR(255) NOT NULL," +
                    "is_public BOOLEAN NOT NULL DEFAULT 0," +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY(id))");

            state.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertWorld(Player owner, String name, boolean isPublic) {
        Connection connection = dbManager.getConnection();
        if (!existWorld(name)) {
            try {
                PreparedStatement state;
                if (owner != null) {
                    state = connection.prepareStatement("INSERT INTO " + tableName + " (owner, name, is_public) VALUES (?, ?, ?)");
                    User user = (User) WorldByPlayer.getInstance().getTableManager().getTableByName(ETable.USER.getName());
                    state.setInt(1, user.getID(owner));
                    state.setString(2, name);
                    state.setBoolean(3, isPublic);
                } else {
                    state = connection.prepareStatement("INSERT INTO " + tableName + " (name, is_public) VALUES (?, ?)");
                    state.setString(1, name);
                    state.setBoolean(2, isPublic);
                }

                state.executeUpdate();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean existWorld(String name) {
        Connection connection = dbManager.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM " + tableName);
            ResultSet resultSet = statement.executeQuery();
             return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
