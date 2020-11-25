package io.fqsh.fibonacci;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.fqsh.Application;
import io.fqsh.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Singleton
public class FibonacciSettingsPanel {
    private ExecutorService executorService;
    private final List<Future<?>> futures = new ArrayList<>();
    private JPanel fibonacciSettingsPanel;
    private JPanel fibonacciSamplesPanel;
    private JPanel fibonacciActionsPanel;
    private JButton fibonacciSettingsPanelCalculateButton;
    private JButton fibonacciSettingsPanelCancelButton;
    private final List<JComboBox<Integer>> fibonacciSettingsPanelComboBoxes = new ArrayList<>();
    private final List<Integer> samples = Arrays.asList(40, 42, 44, 46, 48);
    public static final int SAMPLE_MIN_VALUE = 1;
    public static final int SAMPLE_MAX_VALUE = 50;

    @Inject
    private Application application;

    @Inject
    private FibonacciChartsPanel fibonacciChartsPanel;

    @Inject
    private FibonacciTablePanel fibonacciTablePanel;

    @Inject
    private FibonacciConsolePanel fibonacciConsolePanel;

    public JPanel build() {
        buildSamplesPanel();
        buildActionsPanel();
        buildSettingsPanel();

        return fibonacciSettingsPanel;
    }

    private void buildSamplesPanel() {
        fibonacciSamplesPanel = new JPanel();
        fibonacciSamplesPanel.setLayout(new GridLayout(1, 5, 10, 10));
        fibonacciSamplesPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                fibonacciSamplesPanel.getBorder(),
                "Wyrazy ciągu Fibonacciego do obliczenia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        buildComboBoxes();
    }

    private void buildActionsPanel() {
        fibonacciActionsPanel = new JPanel();
        fibonacciActionsPanel.setLayout(new GridLayout(1, 2, 10, 10));
        fibonacciActionsPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                fibonacciActionsPanel.getBorder(),
                "Obliczenia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        buildCalculateButton();
        buildCancelButton();
    }

    private void buildSettingsPanel() {
        fibonacciSettingsPanel = new JPanel();
        fibonacciSettingsPanel.setLayout(new BorderLayout(10, 10));
        fibonacciSettingsPanel.add(fibonacciSamplesPanel, BorderLayout.CENTER);
        fibonacciSettingsPanel.add(fibonacciActionsPanel, BorderLayout.EAST);
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, SAMPLE_MIN_VALUE, SAMPLE_MAX_VALUE);

            fibonacciSettingsPanelComboBoxes.add(comboBox);
            fibonacciSamplesPanel.add(comboBox);
        });
    }

    private JComboBox<Integer> createRangeComboBox(int index, int min, int max) {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        JComboBox<Integer> comboBox = new JComboBox<>(
            IntStream.rangeClosed(min, max).boxed().toArray(Integer[]::new)
        );
        comboBox.setRenderer(renderer);
        comboBox.setSelectedItem(samples.get(index));
        comboBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                Integer previousValue = samples.get(index);

                Integer value = (Integer) itemEvent.getItem();
                samples.set(index, value);

                if (!Utils.areSamplesValuesDistinct(samples)) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Wybrane wartości nie powinny się powtarzać!",
                        "Komunikat",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    samples.set(index, previousValue);
                    fibonacciSettingsPanelComboBoxes.get(index).setSelectedItem(previousValue);

                    return;
                }

                if (!Utils.areSamplesInAscendingOrder(samples)) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Wybrane wartości powinny występować w porządku rosnącym!",
                        "Komunikat",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    samples.set(index, previousValue);
                    fibonacciSettingsPanelComboBoxes.get(index).setSelectedItem(previousValue);

                    return;
                }

                clearDataAfterActionIfValuesAreComputed();

                fibonacciChartsPanel.updateIterationChart();
                fibonacciChartsPanel.updateRecursiveChart();

                fibonacciTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        fibonacciSettingsPanelCalculateButton = new JButton("Rozpocznij");
        fibonacciSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            blockCalculateButton();
            clearDataAfterActionIfValuesAreComputed();
            calculate();
        });
        fibonacciActionsPanel.add(fibonacciSettingsPanelCalculateButton);
    }

    private void buildCancelButton() {
        fibonacciSettingsPanelCancelButton = new JButton("Zakończ");
        fibonacciSettingsPanelCancelButton.setEnabled(false);
        fibonacciSettingsPanelCancelButton.addActionListener(actionEvent -> {
            blockCancelButton();
            futures.forEach(future -> future.cancel(true));
            executorService.execute(() -> fibonacciTablePanel.setCellsToCalculatingCancelState());
            executorService.execute(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Obliczenia zostały przerwane.",
                    "Komunikat",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        });
        fibonacciActionsPanel.add(fibonacciSettingsPanelCancelButton);
    }

    private void calculate() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> blockUI());
        executorService.execute(() -> unblockCancelButton());
        executorService.execute(() -> fibonacciConsolePanel.write("Rozpoczęto obliczenia."));
        IntStream.rangeClosed(0, 4).forEach(index -> {
            futures.add(executorService.submit(() -> fibonacciIterationExecutor(samples.get(index))));
            futures.add(executorService.submit(() -> fibonacciRecursiveExecutor(samples.get(index))));
        });
        executorService.execute(() -> fibonacciConsolePanel.write("Zakończono obliczenia."));
        executorService.execute(() -> blockCancelButton());
        executorService.execute(() -> unblockUI());
    }

    private void fibonacciIterationExecutor(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = FibonacciIteration.calculate(n);
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

    private void fibonacciRecursiveExecutor(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = FibonacciRecursive.calculate(n);
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

    private void clearDataAfterActionIfValuesAreComputed() {
        if (fibonacciChartsPanel.getIterationTimes().stream().anyMatch(value -> !value.equals(0L))) {
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

    private void blockCancelButton() {
        fibonacciSettingsPanelCancelButton.setEnabled(false);
    }

    private void unblockCancelButton() {
        fibonacciSettingsPanelCancelButton.setEnabled(true);
    }

    public List<Integer> getSamples() {
        return samples;
    }
}
