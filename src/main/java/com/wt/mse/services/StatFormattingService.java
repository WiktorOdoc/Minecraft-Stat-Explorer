package com.wt.mse.services;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatFormattingService
{
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");

    public Comparator<String> numericComparator()
    {
        return (left, right) -> Long.compare(parseLong(left), parseLong(right));
    }

    public Comparator<String> timeComparator()
    {
        return (left, right) -> Long.compare(parseFormattedTime(left), parseFormattedTime(right));
    }

    public Comparator<String> distanceComparator()
    {
        return (left, right) -> Long.compare(parseFormattedDistance(left), parseFormattedDistance(right));
    }

    public boolean isTimeStat(String category, String stat)
    {
        return "custom".equals(category) && (stat.contains("time_") || stat.contains("_time"));
    }

    public boolean isDistanceStat(String category, String stat)
    {
        return "custom".equals(category) && stat.endsWith("_one_cm");
    }

    public String formatTime(long time) //format time in x hr, y min, z sec
    {
        long timeInSeconds = time / 20;          //total time in seconds, tick is 0.05s
        long seconds = timeInSeconds % 60;
        long minutes = (timeInSeconds / 60) % 60;
        long hours = timeInSeconds / 3600;
        return "  " + hours + " hr, " + minutes + " min, " + seconds + " sec";
    }

    public String formatDistance(long distance) //distance is in cm
    {
        long cm = distance % 100;
        long meters = (distance / 100) % 1000;
        long kilometers = (distance / 100000);
        return "  " + kilometers + " km, " + meters + " m, " + cm + " cm";
    }

    private long parseLong(String value)
    {
        try
        {
            return Long.parseLong(value.strip());
        }
        catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    private long parseFormattedTime(String value)
    {
        long[] numbers = extractNumbers(value);
        long hours = numbers.length > 0 ? numbers[0] : 0L;
        long minutes = numbers.length > 1 ? numbers[1] : 0L;
        long seconds = numbers.length > 2 ? numbers[2] : 0L;
        return hours * 3600 + minutes * 60 + seconds;
    }

    private long parseFormattedDistance(String value)
    {
        long[] numbers = extractNumbers(value);
        long kilometers = numbers.length > 0 ? numbers[0] : 0L;
        long meters = numbers.length > 1 ? numbers[1] : 0L;
        long centimeters = numbers.length > 2 ? numbers[2] : 0L;
        return kilometers * 100000 + meters * 100 + centimeters;
    }

    private long[] extractNumbers(String value)
    {
        Matcher matcher = NUMBER_PATTERN.matcher(value);
        long[] numbers = new long[4];
        int index = 0;

        while(matcher.find() && index < numbers.length)
        {
            numbers[index++] = Long.parseLong(matcher.group());
        }

        long[] trimmed = new long[index];
        System.arraycopy(numbers, 0, trimmed, 0, index);
        return trimmed;
    }
}
