package io.fqsh.fibonacci;

import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FibonacciPanel {
    private JPanel fibonacciPanel;

    @Inject
    private FibonacciSettingsPanel fibonacciSettingsPanel;

    public JPanel build() {
        fibonacciPanel = new JPanel();
        fibonacciPanel.setLayout(new BorderLayout(10, 10));
        fibonacciPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        fibonacciPanel.add(fibonacciSettingsPanel.build(), BorderLayout.NORTH);

        return fibonacciPanel;
    }
}
