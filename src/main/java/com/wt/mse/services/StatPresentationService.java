package com.wt.mse.services;

import com.wt.mse.model.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StatPresentationService
{
    private final StatFormattingService formattingService;

    public StatPresentationService(StatFormattingService formattingService)
    {
        this.formattingService = formattingService;
    }

    public StatWindowType classify(String category, String stat)
    {
        if(formattingService.isTimeStat(category, stat))
        {
            return StatWindowType.TIME;
        }
        if(formattingService.isDistanceStat(category, stat))
        {
            return StatWindowType.DISTANCE;
        }
        return StatWindowType.PLAIN;
    }

    public String[] getServerwideStats(String category, Collection<Player> players)
    {
        Set<String> uniqueStats = new HashSet<>();
        for(Player player : players)
        {
            uniqueStats.addAll(player.getCategory(category).keySet());
        }
        return uniqueStats.toArray(new String[0]);
    }

    public String[][] generateData(String category, Collection<Player> players, String[] statList) //filling out the data array, getting data from Main.players[]
    {
        String[][] statData = new String[statList.length][4];

        for(int i = 0; i < statList.length; i++)
        {
            String statKey = statList[i];
            statData[i][0] = statKey.replaceFirst("minecraft:", ""); //remove the prefix for clearer display

            long total = 0L;
            int max = 0;
            String recordHolder = "";

            for(Player player : players)
            {
                int value = player.getCategory(category).getOrDefault(statKey, 0);
                total += value;
                if(value > max) //find maximum
                {
                    max = value;
                    recordHolder = player.getNick();
                }
            }

            statData[i][1] = max + "  ";
            statData[i][2] = recordHolder;
            statData[i][3] = total + "  ";
        }

        return statData;
    }

    public String[][] getPlayerData(String category, String stat, Collection<Player> players, boolean formatted) // Method to get data for the table - can be overridden by subclasses
    {
        String key = normalizeStatKey(stat);
        String[][] statData = new String[players.size()][formatted ? 3 : 2]; //  Player | stat value | formatted value (only in child classes)
        int index = 0;

        for(Player player : players)
        {
            int value = player.getCategory(category).getOrDefault(key, 0);
            statData[index][0] = player.getNick();
            statData[index][1] = value + "  ";

            if(formatted)
            {
                StatWindowType type = classify(category, stat);
                statData[index][2] = switch(type)
                {
                    case TIME -> formattingService.formatTime(value);
                    case DISTANCE -> formattingService.formatDistance(value);
                    case PLAIN -> value + "";
                };
            }
            index++;
        }

        return statData;
    }

    public long calculateServerTotal(String category, String stat, Collection<Player> players)
    {
        String key = normalizeStatKey(stat);
        long total = 0L;
        for(Player player : players)
        {
            total += player.getCategory(category).getOrDefault(key, 0);
        }
        return total;
    }

    public java.util.Comparator<String> numericComparator()
    {
        return formattingService.numericComparator();
    }

    public java.util.Comparator<String> timeComparator()
    {
        return formattingService.timeComparator();
    }

    public java.util.Comparator<String> distanceComparator()
    {
        return formattingService.distanceComparator();
    }

    public String formatTime(long ticks)
    {
        return formattingService.formatTime(ticks);
    }

    public String formatDistance(long distanceInCm)
    {
        return formattingService.formatDistance(distanceInCm);
    }

    private String normalizeStatKey(String stat)
    {
        if(stat.startsWith("minecraft:"))
        {
            return stat;
        }
        return "minecraft:" + stat;
    }
}
