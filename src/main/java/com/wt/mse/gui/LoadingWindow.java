package com.wt.mse.gui;

import javax.swing.*;
import java.awt.*;

public class LoadingWindow
{
    private final JFrame frame;
    private final JLabel progressLabel;
    private final JLabel playerLabel;

    public LoadingWindow()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(0, 0));

        progressLabel = new JLabel("Loading players... 0/0", SwingConstants.CENTER);
        progressLabel.setFont(UiStyles.DEFAULT_FONT);
        playerLabel = new JLabel("", SwingConstants.CENTER);
        playerLabel.setFont(UiStyles.DEFAULT_FONT);

        frame.add(progressLabel, BorderLayout.CENTER);
        frame.add(playerLabel, BorderLayout.AFTER_LAST_LINE);
        frame.setVisible(true);
    }

    public void updateProgress(int loaded, int total, String playerName)
    {
        progressLabel.setText("Loading players... " + loaded + "/" + total);
        playerLabel.setText(playerName + " loaded.");
    }

    public void close()
    {
        frame.dispose();
    }
}
