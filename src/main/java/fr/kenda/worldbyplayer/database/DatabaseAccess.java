package fr.kenda.worldbyplayer.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.kenda.worldbyplayer.WorldByPlayer;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseAccess {

    private final DatabaseCredentials credentials;

    private HikariDataSource hikariDataSource;

    public DatabaseAccess(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }


    public void setupHikariCP() {
        final HikariConfig hikariConfig = new HikariConfig();

//Maximum pool connection
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(credentials.toURI());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPassword());

//temps maximum que la connection reste dans le pool avant d'etre regen
        hikariConfig.setMaxLifetime(10 * 60 * 1000);

//Temps max qu'une connection non utilisé reste
        hikariConfig.setIdleTimeout(5 * 60 * 1000);

//Temps max qu'une connection peut etre use dans le pool (fuite mémoire)
        hikariConfig.setLeakDetectionThreshold(10 * 60 * 1000);

//Temps max qu'une connection cherche la connection
        hikariConfig.setConnectionTimeout(60 * 1000);

        this.hikariDataSource = new HikariDataSource(hikariConfig);

    }

    public void initPool() {
        setupHikariCP();
    }

    public void closePool() {
        this.hikariDataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            Bukkit.getConsoleSender().sendMessage(WorldByPlayer.getInstance().getPrefix() + "&cAn error occurred while retrieving the connection to the database. Try reconnecting...");
            setupHikariCP();
        }
        return this.hikariDataSource.getConnection();
    }
}
