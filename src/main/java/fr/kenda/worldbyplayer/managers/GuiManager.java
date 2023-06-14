package fr.kenda.worldbyplayer.managers;

import fr.kenda.worldbyplayer.gui.Gui;
import fr.kenda.worldbyplayer.gui.NavigationGui;
import fr.kenda.worldbyplayer.utils.Config;

import java.util.HashMap;

public class GuiManager implements IManager {


    private final HashMap<String, Gui> guis = new HashMap<>();

    @Override
    public void register() {
        guis.put("navigation", new NavigationGui(Config.getName("gui.navigation.title"), 9));
    }

    public Gui getGui(final String name) {
        return guis.get(name);
    }
}
