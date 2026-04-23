import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collections;
import javax.swing.table.*;
import javax.swing.table.DefaultTableCellRenderer;

public class StatTab extends JPanel
{

    private String title;
    private JTable table;


    public StatTab(String category, String[] statList)
    {
        this.title = category;
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(Main.TITLE_FONT);
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Statistic", "Server record", "Record holder", "Server total"};
        String[][] data = generateData(statList, columnNames);

        table = new JTable(data, columnNames)
        {
            public boolean isCellEditable(int row, int column)
            {
                return column == 0; //button cells are editable to listen for clicks
            }
        };

        // Sorting for JTable
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        sorter.setComparator(1, Main.numericComparator); //sort numeric columns numerically, not alphabetically
        sorter.setComparator(3, Main.numericComparator);

        table.setRowSorter(sorter);
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(3, SortOrder.DESCENDING)));


        table.setFont(Main.DEFAULT_FONT);
        table.getTableHeader().setFont(Main.DEFAULT_FONT);
        table.setRowHeight(30);
        // Align column 1 and column 3 o the right
        DefaultTableCellRenderer rightAlignRenderer = new DefaultTableCellRenderer();
        rightAlignRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightAlignRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlignRenderer);
        // Align column 2 to the center
        DefaultTableCellRenderer centerAlignRenderer = new DefaultTableCellRenderer();
        centerAlignRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerAlignRenderer);

        table.getColumnModel().getColumn(0).setCellRenderer((TableCellRenderer) (table1, value, isSelected, hasFocus, row, column) -> //rendered for adding JButtons
        {
            JButton button = new JButton(value.toString());
            button.setFont(Main.DEFAULT_FONT);
            return button;
        });

        table.getColumnModel().getColumn(0).setCellEditor(new TableCellEditor() //to use 0th column as buttons, it listens for edit and opens new window
        {
            private JButton button = new JButton();
            private String value;
            private ActionListener listener;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                this.value = value.toString(); //name of stat - to determine which stat to display if clicked
                button.setText(this.value);
                button.setFont(Main.DEFAULT_FONT);

                if (listener != null) //overwrites previous listener with new one
                {
                    button.removeActionListener(listener);
                }
                listener = e -> openStatWindow(title, this.value); //open statwindow when pressed
                button.addActionListener(listener);

                return button;
            }

            @Override
            public Object getCellEditorValue()
            {
                return value;
            }

            //methods needed for the TableCellEditor, handle button clicks
            @Override public boolean isCellEditable(java.util.EventObject anEvent) { return true; }
            @Override public boolean shouldSelectCell(java.util.EventObject anEvent) { return true; }
            @Override public boolean stopCellEditing() { return true; }
            @Override public void cancelCellEditing() {} //editing doesn't do anything, we only want onClick
            @Override public void addCellEditorListener(javax.swing.event.CellEditorListener l) {} //listens if cell is edited (clicked)
            @Override public void removeCellEditorListener(javax.swing.event.CellEditorListener l) {}
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private String[][] generateData(String[] statList, String[] names) //filling out the data array, getting data from Main.players[]
    {
        String[][] statData = new String[statList.length][names.length];
        for (int i = 0; i < statList.length; i++)
        {
            statData[i][0] = statList[i].replaceFirst("minecraft:",""); //remove the prefix for clearer display
            int max = 0, stat;
            long total = 0;
            String record = "";

            for (Player p : Main.getPlayers())
            {
                stat = p.getCategory(title).getOrDefault(statList[i], 0);

                total += stat;
                if (stat > max) //find maximum
                {
                    max = stat;
                    record = p.getNick();
                }
            }
            statData[i][1] = max + "  ";
            statData[i][2] = record;
            statData[i][3] = total + "  ";
        }
        return statData;
    }

    private void openStatWindow(String category, String stat)
    {
        if(category.equals("custom"))
        {
            if(stat.contains("time_") || stat.contains("_time")) //time related stats have special window
            {
                new TimeStatWindow(category, stat);
                return;
            }
            else if(stat.endsWith("_one_cm")) //distance related stats have special window
            {
                new DistanceStatWindow(category, stat);
                return;
            }

        }

        new SingleStatWindow(category, stat, false);

    }
}

