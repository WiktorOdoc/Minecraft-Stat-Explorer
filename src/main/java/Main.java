import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.http.HttpResponse.*;
import java.time.Duration;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main
{
    private static String directoryPath = "stats"; //relative path
    private static Player[] players;

    public static Comparator<String> numericComparator;

    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);

    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException
    {

        File[] allFiles = getFilesBySize(directoryPath);

        players = new Player[allFiles.length]; //initialize player array

        JFrame loadingFrame = new JFrame(); //Jframe for player loading
        loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadingFrame.setSize(300, 100);
        loadingFrame.setLocationRelativeTo(null); // Center the frame
        JLabel loadingLabel = new JLabel("Loading players... 0/" + allFiles.length, SwingConstants.CENTER);
        loadingLabel.setFont(DEFAULT_FONT);
        JLabel playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setFont(DEFAULT_FONT);
        loadingFrame.setLayout(new BorderLayout(0, 0));
        loadingFrame.add(loadingLabel, BorderLayout.CENTER);
        loadingFrame.add(playerNameLabel, BorderLayout.AFTER_LAST_LINE);
        loadingFrame.setVisible(true);

        numericComparator = (String s1, String s2) ->
        {
            try {
                long num1 = Long.parseLong(s1.strip());
                long num2 = Long.parseLong(s2.strip());
                return Long.compare(num1, num2);  // Compare numerically
            } catch (NumberFormatException e) {
                return 0;  // In case of non-numeric values, treat them as equal
            }
        };

        String tempUUID; //get nicks of players and load stats
        for(int i = 0; i < allFiles.length; i++)
        {
            tempUUID = allFiles[i].getName().replace(".json", ""); //get uuid
            players[i] = new Player(tempUUID, nickFromUUID(tempUUID)); //TODO add caching
            players[i].loadFromJson(getJsonString(allFiles[i].getName())); //load statistics for player
            loadingLabel.setText("Loading players... " + (i+1) + "/" + allFiles.length); //log loading
            playerNameLabel.setText(players[i].getNick() + " loaded.");
            System.out.println("Player " + players[i].getNick() + " loaded.");
        }
        loadingFrame.dispose();

        SwingUtilities.invokeLater(() ->
        {
            new StatTabbedWindow(); //creates the main window
        });

    }

    public static Player[] getPlayers()
    {
        return players;
    }

    public static Player getPlayers(int i)
    {
        return players[i];
    }

    //sends uuid to Mojang API to obtain nickname. Returns "! player not found !" if player doesn't exist, and "! no response !" if request fails.
    private static String nickFromUUID(String rawUuid)
    {
        if(rawUuid == null || rawUuid.isBlank())
            return "! wrong UUID !";
        String uuid = rawUuid.replace("-", "");
        if (!uuid.matches("^[0-9a-fA-F]{32}$"))
            return "! wrong UUID !";


        String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(apiUrl)).GET().timeout(Duration.ofSeconds(5)).build();

            HttpResponse<String> response = CLIENT.send(request, BodyHandlers.ofString());

            int statusCode = response.statusCode();


            if(statusCode >= 500 || statusCode == 429)
                return "! no response !";

            if(statusCode == 404 || statusCode == 204)
                return "! player not found !";

            String responseBody = response.body();

            if(responseBody == null || responseBody.isBlank())
                return "! player not found !";

            JsonNode root = MAPPER.readTree(responseBody);
            JsonNode nameNode = root.get("name");

            if(nameNode == null || nameNode.isNull())
                return "! player not found !";

            return nameNode.asText();

        }
        catch (URISyntaxException e)
        {
            LOGGER.log(Level.SEVERE, "Niepoprawny URI", e);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Przerwano żądanie", e);
        } catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Błąd I/O", e);
        }

        return "! no response !";
    }

    private static File[] getFilesBySize(String dirPath) //gets all files from folder. Orders by descending file size so most active players are first.
    {
        File folder = new File(dirPath);
        File[] allFiles = folder.listFiles();
        if(allFiles == null || allFiles.length == 0)
        {
            JOptionPane.showMessageDialog(null,"Stat files not found!",null, 2);
            System.exit(1);
        }
        File temp;
        int j;
        for(int i = 1; i < allFiles.length; i++) //insertion sort
        {
            temp = allFiles[i];
            for(j = i; j > 0 && temp.length() > allFiles[j-1].length(); j--)
            {
                allFiles[j] = allFiles[j-1];
            }
            allFiles[j] = temp;
        }

        return allFiles;
    }

    private static String getJsonString(String fileName) throws FileNotFoundException, IOException //reads json file into string
    {
        BufferedReader br = new BufferedReader(new FileReader(directoryPath+"\\"+fileName));
        return br.readLine(); //the stat json file comes in a single line
    }

    public static String[] getServerwideStats(String category) //get all unique statistic entries that are on the server
    {
        Set<String> uniqueStats = new HashSet<>();

        for (Player player : players)
        {
            uniqueStats.addAll(player.getCategory(category).keySet()); //add every unique key
        }

        return uniqueStats.toArray(new String[0]); // Convert set to array and return
    }
}
