package ru.eatTheFrog.Robots.gui;

import ru.eatTheFrog.Robots.gui.visualizers.GameVisualizer;
import ru.eatTheFrog.Robots.model.GameAndArbitration.Game;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class GameWindow extends RInternalFrame {
    private final GameVisualizer m_visualizer;
    private final Game game;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        game = new Game();
        m_visualizer = new GameVisualizer(game);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        m_visualizer.startTimers();
    }
    public void setGameSpeed(int gameSpeed){
        this.m_visualizer.setGameSpeed(gameSpeed);
    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        super.writeExternal(out);
//        out.writeObject(game);
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//       super.readExternal(in);
//       game.readExternal(in);
//    }
}
