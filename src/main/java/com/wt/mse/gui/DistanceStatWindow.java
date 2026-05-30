package com.wt.mse.gui;

import java.util.Comparator;
import com.wt.mse.model.Player;
import com.wt.mse.services.StatPresentationService;

import java.util.List;

public class DistanceStatWindow extends SingleStatWindow //window to format distance (as it's normally in cm)
{
    private Comparator<String> distanceComparator;
    public DistanceStatWindow(String category, String stat, List<Player> players, StatPresentationService presentationService)
    {
        super(category, stat, players, presentationService, true); //setup window according to superclass

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
        totalLabel.setText("Server Total:" + presentationService.formatDistance(serverTotal));
    }
}
