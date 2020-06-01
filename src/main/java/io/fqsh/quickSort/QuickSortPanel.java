package io.fqsh.quickSort;

import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuickSortPanel {
    private JPanel quickSortPanel;
    private JPanel internalPanel;

    @Inject
    private QuickSortSettingsPanel quickSortSettingsPanel;

    @Inject
    private QuickSortChartsPanel quickSortChartsPanel;

    @Inject
    private QuickSortTablePanel quickSortTablePanel;

    @Inject
    private QuickSortConsolePanel quickSortConsolePanel;

    public JPanel build() {
        buildInternalPanel();

        quickSortPanel = new JPanel();
        quickSortPanel.setLayout(new BorderLayout(10, 10));
        quickSortPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        quickSortPanel.add(quickSortSettingsPanel.build(), BorderLayout.NORTH);
        quickSortPanel.add(internalPanel, BorderLayout.CENTER);
        quickSortPanel.add(quickSortConsolePanel.build(), BorderLayout.SOUTH);

        return quickSortPanel;
    }

    private void buildInternalPanel() {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BorderLayout(10, 10));
        internalPanel.add(quickSortChartsPanel.build(), BorderLayout.CENTER);
        internalPanel.add(quickSortTablePanel.build(), BorderLayout.SOUTH);
    }
}
