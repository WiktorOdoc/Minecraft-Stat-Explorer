package com.wt.mse.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt.mse.model.Player;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerStatsParser
{
    private static final Map<String, String> SECTIONS = new LinkedHashMap<>();

    static
    {
        SECTIONS.put("minecraft:custom", "custom");
        SECTIONS.put("minecraft:mined", "mined");
        SECTIONS.put("minecraft:broken", "broken");
        SECTIONS.put("minecraft:dropped", "dropped");
        SECTIONS.put("minecraft:used", "used");
        SECTIONS.put("minecraft:crafted", "crafted");
        SECTIONS.put("minecraft:picked_up", "picked_up");
        SECTIONS.put("minecraft:killed", "killed");
        SECTIONS.put("minecraft:killed_by", "killed_by");
    }

    private final ObjectMapper mapper;

    public PlayerStatsParser()
    {
        this(new ObjectMapper());
    }

    public PlayerStatsParser(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    public void loadFromJson(Player player, String json) throws IOException
    {
        JsonNode root = mapper.readTree(json);
        JsonNode statsRoot = root.get("stats");
        if(statsRoot != null && statsRoot.isObject())
        {
            root = statsRoot;
        }

        for(String key : SECTIONS.keySet()) // Loop through each section and populate the corresponding stat map
        {
            JsonNode section = root.get(key);
            if(section == null)
            {
                continue;
            }

            populateMap(section, player.getCategory(SECTIONS.get(key)));
        }
    }

    private void populateMap(JsonNode section, Map<String, Integer> map)
    {
        section.fields().forEachRemaining(entry ->
        {
            String key = normalizeKey(entry.getKey());
            if(!entry.getValue().canConvertToLong())
            {
                return;
            }

            int value = entry.getValue().asInt();
            map.put(key, value); //add field to map
        });
    }

    private String normalizeKey(String key)
    {
        if("minecraft:play_one_minute".equals(key)) //for mc version 1.15 and earlier the stat was stored like this. This is to handle old players.
        {
            key = "minecraft:play_time";
        }

        return key;
    }
}
