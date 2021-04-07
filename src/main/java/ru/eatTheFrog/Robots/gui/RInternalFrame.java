package ru.eatTheFrog.Robots.gui;

import ru.eatTheFrog.Robots.Savables.ISavable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class RInternalFrame extends JInternalFrame implements IDisposable, ISavable {
    public RInternalFrame(String name, boolean b, boolean b1, boolean b2, boolean b3) {
        super(name, b, b1, b2, b3);
        YesNoDialogCaller.signOnJInternalFrame(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.getLocation());
        out.writeObject(this.getSize());
        out.writeObject(this.isClosed());
        out.writeObject(this.isIcon());
        System.out.println("written: " + this.getLocation().toString() + this.getSize() + this.isClosed);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setLocation((Point) in.readObject());
        this.setSize((Dimension) in.readObject());
        this.isClosed = (Boolean) in.readObject();
        if (this.isClosed)
            this.dispose();
        try {
            this.setIcon((Boolean) in.readObject());
        } catch (PropertyVetoException ignored) {
        }
        System.out.println("read: " + this.getLocation().toString() + this.getSize() + this.isClosed);
    }
}
