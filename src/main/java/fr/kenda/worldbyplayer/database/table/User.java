package fr.kenda.worldbyplayer.database.table;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.database.Table;
import fr.kenda.worldbyplayer.managers.DatabaseManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends Table {


    private final DatabaseManager dbManager = WorldByPlayer.getInstance().getDatabaseManager();

    public User(String tableName) {
        super(tableName);
    }

    @Override
    public void createTable() {
        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement state = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "id INT AUTO_INCREMENT NOT NULL," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "playername VARCHAR(36) NOT NULL," +
                    "PRIMARY KEY(id))");

            state.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertUser(Player player) {
        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement state = connection.prepareStatement("INSERT INTO " + tableName + " (uuid, playername) VALUES (?, ?)");
            state.setString(1, player.getUniqueId().toString());
            state.setString(2, player.getName());

            state.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getID(Player player) {
        if (player == null) return -1;
        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement state = connection.prepareStatement("SELECT id FROM " + tableName + " WHERE uuid = ?");
            state.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = state.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1; // Retourne -1 si aucune correspondance n'a été trouvée
    }

    public String getUUID(Player player) {
        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement state = connection.prepareStatement("SELECT uuid FROM " + tableName + " WHERE uuid = ?");
            state.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = state.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("uuid");
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // Retourne null si aucune correspondance n'a été trouvée
    }
}
