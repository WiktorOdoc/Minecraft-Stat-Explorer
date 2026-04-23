import java.util.*;
import java.util.regex.*;
public class Player
{
    private String uuid;
    private String nick;

    private Map<String, Integer> custom = new HashMap<>();
    private Map<String, Integer> mined = new HashMap<>();
    private Map<String, Integer> broken = new HashMap<>();
    private Map<String, Integer> dropped = new HashMap<>();
    private Map<String, Integer> used = new HashMap<>();
    private Map<String, Integer> crafted = new HashMap<>();
    private Map<String, Integer> picked_up = new HashMap<>();
    private Map<String, Integer> killed = new HashMap<>();
    private Map<String, Integer> killed_by = new HashMap<>();

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


    public void loadFromJson(String json)
    {
        // Define mappings for each section
        Map<String, Map<String, Integer>> sections = new HashMap<>();
        sections.put("minecraft:custom", custom);
        sections.put("minecraft:mined", mined);
        sections.put("minecraft:broken", broken);
        sections.put("minecraft:dropped", dropped);
        sections.put("minecraft:used", used);
        sections.put("minecraft:crafted", crafted);
        sections.put("minecraft:picked_up", picked_up);
        sections.put("minecraft:killed", killed);
        sections.put("minecraft:killed_by", killed_by);


        for (String key : sections.keySet()) // Loop through each section and populate the corresponding stat map
        {
            Pattern sectionPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\\{(.*?)}", Pattern.DOTALL); //regex for "category":(whole_category)}
            Matcher sectionMatcher = sectionPattern.matcher(json);
            if (sectionMatcher.find())
            {
                String sectionContent = sectionMatcher.group(1);
                populateMap(sectionContent, sections.get(key)); //add fields to the matched category
            }
        }
    }

    private void populateMap(String section, Map<String, Integer> map)
    {
        Pattern entryPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)"); //regex for "statistic":"value"
        Matcher entryMatcher = entryPattern.matcher(section);
        while (entryMatcher.find()) //do for all entries
        {
            String key = entryMatcher.group(1);
            if(key.equals("minecraft:play_one_minute")) //for mc version 1.15 and earlier the stat was stored like this. This is to handle old players.
                key = "minecraft:play_time";
            int value = Integer.parseInt(entryMatcher.group(2));
            map.put(key, value); //add field to map
        }
    }
}
