import java.util.Comparator;

public class DistanceStatWindow extends SingleStatWindow //window to format distance (as it's normally in cm)
{
    private Comparator<String> distanceComparator;
    public DistanceStatWindow(String category, String stat)
    {
        super(category, stat, true); //setup window according to superclass

        distanceComparator = (String s1, String s2) -> //comparator for formatted distance
        {
            try
            {
                String[] nums1 = s1.split(" ");
                String[] nums2 = s2.split(" ");
                int num1 = Integer.parseInt(nums1[2])*1000 + Integer.parseInt(nums1[4])*100 + Integer.parseInt(nums1[6]); //get cm number
                int num2 = Integer.parseInt(nums2[2])*1000 + Integer.parseInt(nums2[4])*100 + Integer.parseInt(nums2[6]);
                return Integer.compare(num1, num2);  // Compare numerically
            }
            catch (NumberFormatException e)
            {
                return 0;  //in case of non-numeric values, treat them as equal
            }
        };

        sorter.setComparator(2, distanceComparator);
        totalLabel.setText("Server Total:" + distanceToString(serverTotal));
    }

    @Override
    protected String[][] getPlayerData()
    {
        String[][] statData = super.getPlayerData(); //load 0th and 1st column (2nd column is empty)
        for(int i = 0; i < Main.getPlayers().length; i++)
        {
            statData[i][2] = distanceToString(Long.parseLong(statData[i][1].trim())); //add 2nd, formatted column
        }
        return statData;
    }
    private String distanceToString(long distance) //distance is in cm
    {
        long cm = distance % 100;
        long m = (distance/100)%1000;
        long km = (distance/100000);
        return "  " + km + " km, " + m + " m, " + cm + " cm";
    }
}
