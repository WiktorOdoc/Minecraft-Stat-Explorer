package com.wt.mse.services;

import com.wt.mse.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerStatsParserTest {
    @Test
    void parsesAllKnownSectionsAndNormalizesOldPlayTimeKey() throws Exception {
        String json = """
                {
                  "stats": {
                    "minecraft:custom": {
                      "minecraft:play_one_minute": 120,
                      "minecraft:jump": 5
                    },
                    "minecraft:mined": {
                      "minecraft:stone": 3
                    }
                  }
                }
                """;

        Player player = new Player("uuid", "nick");
        new PlayerStatsParser().loadFromJson(player, json);

        assertEquals(120, player.getCategory("custom").get("minecraft:play_time"));
        assertEquals(5, player.getCategory("custom").get("minecraft:jump"));
        assertEquals(3, player.getCategory("mined").get("minecraft:stone"));
    }
}
