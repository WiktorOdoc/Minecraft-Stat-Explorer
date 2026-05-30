package com.wt.mse.model;

import java.util.HashMap;
import java.util.Map;

public class Player
{
    private final String uuid;
    private final String nick;

    private final Map<String, Integer> custom = new HashMap<>();
    private final Map<String, Integer> mined = new HashMap<>();
    private final Map<String, Integer> broken = new HashMap<>();
    private final Map<String, Integer> dropped = new HashMap<>();
    private final Map<String, Integer> used = new HashMap<>();
    private final Map<String, Integer> crafted = new HashMap<>();
    private final Map<String, Integer> picked_up = new HashMap<>();
    private final Map<String, Integer> killed = new HashMap<>();
    private final Map<String, Integer> killed_by = new HashMap<>();

    public Player(String uuid, String nick)
    {
        this.uuid = uuid;
        this.nick = nick;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getNick()
    {
        return nick;
    }

    public Map<String, Integer> getCategory(String category) //returns category from string id
    {
        switch (category) // Determine which map to use based on category
        {
            case "custom" -> {return custom;}
            case "mined" -> {return mined;}
            case "broken" -> {return broken;}
            case "dropped" -> {return dropped;}
            case "used" -> {return used;}
            case "crafted" -> {return crafted;}
            case "picked_up" -> {return picked_up;}
            case "killed" -> {return killed;}
            case "killed_by" -> {return killed_by;}
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        }
    }
}
