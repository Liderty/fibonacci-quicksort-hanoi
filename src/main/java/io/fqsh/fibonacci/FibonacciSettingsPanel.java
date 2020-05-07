package io.fqsh.fibonacci;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.fqsh.Application;
import io.fqsh.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

@Singleton
public class FibonacciSettingsPanel {
    private JPanel fibonacciSettingsPanel;
    private final List<JComboBox<Integer>> fibonacciSettingsPanelComboBoxes = new ArrayList<>();
    private JButton fibonacciSettingsPanelCalculateButton;
    private final List<Integer> samples = Arrays.asList(40, 42, 44, 46, 48);

    @Inject
    private Application application;

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
            calculate();
        });
        fibonacciSettingsPanel.add(fibonacciSettingsPanelCalculateButton);
    }

    private void calculate() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(this::blockUI);
        executor.execute(() -> fibonacciConsolePanel.write("Rozpoczęto obliczenia."));

        IntStream.rangeClosed(0, 4).forEach(index -> {
            executor.execute(() -> fibonacciIterationWrapper(samples.get(index)));
            executor.execute(() -> fibonacciRecursiveWrapper(samples.get(index)));
        });

        executor.execute(() -> fibonacciConsolePanel.write("Zakończono obliczenia."));
        executor.execute(this::unblockUI);
    }

    private void fibonacciIterationWrapper(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = fibonacciIteration(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        fibonacciChartsPanel.setIterationTimeAt(index, timeElapsed);
        fibonacciChartsPanel.updateIterationChart();
        fibonacciTablePanel.setCellValueAt(index, 1, Utils.convertTime(timeElapsed));

        fibonacciConsolePanel.write(String.format(
            "Wyraz %d. ciągu Fibonacciego o wartości: %,d został obliczony iteracyjnie w czasie: %s. ",
            n,
            result,
            Utils.convertTime(timeElapsed)
        ));
    }

    private Long fibonacciIteration(long n) {
        if (n == 0 || n == 1) return n;

        long a = 1;
        long b = 1;
        long temporary;

        for (int i = 0; i < (n - 2); i++) {
            temporary = a;
            a = b;
            b = temporary;
            b += a;
        }

        return b;
    }

    private void fibonacciRecursiveWrapper(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = fibonacciRecursive(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        fibonacciChartsPanel.setRecursiveTimeAt(index, timeElapsed);
        fibonacciChartsPanel.updateRecursiveChart();
        fibonacciTablePanel.setCellValueAt(index, 2, Utils.convertTime(timeElapsed));

        fibonacciConsolePanel.write(String.format(
            "Wyraz %d. ciągu Fibonacciego o wartości: %,d został obliczony rekurencyjnie w czasie: %s.",
            n,
            result,
            Utils.convertTime(timeElapsed)
        ));
    }

    private long fibonacciRecursive(long n) {
        if (n == 0 || n == 1) return n;

        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
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

    private void blockUI() {
        application.blockTabbedPane();
        blockAllComboBoxes();
        blockCalculateButton();
        fibonacciTablePanel.setCellsToCalculatingState();
        fibonacciConsolePanel.clearConsole();
    }

    private void unblockUI() {
        application.unblockTabbedPane();
        unblockAllComboBoxes();
        unblockCalculateButton();
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
