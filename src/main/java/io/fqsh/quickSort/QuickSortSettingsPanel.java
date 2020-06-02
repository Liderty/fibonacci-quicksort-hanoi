package io.fqsh.quickSort;

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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Singleton
public class QuickSortSettingsPanel {
    private ExecutorService executorService;
    private final List<Future<?>> futures = new ArrayList<>();
    private JPanel quickSortSettingsPanel;
    private JPanel quickSortSamplesPanel;
    private JPanel quickSortActionsPanel;
    private JButton quickSortSettingsPanelCalculateButton;
    private JButton quickSortSettingsPanelCancelButton;
    private final List<JComboBox<Integer>> quickSortSettingsPanelComboBoxes = new ArrayList<>();
    private final List<Integer> samples = Arrays.asList(50, 60, 70, 80, 90);
    public static final int SAMPLE_MIN_VALUE = 5;
    public static final int SAMPLE_MAX_VALUE = 100;

    @Inject
    private Application application;

    @Inject
    private QuickSortChartsPanel quickSortChartsPanel;

    @Inject
    private QuickSortTablePanel quickSortTablePanel;

    @Inject
    private QuickSortConsolePanel quickSortConsolePanel;

    public JPanel build() {
        buildSamplesPanel();
        buildActionsPanel();
        buildSettingsPanel();

        return quickSortSettingsPanel;
    }

    private void buildSamplesPanel() {
        quickSortSamplesPanel = new JPanel();
        quickSortSamplesPanel.setLayout(new GridLayout(1, 5, 10, 10));
        quickSortSamplesPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                quickSortSamplesPanel.getBorder(),
                "Liczba elementów do posortowania (w tys.)",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        buildComboBoxes();
    }

    private void buildActionsPanel() {
        quickSortActionsPanel = new JPanel();
        quickSortActionsPanel.setLayout(new GridLayout(1, 2, 10, 10));
        quickSortActionsPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                quickSortActionsPanel.getBorder(),
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
        quickSortSettingsPanel = new JPanel();
        quickSortSettingsPanel.setLayout(new BorderLayout(10, 10));
        quickSortSettingsPanel.add(quickSortSamplesPanel, BorderLayout.CENTER);
        quickSortSettingsPanel.add(quickSortActionsPanel, BorderLayout.EAST);
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, SAMPLE_MIN_VALUE, SAMPLE_MAX_VALUE);

            quickSortSettingsPanelComboBoxes.add(comboBox);
            quickSortSamplesPanel.add(comboBox);
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
                clearDataAfterActionIfValuesAreComputed();

                Integer value = (Integer) itemEvent.getItem();
                samples.set(index, value);

                quickSortChartsPanel.updateIterationChart();
                quickSortChartsPanel.updateRecursiveChart();

                quickSortTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        quickSortSettingsPanelCalculateButton = new JButton("Rozpocznij");
        quickSortSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            if (samples.stream().distinct().count() < 5) {
                JOptionPane.showMessageDialog(null, "Wybrane wartości nie powinny się powtarzać!");

                return;
            }

            blockCalculateButton();
            clearDataAfterActionIfValuesAreComputed();
            calculate();
        });
        quickSortActionsPanel.add(quickSortSettingsPanelCalculateButton);
    }

    private void buildCancelButton() {
        quickSortSettingsPanelCancelButton = new JButton("Zakończ");
        quickSortSettingsPanelCancelButton.setEnabled(false);
        quickSortSettingsPanelCancelButton.addActionListener(actionEvent -> {
            blockCancelButton();
            futures.forEach(future -> future.cancel(true));
            executorService.execute(() -> quickSortTablePanel.setCellsToCalculatingCancelState());
            executorService.execute(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Obliczenia zostały przerwane.",
                    "Komunikat",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        });
        quickSortActionsPanel.add(quickSortSettingsPanelCancelButton);
    }

    private void calculate() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> blockUI());
        executorService.execute(() -> unblockCancelButton());
        executorService.execute(() -> quickSortConsolePanel.write("Rozpoczęto obliczenia."));
        IntStream.rangeClosed(0, 4).forEach(index -> {
            int elementsInThousands = samples.get(index);
            int[] unsortedDataForIteration = simplyDataGenerator(elementsInThousands);
            int[] unsortedDataForRecursion = unsortedDataForIteration.clone();

            futures.add(
                executorService.submit(() -> quickSortIterationExecutor(elementsInThousands, unsortedDataForIteration))
            );

            futures.add(
                executorService.submit(() -> quickSortRecursiveExecutor(elementsInThousands, unsortedDataForRecursion))
            );
        });
        executorService.execute(() -> quickSortConsolePanel.write("Zakończono obliczenia."));
        executorService.execute(() -> blockCancelButton());
        executorService.execute(() -> unblockUI());
    }

    private int[] simplyDataGenerator(int elementsInThousands) {
        return (new Random()).ints(elementsInThousands * 1000, 0, 100_000).toArray();
    }

    private void quickSortIterationExecutor(int n, int[] unsortedData) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        QuickSortIteration.calculate(unsortedData, 0, unsortedData.length - 1);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        quickSortChartsPanel.setIterationTimeAt(index, timeElapsed);
        quickSortChartsPanel.updateIterationChart();
        quickSortTablePanel.setCellValueAt(index, 1, Utils.convertTime(timeElapsed));

        quickSortConsolePanel.write(String.format(
            "Losowe elementy o rozmiarze: %,d tys. zostały posortowane iteracyjnie w czasie: %s.",
            n,
            Utils.convertTime(timeElapsed)
        ));
    }

    private void quickSortRecursiveExecutor(int n, int[] unsortedData) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        QuickSortRecursive.calculate(unsortedData, 0, unsortedData.length - 1);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        quickSortChartsPanel.setRecursiveTimeAt(index, timeElapsed);
        quickSortChartsPanel.updateRecursiveChart();
        quickSortTablePanel.setCellValueAt(index, 2, Utils.convertTime(timeElapsed));

        quickSortConsolePanel.write(String.format(
            "Losowe elementy o rozmiarze: %,d tys. zostały posortowane rekurencyjnie w czasie: %s.",
            n,
            Utils.convertTime(timeElapsed)
        ));
    }

    private void clearDataAfterActionIfValuesAreComputed() {
        if (quickSortChartsPanel.getIterationTimes().stream().anyMatch(value -> !value.equals(0L))) {
            quickSortChartsPanel.resetData();
            quickSortChartsPanel.updateIterationChart();
            quickSortChartsPanel.updateRecursiveChart();
            quickSortTablePanel.setCellsToNoDataState();
            quickSortConsolePanel.clearConsole();
        }
    }

    private void blockUI() {
        application.blockTabbedPane();
        blockAllComboBoxes();
        quickSortTablePanel.setCellsToCalculatingState();
        quickSortConsolePanel.clearConsole();
    }

    private void unblockUI() {
        application.unblockTabbedPane();
        unblockAllComboBoxes();
        unblockCalculateButton();
    }

    private void blockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            quickSortSettingsPanelComboBoxes.get(index).setEnabled(false);
        });
    }

    private void unblockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            quickSortSettingsPanelComboBoxes.get(index).setEnabled(true);
        });
    }

    private void blockCalculateButton() {
        quickSortSettingsPanelCalculateButton.setEnabled(false);
    }

    private void unblockCalculateButton() {
        quickSortSettingsPanelCalculateButton.setEnabled(true);
    }

    private void blockCancelButton() {
        quickSortSettingsPanelCancelButton.setEnabled(false);
    }

    private void unblockCancelButton() {
        quickSortSettingsPanelCancelButton.setEnabled(true);
    }

    public List<Integer> getSamples() {
        return samples;
    }
}
