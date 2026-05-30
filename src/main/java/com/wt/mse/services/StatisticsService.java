package com.wt.mse.services;

import com.wt.mse.model.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StatisticsService
{
    private final StatsFileService fileService;
    private final NicknameService nicknameService;
    private final PlayerStatsParser parser;

    public StatisticsService()
    {
        this(new StatsFileService(), new HttpNicknameService(), new PlayerStatsParser());
    }

    public StatisticsService(StatsFileService fileService, NicknameService nicknameService, PlayerStatsParser parser)
    {
        this.fileService = fileService;
        this.nicknameService = nicknameService;
        this.parser = parser;
    }

    public List<Player> loadPlayers(Path statsDirectory, Consumer<LoadProgress> progressConsumer) throws IOException
    {
        java.io.File[] allFiles = fileService.getFilesBySize(statsDirectory); //gets all files from folder. Orders by descending file size so most active players are first.
        if(allFiles.length == 0)
        {
            throw new IllegalStateException("Stat files not found!");
        }

        List<Player> players = new ArrayList<>();
        for(int i = 0; i < allFiles.length; i++)
        {
            java.io.File file = allFiles[i];
            String uuid = file.getName().replace(".json", ""); //get uuid
            Player player = new Player(uuid, nicknameService.nickFromUUID(uuid)); //TODO add caching
            parser.loadFromJson(player, fileService.getJsonString(file.getName(), statsDirectory)); //load statistics for player
            players.add(player);

            if(progressConsumer != null)
            {
                progressConsumer.accept(new LoadProgress(i + 1, allFiles.length, player.getNick())); //log loading
            }
        }

        return List.copyOf(players);
    }
}
