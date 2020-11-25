package io.fqsh.fibonacci;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.stream.IntStream;

@Singleton
public class FibonacciTablePanel {
    private JTable table;
    private DefaultTableModel model;

    @Inject
    private FibonacciSettingsPanel fibonacciSettingsPanel;

    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);

            return this;
        }
    };

    public JPanel build() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));
        panel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                panel.getBorder(),
                "Tabela czasów obliczania w zależności od podejścia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.add(buildTable());

        return panel;
    }

    private JScrollPane buildTable() {
        String[] columns = new String[] {
            "Wyraz ciągu Fibonacciego", "Czas obliczania - iteracyjnie", "Czas obliczania - rekurencyjnie"
        };

        Object[][] rows = new Object[5][3];

        IntStream.rangeClosed(0, 4).forEach(index -> {
            rows[index][0] = fibonacciSettingsPanel.getSamples().get(index);
            rows[index][1] = "Brak danych";
            rows[index][2] = "Brak danych";
        });

        model = new DefaultTableModel(rows, columns);

        table = new JTable(model);
        table.setEnabled(false);
        table.setDefaultRenderer(Object.class, renderer);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    public void setCellValueAt(int x, int y, Object value) {
        model.setValueAt(value, x, y);
    }

    public void setCellsToNoDataState() {
        setValueToCells("Brak danych");
    }

    public void setCellsToCalculatingState() {
        setValueToCells("Obliczanie...");
    }

    public void setCellsToCalculatingCancelState() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            if (model.getValueAt(index, 1).equals("Obliczanie...")) {
                model.setValueAt("0 ns", index, 1);
            }

            if (model.getValueAt(index, 2).equals("Obliczanie...")) {
                model.setValueAt("0 ns", index, 2);
            }
        });
    }

    private void setValueToCells(String message) {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            model.setValueAt(message, index, 1);
            model.setValueAt(message, index, 2);
        });
    }
}
