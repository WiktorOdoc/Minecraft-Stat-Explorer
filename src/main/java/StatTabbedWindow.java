import javax.swing.*;

public class StatTabbedWindow //main window
{
    private JFrame frame = new JFrame("Statistic explorer");
    private String[] tabNames = {"custom", "mined", "broken", "dropped", "used", "crafted", "picked_up", "killed", "killed_by"}; //stat categories

    private JTabbedPane tabbedPane;
    public StatTabbedWindow()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //open maximized
        frame.setVisible(true);
        tabbedPane = new JTabbedPane();
        for (String tabName : tabNames) //add tabs for categories
        {
            tabbedPane.addTab(tabName, new StatTab(tabName, Main.getServerwideStats(tabName)));
        }

        frame.add(tabbedPane);
    }
}

