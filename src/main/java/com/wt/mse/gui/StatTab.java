package com.wt.mse.gui;

import com.wt.mse.model.Player;
import com.wt.mse.services.StatPresentationService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class StatTab extends JPanel
{
    private String title;
    private JTable table;

    private List<Player> players;
    private StatPresentationService presentationService;

    public StatTab(String category, List<Player> players, StatPresentationService presentationService)
    {
        this.title = category;
        this.players = players;
        this.presentationService = presentationService;
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(UiStyles.TITLE_FONT);
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Statistic", "Server record", "Record holder", "Server total"};
        String[] statList = presentationService.getServerwideStats(category, players);
        String[][] data = presentationService.generateData(category, players, statList);

        table = new JTable(data, columnNames)
        {
            public boolean isCellEditable(int row, int column)
            {
                return column == 0; //button cells are editable to listen for clicks
            }
        };

        // Sorting for JTable
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        sorter.setComparator(1, presentationService.numericComparator()); //sort numeric columns numerically, not alphabetically
        sorter.setComparator(3, presentationService.numericComparator());

        table.setRowSorter(sorter);
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(3, SortOrder.DESCENDING)));


        table.setFont(UiStyles.DEFAULT_FONT);
        table.getTableHeader().setFont(UiStyles.DEFAULT_FONT);
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
            button.setFont(UiStyles.DEFAULT_FONT);
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
                button.setFont(UiStyles.DEFAULT_FONT);

                if(listener != null) //overwrites previous listener with new one
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

    private void openStatWindow(String category, String stat)
    {
        if(category.equals("custom"))
        {
            if(stat.contains("time_") || stat.contains("_time")) //time related stats have special window
            {
                new TimeStatWindow(category, stat, players, presentationService);
                return;
            }
            else if(stat.endsWith("_one_cm")) //distance related stats have special window
            {
                new DistanceStatWindow(category, stat, players, presentationService);
                return;
            }
        }

        new SingleStatWindow(category, stat, players, presentationService, false);
    }
}
