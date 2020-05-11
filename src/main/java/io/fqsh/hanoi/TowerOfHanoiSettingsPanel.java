package io.fqsh.hanoi;

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
public class TowerOfHanoiSettingsPanel {
    private JPanel hanoiSettingsPanel;
    private final List<JComboBox<Integer>> hanoiSettingsPanelComboBoxes = new ArrayList<>();
    private JButton hanoiSettingsPanelCalculateButton;
    private final List<Integer> samples = Arrays.asList(3, 15, 20, 22, 25);

    @Inject
    private Application application;

    @Inject
    private TowerOfHanoiChartsPanel hanoiChartsPanel;

    @Inject
    private TowerOfHanoiTablePanel hanoiTablePanel;

    @Inject
    private TowerOfHanoiConsolePanel hanoiConsolePanel;

    public JPanel build() {
        buildSettingsPanel();
        buildComboBoxes();
        buildCalculateButton();

        return hanoiSettingsPanel;
    }

    private void buildSettingsPanel() {
        hanoiSettingsPanel = new JPanel();
        hanoiSettingsPanel.setLayout(new GridLayout(1, 6, 10, 10));
        hanoiSettingsPanel.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                    hanoiSettingsPanel.getBorder(),
                "Ilość krążków do przełożenia",
                TitledBorder.CENTER,
                TitledBorder.TOP
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, 1, 26);

            hanoiSettingsPanelComboBoxes.add(comboBox);
            hanoiSettingsPanel.add(comboBox);
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
                hanoiChartsPanel.updateIterationChart();
                hanoiChartsPanel.updateRecursiveChart();
                hanoiTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        hanoiSettingsPanelCalculateButton = new JButton("Oblicz");
        hanoiSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            if (samples.stream().distinct().count() < 5) {
                JOptionPane.showMessageDialog(null, "Ilość wybranych krążków nie powinna się powtarzać!");

                return;
            }

            clearDataAfterActionIfValuesAreComputed();
            calculate();
        });
        hanoiSettingsPanel.add(hanoiSettingsPanelCalculateButton);
    }

    private void calculate() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(this::blockUI);
        executor.execute(() -> hanoiConsolePanel.write("Rozpoczęto obliczenia."));

        IntStream.rangeClosed(0, 4).forEach(index -> {
            executor.execute(() -> hanoiIterationWrapper(samples.get(index)));
            executor.execute(() -> hanoiRecursiveWrapper(samples.get(index)));
        });

        executor.execute(() -> hanoiConsolePanel.write("Zakończono obliczenia."));
        executor.execute(this::unblockUI);
    }

    private void hanoiIterationWrapper(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = hanoiIteration(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        hanoiChartsPanel.setIterationTimeAt(index, timeElapsed);
        hanoiChartsPanel.updateIterationChart();
        hanoiTablePanel.setCellValueAt(index, 1, Utils.convertTime(timeElapsed));

        hanoiConsolePanel.write(String.format(
                "Wieża Hanoi złożona z %d krążków, została ułożona rekurencyjnie w czasie: %s. Wykonano %d ruchów.",
            n,
            Utils.convertTime(timeElapsed),
                result
        ));
    }

    private Long hanoiIteration(int disks_number) {
        IterativeHanoiTower towerIterative = new IterativeHanoiTower(disks_number);
        towerIterative.solveIterativeHanoiTower();
        return (long) towerIterative.getNumberOfMoves();
    }

    private void hanoiRecursiveWrapper(Integer n) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        long result = hanoiRecursive(n);
        long finishTime = System.nanoTime();

        long timeElapsed = finishTime - startTime;

        hanoiChartsPanel.setRecursiveTimeAt(index, timeElapsed);
        hanoiChartsPanel.updateRecursiveChart();
        hanoiTablePanel.setCellValueAt(index, 2, Utils.convertTime(timeElapsed));

        hanoiConsolePanel.write(String.format(
            "Wieża Hanoi złożona z %d krążków, została ułożona rekurencyjnie w czasie: %s. Wykonano %d ruchów.",
            n,
            Utils.convertTime(timeElapsed),
                result
        ));
    }

    static long hanoiRecursive(int disks_number) {
        RecursiveHanoiTower towerRecurive = new RecursiveHanoiTower(disks_number);
        towerRecurive.solveRecursvieHanoiTower();
        return (long) towerRecurive.getNumberOfMoves();
    }

    private void clearDataAfterActionIfValuesAreComputed() {

        if (hanoiChartsPanel.getIterationTimes().stream().noneMatch(value -> value.equals(0L))) {
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
        blockCalculateButton();
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

    public List<Integer> getSamples() {
        return samples;
    }
}
