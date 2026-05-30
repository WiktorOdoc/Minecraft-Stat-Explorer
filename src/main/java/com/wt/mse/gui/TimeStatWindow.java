package com.wt.mse.gui;

import java.util.Comparator;
import com.wt.mse.model.Player;
import com.wt.mse.services.StatPresentationService;

import java.util.List;

public class TimeStatWindow extends SingleStatWindow //window to format time (as it's normally in ticks)
{
    private Comparator<String> timeComparator;
    public TimeStatWindow(String category, String stat, List<Player> players, StatPresentationService presentationService)
    {
        super(category, stat, players, presentationService, true); //setup window according to superclass

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
        totalLabel.setText("Server Total:" + presentationService.formatTime(serverTotal)); //format total label
    }
}
