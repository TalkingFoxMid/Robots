package ru.eatTheFrog.Robots.gui.RMenu;

import javax.swing.*;

public class RMenuBar extends JMenuBar {
    public RMenuBar(JMenu... menus) {
        super();
        for (JMenu m : menus)
            this.add(m);
    }
}
