package fr.kenda.worldbyplayer.datas;

import java.util.List;

public class CreationSettings {

    private String name = null;
    private int seed = 0;
    private List<String> description = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
