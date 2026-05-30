package com.wt.mse.gui;

import com.wt.mse.model.Player;
import com.wt.mse.services.StatPresentationService;

import javax.swing.*;
import java.util.List;

public class StatTabbedWindow //main window
{
    private JFrame frame = new JFrame("Statistic explorer");
    private String[] tabNames = {"custom", "mined", "broken", "dropped", "used", "crafted", "picked_up", "killed", "killed_by"}; //stat categories

    private JTabbedPane tabbedPane;

    public StatTabbedWindow(List<Player> players, StatPresentationService presentationService)
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //open maximized
        frame.setVisible(true);
        tabbedPane = new JTabbedPane();
        for(String tabName : tabNames) //add tabs for categories
        {
            tabbedPane.addTab(tabName, new StatTab(tabName, players, presentationService));
        }

        frame.add(tabbedPane);
    }
}
