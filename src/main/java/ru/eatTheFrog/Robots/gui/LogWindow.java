package ru.eatTheFrog.Robots.gui;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.log.LogChangeListener;
import ru.eatTheFrog.Robots.log.LogEntry;
import ru.eatTheFrog.Robots.log.LogWindowSource;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends RInternalFrame implements LogChangeListener
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    @Override
    public void dispose(){
        super.dispose();
        if (this.m_logSource != null)
            this.m_logSource.unregisterListener(this);
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public boolean isListenerClosed() {
        return this.isClosed;
    }
}
