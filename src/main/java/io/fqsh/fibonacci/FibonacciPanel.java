package io.fqsh.fibonacci;

import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FibonacciPanel {
    private JPanel fibonacciPanel;

    @Inject
    private FibonacciSettingsPanel fibonacciSettingsPanel;

    @Inject
    private FibonacciChartsPanel fibonacciChartsPanel;

    @Inject
    private FibonacciTablePanel fibonacciTablePanel;

    public JPanel build() {
        fibonacciPanel = new JPanel();
        fibonacciPanel.setLayout(new BorderLayout(10, 10));
        fibonacciPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        fibonacciPanel.add(fibonacciSettingsPanel.build(), BorderLayout.NORTH);
        fibonacciPanel.add(buildInternalPanel(), BorderLayout.CENTER);

        return fibonacciPanel;
    }

    private JPanel buildInternalPanel() {
        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new BorderLayout(10, 10));
        internalPanel.add(fibonacciChartsPanel.build(), BorderLayout.CENTER);
        internalPanel.add(fibonacciTablePanel.build(), BorderLayout.SOUTH);

        return internalPanel;
    }
}
