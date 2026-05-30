package com.wt.mse.services;

import com.wt.mse.model.Player;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {
    @Test
    void loadsPlayersInDescendingFileSizeOrderAndReportsProgress() throws Exception {
        Path tempDirectory = Files.createTempDirectory("stats-test");
        Path smallerFile = tempDirectory.resolve("11111111-1111-1111-1111-111111111111.json");
        Path largerFile = tempDirectory.resolve("22222222-2222-2222-2222-222222222222.json");

        Files.writeString(smallerFile, """
                {"minecraft:custom":{"minecraft:jump":1}}
                """);
        Files.writeString(largerFile, """
                {"minecraft:custom":{"minecraft:jump":10,"minecraft:play_time":40},"minecraft:mined":{"minecraft:stone":2}}
                """);

        StatisticsService service = new StatisticsService(
                new StatsFileService(),
                uuid -> "nick-" + uuid.substring(0, 4),
                new PlayerStatsParser()
        );

        List<LoadProgress> progressUpdates = new ArrayList<>();
        List<Player> players = service.loadPlayers(tempDirectory, progressUpdates::add);

        assertEquals(2, players.size());
        assertEquals("nick-2222", players.get(0).getNick());
        assertEquals(10, players.get(0).getCategory("custom").get("minecraft:jump"));
        assertEquals(2, players.get(0).getCategory("mined").get("minecraft:stone"));
        assertEquals(2, progressUpdates.size());
        assertEquals("nick-2222", progressUpdates.get(0).playerName());
        assertEquals("nick-1111", progressUpdates.get(1).playerName());
    }
}
