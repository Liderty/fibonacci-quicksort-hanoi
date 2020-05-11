package io.fqsh.quickSort;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Singleton
public class QuickSortChartsPanel {
    private JPanel quickSortChartsPanel;
    private XChartPanel<XYChart> iterationChartPanel;
    private XChartPanel<XYChart> recursiveChartPanel;
    private XYChart iterationChart;
    private XYChart recursiveChart;
    private final List<Long> iterationTimes = new ArrayList<>(Collections.nCopies(5, 0L));
    private final List<Long> recursiveTimes = new ArrayList<>(Collections.nCopies(5, 0L));

    @Inject
    private QuickSortSettingsPanel quickSortSettingsPanel;

    public JPanel build() {
        quickSortChartsPanel = new JPanel();
        quickSortChartsPanel.setLayout(new GridLayout(1, 2));
        quickSortChartsPanel.setBorder(BorderFactory.createTitledBorder(
                quickSortChartsPanel.getBorder(),
            "Wykresy czasu obliczeń w zależności od podejścia",
            TitledBorder.CENTER,
            TitledBorder.TOP
        ));

        iterationChart = new XYChartBuilder()
            .title("Iteracyjnie")
            .xAxisTitle("Ilość sortowanych danych")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        iterationChart.getStyler().setChartBackgroundColor(new Color(238, 238, 238));
        iterationChart.getStyler().setChartTitlePadding(10);
        iterationChart.getStyler().setLegendVisible(false);
        iterationChart.getStyler().setToolTipsEnabled(true);
        iterationChart.getStyler().setToolTipType(Styler.ToolTipType.yLabels);

        iterationChart.addSeries("times", quickSortSettingsPanel.getSamples(), iterationTimes);

        recursiveChart = new XYChartBuilder()
            .title("Rekurencyjnie")
            .xAxisTitle("Ilość sortowanych danych")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        recursiveChart.getStyler().setChartBackgroundColor(new Color(238, 238, 238));
        recursiveChart.getStyler().setChartTitlePadding(10);
        recursiveChart.getStyler().setLegendVisible(false);
        recursiveChart.getStyler().setToolTipsEnabled(true);
        recursiveChart.getStyler().setToolTipType(Styler.ToolTipType.yLabels);

        recursiveChart.addSeries("times", quickSortSettingsPanel.getSamples(), recursiveTimes);

        iterationChartPanel = new XChartPanel<>(iterationChart);
        recursiveChartPanel = new XChartPanel<>(recursiveChart);

        quickSortChartsPanel.add(iterationChartPanel);
        quickSortChartsPanel.add(recursiveChartPanel);

        return quickSortChartsPanel;
    }

    public void setIterationTimeAt(int index, long value) {
        iterationTimes.set(index, value);
    }

    public void setRecursiveTimeAt(int index, long value) {
        recursiveTimes.set(index, value);
    }

    public void resetData() {
        IntStream.rangeClosed(0, 4).forEach(index -> {
            iterationTimes.set(index, 0L);
            recursiveTimes.set(index, 0L);
        });
    }

    public void updateIterationChart() {
        iterationChart.updateXYSeries("times", quickSortSettingsPanel.getSamples(), iterationTimes, null);
        iterationChartPanel.revalidate();
        iterationChartPanel.repaint();
    }

    public void updateRecursiveChart() {
        recursiveChart.updateXYSeries("times", quickSortSettingsPanel.getSamples(), recursiveTimes, null);
        recursiveChartPanel.revalidate();
        recursiveChartPanel.repaint();
    }

    public List<Long> getIterationTimes() {
        return iterationTimes;
    }
}
