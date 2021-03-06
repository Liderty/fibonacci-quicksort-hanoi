package io.fqsh.hanoi;

import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TowerOfHanoiPanel {
    private JPanel hanoiPanel;
    private JPanel internalPanel;

    @Inject
    private TowerOfHanoiSettingsPanel hanoiSettingsPanel;

    @Inject
    private TowerOfHanoiChartsPanel hanoiChartsPanel;

    @Inject
    private TowerOfHanoiTablePanel hanoiTablePanel;

    @Inject
    private TowerOfHanoiConsolePanel hanoiConsolePanel;

    public JPanel build() {
        buildInternalPanel();

        hanoiPanel = new JPanel();
        hanoiPanel.setLayout(new BorderLayout(10, 10));
        hanoiPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        hanoiPanel.add(hanoiSettingsPanel.build(), BorderLayout.NORTH);
        hanoiPanel.add(internalPanel, BorderLayout.CENTER);
        hanoiPanel.add(hanoiConsolePanel.build(), BorderLayout.SOUTH);

        return hanoiPanel;
    }

    private void buildInternalPanel() {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BorderLayout(10, 10));
        internalPanel.add(hanoiChartsPanel.build(), BorderLayout.CENTER);
        internalPanel.add(hanoiTablePanel.build(), BorderLayout.SOUTH);
    }
}
