package fr.kenda.worldbyplayer.datas;

public class CreationSettings {

    private String name = null;
    private int seed = 0;

    /**
     * Get the name given during creation
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of world in creation
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the seed given
     *
     * @return Integer
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Set the seed given of world
     *
     * @param seed Integer
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

}
