package io.fqsh.quickSort;

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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

@Singleton
public class QuickSortSettingsPanel {
    private JPanel quickSortSettingsPanel;
    private final List<JComboBox<Integer>> quickSortSettingsPanelComboBoxes = new ArrayList<>();
    private JButton quickSortSettingsPanelCalculateButton;
    private final List<Integer> samples = Arrays.asList(50, 60, 70, 80, 90);

    @Inject
    private Application application;

    @Inject
    private QuickSortChartsPanel quickSortChartsPanel;

    @Inject
    private QuickSortTablePanel quickSortTablePanel;

    @Inject
    private QuickSortConsolePanel quickSortConsolePanel;

    public JPanel build() {
        buildSettingsPanel();
        buildComboBoxes();
        buildCalculateButton();

        return quickSortSettingsPanel;
    }

    private void buildSettingsPanel() {
        quickSortSettingsPanel = new JPanel();
        quickSortSettingsPanel.setLayout(new GridLayout(1, 6, 10, 10));
        quickSortSettingsPanel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        quickSortSettingsPanel.getBorder(),
                        "Liczba elementów do posortowania (w tys.)",
                        TitledBorder.CENTER,
                        TitledBorder.TOP
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private void buildComboBoxes() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            JComboBox<Integer> comboBox = createRangeComboBox(index, 5, 100);

            quickSortSettingsPanelComboBoxes.add(comboBox);
            quickSortSettingsPanel.add(comboBox);
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
                quickSortChartsPanel.updateIterationChart();
                quickSortChartsPanel.updateRecursiveChart();
                quickSortTablePanel.setCellValueAt(index, 0, value);
            }
        });

        return comboBox;
    }

    private void buildCalculateButton() {
        quickSortSettingsPanelCalculateButton = new JButton("Oblicz");
        quickSortSettingsPanelCalculateButton.addActionListener(actionEvent -> {
            if (samples.stream().distinct().count() < 5) {
                JOptionPane.showMessageDialog(null, "Ilość danych nie powinna się powtarzać!");

                return;
            }

            clearDataAfterActionIfValuesAreComputed();
            calculate();
        });
        quickSortSettingsPanel.add(quickSortSettingsPanelCalculateButton);
    }

    private void calculate() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(this::blockUI);
        executor.execute(() -> quickSortConsolePanel.write("Rozpoczęto sortowanie."));

        IntStream.rangeClosed(0, 4).forEach(index -> {
            int data_size = samples.get(index);

            int [] unsortedDataForIteration = simplyDataGenerator(data_size);
            int [] unsortedDataForRecursion = unsortedDataForIteration.clone();

            executor.execute(() -> quickSortIterationWrapper(data_size, unsortedDataForIteration));
            executor.execute(() -> quickSortRecursiveWrapper(data_size, unsortedDataForRecursion));
        });

        executor.execute(() -> quickSortConsolePanel.write("Zakończono sortowanie."));
        executor.execute(this::unblockUI);
    }

    private int[] simplyDataGenerator(int data_size) {
        int[] sampleArray = new int[data_size*1000];
        Random generator = new Random();

        for (int i = 0; i < sampleArray.length; i++) {
            sampleArray[i] = generator.nextInt(1000);
        }

        return sampleArray;
    }

    private void quickSortIterationWrapper(int n, int [] unsorted_data) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        quickSortIteration(unsorted_data, 0, unsorted_data.length - 1);
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

    private int separate(int separatedArray[], int leftIndex, int rightIndex) {
        int separatorValueRight = separatedArray[rightIndex];
        int separatorLeftIndex = leftIndex - 1;

        for (int s = leftIndex; s < rightIndex; s++) {
            if (separatedArray[s] < separatorValueRight) {
                separatorLeftIndex++;

                int tmp = separatedArray[separatorLeftIndex];
                separatedArray[separatorLeftIndex] = separatedArray[s];
                separatedArray[s] = tmp;
            }
        }

        int tmp = separatedArray[separatorLeftIndex + 1];
        separatedArray[separatorLeftIndex + 1] = separatedArray[rightIndex];
        separatedArray[rightIndex] = tmp;

        return separatorLeftIndex + 1;
    }

    private void quickSortIteration(int sortedArray[], int leftIndex, int rightIndex) {
        int[] stack = new int[rightIndex - leftIndex + 1];
        int top = 0;

        stack[top] = leftIndex;
        stack[++top] = rightIndex;

        while (top >= 0) {
            rightIndex = stack[top--];
            leftIndex = stack[top--];

            int separator = separate(sortedArray, leftIndex, rightIndex);

            if ((separator - 1) > leftIndex) {
                stack[++top] = leftIndex;
                stack[++top] = rightIndex - 1;
            }

            if ((separator + 1) < rightIndex) {
                stack[++top] = separator + 1;
                stack[++top] = rightIndex;
            }
        }
    }

    private void quickSortRecursiveWrapper(int n, int [] unsorted_data) {
        int index = samples.indexOf(n);

        long startTime = System.nanoTime();
        quickSortRecursive(unsorted_data, 0, unsorted_data.length - 1);
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

    private void quickSortRecursive(int sortedArray[], int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int separationIndex = separate(sortedArray, leftIndex, rightIndex);

            quickSortRecursive(sortedArray, leftIndex, separationIndex - 1);
            quickSortRecursive(sortedArray, separationIndex + 1, rightIndex);
        }
    }

    private void clearDataAfterActionIfValuesAreComputed() {

        if (quickSortChartsPanel.getIterationTimes().stream().noneMatch(value -> value.equals(0L))) {
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
        blockCalculateButton();
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

    public List<Integer> getSamples() {
        return samples;
    }
}
