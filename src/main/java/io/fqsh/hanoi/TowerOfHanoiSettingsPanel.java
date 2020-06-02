package io.fqsh.hanoi;

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
public class TowerOfHanoiSettingsPanel {
    private ExecutorService executorService;
    private final List<Future<?>> futures = new ArrayList<>();
    private JPanel hanoiSettingsPanel;
    private JPanel hanoiSamplesPanel;
    private JPanel hanoiActionsPanel;
    private JButton hanoiSettingsPanelCalculateButton;
    private JButton hanoiSettingsPanelCancelButton;
    private final List<JComboBox<Integer>> hanoiSettingsPanelComboBoxes = new ArrayList<>();
    private final List<Integer> samples = Arrays.asList(20, 21, 22, 23, 24);
    public static final int SAMPLE_MIN_VALUE = 1;
    public static final int SAMPLE_MAX_VALUE = 25;

    @Inject
    private Application application;

    @Inject
    private TowerOfHanoiChartsPanel hanoiChartsPanel;

    @Inject
    private TowerOfHanoiTablePanel hanoiTablePanel;

    @Inject
    private TowerOfHanoiConsolePanel hanoiConsolePanel;

    public JPanel build() {
        buildSamplesPanel();
        buildActionsPanel();
        buildSettingsPanel();

        return hanoiSettingsPanel;
    }

    private void buildSamplesPanel() {
        hanoiSamplesPanel = new JPanel();
        hanoiSamplesPanel.setLayout(new GridLayout(1, 5, 10, 10));
        hanoiSamplesPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                hanoiSamplesPanel.getBorder(),
                "Liczba krążków do przełożenia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        buildComboBoxes();
    }

    private void buildActionsPanel() {
        hanoiActionsPanel = new JPanel();
        hanoiActionsPanel.setLayout(new GridLayout(1, 2, 10, 10));
        hanoiActionsPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                hanoiActionsPanel.getBorder(),
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
        hanoiSettingsPanel = new JPanel();
        hanoiSettingsPanel.setLayout(new BorderLayout(10, 10));
        hanoiSettingsPanel.add(hanoiSamplesPanel, BorderLayout.CENTER);
        hanoiSettingsPanel.add(hanoiActionsPanel, BorderLayout.EAST);
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, SAMPLE_MIN_VALUE, SAMPLE_MAX_VALUE);

            hanoiSettingsPanelComboBoxes.add(comboBox);
            hanoiSamplesPanel.add(comboBox);
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

                hanoiChartsPanel.updateIterationChart();
                hanoiChartsPanel.updateRecursiveChart();

                hanoiTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        hanoiSettingsPanelCalculateButton = new JButton("Rozpocznij");
        hanoiSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            if (samples.stream().distinct().count() < 5) {
                JOptionPane.showMessageDialog(null, "Wybrane wartości nie powinny się powtarzać!");

                return;
            }

            blockCalculateButton();
            clearDataAfterActionIfValuesAreComputed();
            calculate();
        });
        hanoiActionsPanel.add(hanoiSettingsPanelCalculateButton);
    }

    private void buildCancelButton() {
        hanoiSettingsPanelCancelButton = new JButton("Zakończ");
        hanoiSettingsPanelCancelButton.setEnabled(false);
        hanoiSettingsPanelCancelButton.addActionListener(actionEvent -> {
            blockCancelButton();
            futures.forEach(future -> future.cancel(true));
            executorService.execute(() -> hanoiTablePanel.setCellsToCalculatingCancelState());
            executorService.execute(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Obliczenia zostały przerwane.",
                    "Komunikat",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        });
        hanoiActionsPanel.add(hanoiSettingsPanelCancelButton);
    }

    private void calculate() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> blockUI());
        executorService.execute(() -> unblockCancelButton());
        executorService.execute(() -> hanoiConsolePanel.write("Rozpoczęto obliczenia."));
        IntStream.rangeClosed(0, 4).forEach(index -> {
            futures.add(executorService.submit(() -> hanoiIterationExecutor(samples.get(index))));
            futures.add(executorService.submit(() -> hanoiRecursiveExecutor(samples.get(index))));
        });
        executorService.execute(() -> hanoiConsolePanel.write("Zakończono obliczenia."));
        executorService.execute(() -> blockCancelButton());
        executorService.execute(() -> unblockUI());
    }

    private void hanoiIterationExecutor(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = hanoiIteration(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        hanoiChartsPanel.setIterationTimeAt(index, timeElapsed);
        hanoiChartsPanel.updateIterationChart();
        hanoiTablePanel.setCellValueAt(index, 1, Utils.convertTime(timeElapsed));

        hanoiConsolePanel.write(String.format(
            "Wieża Hanoi złożona z %d krążków została ułożona iteracyjnie w czasie: %s (%,d ruchów).",
            n,
            Utils.convertTime(timeElapsed),
            result
        ));
    }

    private long hanoiIteration(int disksNumber) {
        IterativeHanoiTower towerIterative = new IterativeHanoiTower(disksNumber);
        towerIterative.solveIterativeHanoiTower();

        return towerIterative.getNumberOfMoves();
    }

    private void hanoiRecursiveExecutor(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = hanoiRecursive(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        hanoiChartsPanel.setRecursiveTimeAt(index, timeElapsed);
        hanoiChartsPanel.updateRecursiveChart();
        hanoiTablePanel.setCellValueAt(index, 2, Utils.convertTime(timeElapsed));

        hanoiConsolePanel.write(String.format(
            "Wieża Hanoi złożona z %d krążków została ułożona rekurencyjnie w czasie: %s (%,d ruchów).",
            n,
            Utils.convertTime(timeElapsed),
            result
        ));
    }

    private long hanoiRecursive(int disksNumber) {
        RecursiveHanoiTower towerRecursive = new RecursiveHanoiTower(disksNumber);
        towerRecursive.solveRecursiveHanoiTower();

        return towerRecursive.getNumberOfMoves();
    }

    private void clearDataAfterActionIfValuesAreComputed() {
        if (hanoiChartsPanel.getIterationTimes().stream().anyMatch(value -> !value.equals(0L))) {
            hanoiChartsPanel.resetData();
            hanoiChartsPanel.updateIterationChart();
            hanoiChartsPanel.updateRecursiveChart();
            hanoiTablePanel.setCellsToNoDataState();
            hanoiConsolePanel.clearConsole();
        }
    }

    private void blockUI() {
        application.blockTabbedPane();
        blockAllComboBoxes();
        hanoiTablePanel.setCellsToCalculatingState();
        hanoiConsolePanel.clearConsole();
    }

    private void unblockUI() {
        application.unblockTabbedPane();
        unblockAllComboBoxes();
        unblockCalculateButton();
    }

    private void blockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            hanoiSettingsPanelComboBoxes.get(index).setEnabled(false);
        });
    }

    private void unblockAllComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            hanoiSettingsPanelComboBoxes.get(index).setEnabled(true);
        });
    }

    private void blockCalculateButton() {
        hanoiSettingsPanelCalculateButton.setEnabled(false);
    }

    private void unblockCalculateButton() {
        hanoiSettingsPanelCalculateButton.setEnabled(true);
    }

    private void blockCancelButton() {
        hanoiSettingsPanelCancelButton.setEnabled(false);
    }

    private void unblockCancelButton() {
        hanoiSettingsPanelCancelButton.setEnabled(true);
    }

    public List<Integer> getSamples() {
        return samples;
    }
}
