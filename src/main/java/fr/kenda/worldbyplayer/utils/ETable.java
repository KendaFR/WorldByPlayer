package fr.kenda.worldbyplayer.utils;

public enum ETable {

    USER("user"), WORLDS("worlds");

    private final String name;

    ETable(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
