package com.wt.mse.services;

import com.wt.mse.gui.LoadingWindow;
import com.wt.mse.gui.StatTabbedWindow;
import com.wt.mse.model.Player;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ApplicationBootstrap
{
    private final Path directoryPath;
    private final StatisticsService statisticsService;
    private final StatPresentationService presentationService;

    public ApplicationBootstrap()
    {
        this(Path.of("stats"), new StatisticsService(), new StatPresentationService(new StatFormattingService()));
    }

    public ApplicationBootstrap(Path statsDirectory, StatisticsService statisticsService, StatPresentationService presentationService)
    {
        this.directoryPath = statsDirectory;
        this.statisticsService = statisticsService;
        this.presentationService = presentationService;
    }

    public void start()
    {
        LoadingWindow loadingFrame = new LoadingWindow(); //Jframe for player loading
        List<Player> players;

        try
        {
            players = statisticsService.loadPlayers(directoryPath, progress ->
                    SwingUtilities.invokeLater(() ->
                    {
                        loadingFrame.updateProgress(progress.loaded(), progress.total(), progress.playerName()); //log loading
                    }));
        }
        catch (IOException | IllegalStateException e)
        {
            loadingFrame.close();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            return;
        }

        loadingFrame.close();
        SwingUtilities.invokeLater(() ->
        {
            new StatTabbedWindow(players, presentationService); //creates the main window
        });
    }
}
