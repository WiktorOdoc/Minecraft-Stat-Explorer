import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

class SingleStatWindow extends JFrame
{

    protected String[][] data;
    protected String[] columnNames;
    protected JTable table;
    protected TableRowSorter<TableModel> sorter;
    protected JLabel totalLabel;
    protected long serverTotal;
    protected String category;
    protected String stat;



    public SingleStatWindow(String category, String stat, boolean formatted)
    {
        this.category = category;
        this.stat = stat;
        setTitle(category + ": " + stat);
        setSize(500, 1000);
        if(formatted) // formatted = has 3rd column with formatted data (child class object)
            setSize(600, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel label = new JLabel(category + ": " + stat, SwingConstants.CENTER); // title label
        label.setFont(Main.TITLE_FONT);
        add(label, BorderLayout.NORTH);

        if(formatted) // Initialize JTable and set up default column names
            columnNames = new String[]{"Player", "Value", "Formatted Value"};
        else
            columnNames = new String[]{"Player", "Value"};

        data = getPlayerData(); // Get data based on category/stat

        table = new JTable(data, columnNames) //cells are not editable
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        sorter = new TableRowSorter<>(table.getModel()); // initialize sorter for columns
        sorter.setComparator(1, Main.numericComparator);
        table.setRowSorter(sorter);
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(1, SortOrder.DESCENDING))); //sort descending

        table.setFont(Main.DEFAULT_FONT);
        table.getTableHeader().setFont(Main.DEFAULT_FONT);
        table.setRowHeight(30);
        //Align column 1 to right
        DefaultTableCellRenderer rightAlignRenderer = new DefaultTableCellRenderer();
        rightAlignRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlignRenderer);
        //Align column 0 to the center
        DefaultTableCellRenderer centerAlignRenderer = new DefaultTableCellRenderer();
        centerAlignRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlignRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Server Total: " + serverTotal, SwingConstants.CENTER); //label for displaying server total
        totalLabel.setFont(Main.DEFAULT_FONT);
        add(totalLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Method to get data for the table - can be overridden by subclasses
    protected String[][] getPlayerData()
    {
        String[][] statData = new String[Main.getPlayers().length][columnNames.length]; //  Player | stat value | formatted value (only in child classes)
        int tempStat;
        for(int i = 0; i < Main.getPlayers().length; i++)
        {
            Player p = Main.getPlayers(i);
            statData[i][0] = p.getNick();                        //add the prefix removed in StatTab
            tempStat = p.getCategory(category).getOrDefault("minecraft:" + stat, 0);
            serverTotal += tempStat;
            statData[i][1] = tempStat + "  ";
        }
        return statData;
    }

    // Method to get comparator for the "Value" column - can be overridden

}