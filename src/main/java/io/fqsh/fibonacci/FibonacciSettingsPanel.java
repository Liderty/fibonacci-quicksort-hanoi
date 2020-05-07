package io.fqsh.fibonacci;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Singleton
public class FibonacciSettingsPanel {
    private JPanel fibonacciSettingsPanel;
    private final List<JComboBox<Integer>> fibonacciSettingsPanelComboBoxes = new ArrayList<>();
    private JButton fibonacciSettingsPanelCalculateButton;
    private final List<Integer> samples = Arrays.asList(40, 42, 44, 46, 48);

    @Inject
    private FibonacciChartsPanel fibonacciChartsPanel;

    @Inject
    private FibonacciTablePanel fibonacciTablePanel;

    @Inject
    private FibonacciConsolePanel fibonacciConsolePanel;

    public JPanel build() {
        buildSettingsPanel();
        buildComboBoxes();
        buildCalculateButton();

        return fibonacciSettingsPanel;
    }

    private void buildSettingsPanel() {
        fibonacciSettingsPanel = new JPanel();
        fibonacciSettingsPanel.setLayout(new GridLayout(1, 6, 10, 10));
        fibonacciSettingsPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                fibonacciSettingsPanel.getBorder(),
                "Wyrazy ciągu Fibonacciego do obliczenia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, 1, 50);

            fibonacciSettingsPanelComboBoxes.add(comboBox);
            fibonacciSettingsPanel.add(comboBox);
        });
    }

    private JComboBox<Integer> createRangeComboBox(int index, int min, int max) {
        JComboBox<Integer> comboBox = new JComboBox<>(
            IntStream.rangeClosed(min, max).boxed().toArray(Integer[]::new)
        );
        comboBox.setSelectedItem(samples.get(index));
        comboBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                clearDataAfterActionIfValuesAreComputed();

                Integer value = (Integer) itemEvent.getItem();
                samples.set(index, value);
                fibonacciChartsPanel.updateIterationChart();
                fibonacciChartsPanel.updateRecursiveChart();
                fibonacciTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        fibonacciSettingsPanelCalculateButton = new JButton("Oblicz");
        fibonacciSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            if (samples.stream().distinct().count() < 5) {
                JOptionPane.showMessageDialog(null, "Każdy wybrany wyraz powinien wystąpić tylko raz!");

                return;
            }

            clearDataAfterActionIfValuesAreComputed();

            JOptionPane.showMessageDialog(null, "Run!");
        });
        fibonacciSettingsPanel.add(fibonacciSettingsPanelCalculateButton);
    }

    private void clearDataAfterActionIfValuesAreComputed() {

        if (fibonacciChartsPanel.getIterationTimes().stream().noneMatch(value -> value.equals(0L))) {
            fibonacciChartsPanel.resetData();
            fibonacciChartsPanel.updateIterationChart();
            fibonacciChartsPanel.updateRecursiveChart();
            fibonacciTablePanel.setCellsToNoDataState();
            fibonacciConsolePanel.clearConsole();
        }
    }

    private void blockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            fibonacciSettingsPanelComboBoxes.get(index).setEnabled(false);
        });
    }

    private void unblockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            fibonacciSettingsPanelComboBoxes.get(index).setEnabled(true);
        });
    }

    private void blockCalculateButton() {
        fibonacciSettingsPanelCalculateButton.setEnabled(false);
    }

    private void unblockCalculateButton() {
        fibonacciSettingsPanelCalculateButton.setEnabled(true);
    }

    public List<Integer> getSamples() {
        return samples;
    }
}
