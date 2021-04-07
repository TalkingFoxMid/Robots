package ru.eatTheFrog.Robots.gui;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

public class YesNoDialogCaller {
    public static void internalFrameClosing(IDisposable cont) {
        int reply = JOptionPane.showConfirmDialog(null,
                "Closing this window will lead to it being closed.",
                "Title",
                JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            cont.dispose();
        }
    }

    public static void signOnJInternalFrame(RInternalFrame internalFrame) {
        internalFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                YesNoDialogCaller.internalFrameClosing(internalFrame);
            }
        });
    }
}
