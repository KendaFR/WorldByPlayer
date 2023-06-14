package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.WorldByPlayer;
import fr.kenda.worldbyplayer.database.DatabaseAccess;
import fr.kenda.worldbyplayer.database.DatabaseCredentials;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager implements IManager {

    private final FileConfiguration dbConfig = WorldByPlayer.getInstance().getFileManager().getConfigFrom("database");
    private DatabaseAccess databaseAccess;

    @Override
    public void register() {
        String host = dbConfig.getString("host");
        int port = dbConfig.getInt("port");
        String user = dbConfig.getString("username");
        String password = dbConfig.getString("password");
        String dbName = dbConfig.getString("database");
        databaseAccess = new DatabaseAccess(new DatabaseCredentials(host, user, password, dbName, port));

        databaseAccess.initPool();
    }

    public void closePool() {
        databaseAccess.closePool();
    }


    public Connection getConnection() {
        try {
            return databaseAccess.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
