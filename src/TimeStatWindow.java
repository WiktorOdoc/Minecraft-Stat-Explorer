import java.util.Comparator;

public class TimeStatWindow extends SingleStatWindow //window to format time (as it's normally in ticks)
{
    private Comparator<String> timeComparator;
    public TimeStatWindow(String category, String stat)
    {
        super(category, stat, true); //setup window according to superclass

        timeComparator = (String s1, String s2) -> //comparator for formatted time
        {
            try
            {
                String[] nums1 = s1.split(" ");
                String[] nums2 = s2.split(" ");
                int num1 = Integer.parseInt(nums1[2])*3600 + Integer.parseInt(nums1[4])*60 + Integer.parseInt(nums1[6]); //get number of seconds
                int num2 = Integer.parseInt(nums2[2])*3600 + Integer.parseInt(nums2[4])*60 + Integer.parseInt(nums2[6]);
                return Integer.compare(num1, num2);  // Compare numerically
            }
            catch (NumberFormatException e)
            {
                return 0;  //in case of non-numeric values, treat them as equal
            }
        };

        sorter.setComparator(2, timeComparator);
        totalLabel.setText("Server Total:" + timeToString(serverTotal)); //format total label
    }

    @Override
    protected String[][] getPlayerData()
    {
        String[][] statData = super.getPlayerData(); //load 0th and 1st column (2nd column is empty)
        for(int i = 0; i < Main.getPlayers().length; i++)
        {
            statData[i][2] = timeToString(Long.parseLong(statData[i][1].trim())); //add 2nd, formatted column
        }
        return statData;
    }

    private String timeToString(long time) //format time in x hr, y min, z sec
    {
        time /= 20;          //total time in seconds, tick is 0.05s
        long seconds = time % 60;
        long minutes = (time/60)%60;
        long hours = (time/3600);
        return "  " + hours + " hr, " + minutes + " min, " + seconds + " sec";
    }
}

