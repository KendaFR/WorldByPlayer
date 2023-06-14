package fr.kenda.worldbyplayer.database;

public class DatabaseCredentials {

    private final String host;
    private final String user;
    private final String password;
    private final String dbName;
    private final int port;

    public DatabaseCredentials(String host, String user, String password, String dbName, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
    }

    public String toURI() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    }



    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }



}
