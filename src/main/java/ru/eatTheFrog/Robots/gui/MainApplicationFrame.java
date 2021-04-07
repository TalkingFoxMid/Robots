package ru.eatTheFrog.Robots.gui;

//import org.apache.maven.plugin.logging.Log;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.Saver;
import ru.eatTheFrog.Robots.gui.RMenu.RMenu;
import ru.eatTheFrog.Robots.gui.RMenu.RMenuBar;
import ru.eatTheFrog.Robots.gui.RMenu.RMenuItem;
import ru.eatTheFrog.Robots.log.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame implements IDisposable, ISavable {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final String AUTO_SAVE_PATH_NAME = "autosave.rbts";
    private final GameWindow gameWindow;
    private final LogWindow logWindow;
    private JFileChooser fileChooser;


    public MainApplicationFrame() {
        desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 25;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        setJMenuBar(
                new RMenuBar(
                        new RMenu("Меню",
                                new RMenuItem("Сохранить", (event) -> {
                                    saveTo(chooseFile());
                                }),
                                new RMenuItem("Загрузить", (event) -> {
                                    loadSaveFrom(chooseFile());
                                }),
                                new RMenuItem("Выход", (event) -> {
                                    YesNoDialogCaller.internalFrameClosing(this);
                                })),
                        new RMenu("Режим отображения", KeyEvent.VK_V, "Управление режимом отображения приложения",
                                new RMenuItem("Системная схема", KeyEvent.VK_S, (event) -> {
                                    setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                                    this.invalidate();
                                }),
                                new RMenuItem("Универсальная схема", KeyEvent.VK_S, (event) -> {
                                    setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                                    this.invalidate();
                                })
                        ),
                        new RMenu("Тесты", KeyEvent.VK_T,
                                "Тестовые команды", new RMenuItem("Сообщение в лог", KeyEvent.VK_S, (event) -> {
                            Logger.debug("Новая строка");
                        })),
                        new RMenu("Скорость игры",
                                new RMenuItem("1x", (event) -> {
                                    gameWindow.setGameSpeed(1);
                                }),
                                new RMenuItem("2x", (event) -> {
                                    gameWindow.setGameSpeed(2);
                                }),
                                new RMenuItem("5x", (event) -> {
                                    gameWindow.setGameSpeed(5);
                                }),
                                new RMenuItem("20x", (event) -> {
                                    gameWindow.setGameSpeed(20);
                                }),
                                new RMenuItem("100x", (event) -> {
                                    gameWindow.setGameSpeed(100);
                                })
                        ))
        );
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        askToLoad();
    }

    protected String chooseFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
            var filter = new FileNameExtensionFilter("Robots save files", "rbts");
            fileChooser.setFileFilter(filter);
            fileChooser.addChoosableFileFilter(filter);
        }
        var answer = fileChooser.showOpenDialog(null);
        if (answer == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getPath();
        return null;
    }

    protected void saveTo(String path) {
        if (path == null)
            return;
        try {
            Saver.saveToFile(this, (path.endsWith(".rbts") ? path : path + ".rbts"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "ERROR - saving gone completely wrong" + e.toString(),
                    "io exception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void showError(String title, String message) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    protected void loadSaveFrom(String path) {
        if (path == null)
            return;
        try {
            Saver.updateFromFile(this, path);
        } catch (InvalidClassException e) {
            showError("old format", "This save file is of old format. Make a new one!");
        }catch (StreamCorruptedException e){
            showError("corrupted file", "This save file is corrupted,");
        } catch (IOException e) {
            System.out.println(e);
            showError("IO exception", String.format("IOException - can't get to %s", path));
        } catch (Exception e) {
            showError("bad format", "Bad save file format");
        }
    }

    public void askToLoad() {
        var answer = JOptionPane.showConfirmDialog(null,
                "Would you like to load autosave??",
                "title",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            loadSaveFrom(AUTO_SAVE_PATH_NAME);
        }
    }


    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        logWindow.invalidate();
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            Saver.saveToFile(this, AUTO_SAVE_PATH_NAME);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "IOException - couldn't save program's state because" + e.getMessage(),
                    "title",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        //out.writeObject(this.gameWindow);
        logWindow.writeExternal(out);
        gameWindow.writeExternal(out);
        out.writeObject(this.getLocation());
        out.writeObject(this.getSize());
        out.write(this.getExtendedState());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //this.desktopPane = (JDesktopPane) in.readObject();
//        this.gameWindow = (GameWindow) in.readObject();
        logWindow.readExternal(in);
        gameWindow.readExternal(in);
        this.setLocation((Point) in.readObject());
        this.setSize((Dimension) in.readObject());
        this.setExtendedState(in.read());
    }
}
