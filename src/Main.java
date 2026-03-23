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

public class Main
{
    private static String directoryPath = "stats"; //relative path
    private static Player[] players;

    public static Comparator<String> numericComparator;

    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);

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
            players[i] = new Player(tempUUID, nickFromUUID(tempUUID));
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

    private static String nickFromUUID(String UUID) //sends uuid to Mojang API to obtain nickname
    {
        String apiURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + UUID;
        try
        {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(apiURL)).GET().timeout(Duration.ofSeconds(10)).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            String responseBody = response.body();

            String[] respSplit = responseBody.split("\"");
            if(respSplit.length < 8)
                return "! name not found !"; //player doesn't exist (sometimes happens when using bots on server)
            return respSplit[7]; //8th field in the split response always is the nick

        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


        return "! no response !"; //if no response from API
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