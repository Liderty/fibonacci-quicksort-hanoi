package io.fqsh.fibonacci;

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
public class FibonacciChartsPanel {
    private JPanel fibonacciChartsPanel;
    private XChartPanel<XYChart> iterationChartPanel;
    private XChartPanel<XYChart> recursiveChartPanel;
    private XYChart iterationChart;
    private XYChart recursiveChart;
    private final List<Long> iterationTimes = new ArrayList<>(Collections.nCopies(5, 0L));
    private final List<Long> recursiveTimes = new ArrayList<>(Collections.nCopies(5, 0L));

    @Inject
    private FibonacciSettingsPanel fibonacciSettingsPanel;

    public JPanel build() {
        fibonacciChartsPanel = new JPanel();
        fibonacciChartsPanel.setLayout(new GridLayout(1, 2));
        fibonacciChartsPanel.setBorder(BorderFactory.createTitledBorder(
            fibonacciChartsPanel.getBorder(),
            "Wykresy prezentujące czas obliczania w zależności od podejścia",
            TitledBorder.CENTER,
            TitledBorder.TOP
        ));

        buildIterationChart();
        fibonacciChartsPanel.add(iterationChartPanel);

        buildRecursiveChart();
        fibonacciChartsPanel.add(recursiveChartPanel);

        return fibonacciChartsPanel;
    }

    private void buildIterationChart() {
        iterationChart = new XYChartBuilder()
            .title("Iteracyjnie")
            .xAxisTitle("Wyraz ciągu Fibonacciego")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        iterationChart.addSeries("times", fibonacciSettingsPanel.getSamples(), iterationTimes);

        iterationChart.getStyler()
            .setChartBackgroundColor(new Color(238, 238, 238))
            .setChartTitlePadding(10)
            .setLegendVisible(false)
            .setToolTipsEnabled(true)
            .setToolTipType(Styler.ToolTipType.yLabels);

        iterationChartPanel = new XChartPanel<>(iterationChart);
    }

    private void buildRecursiveChart() {
        recursiveChart = new XYChartBuilder()
            .title("Rekurencyjnie")
            .xAxisTitle("Wyraz ciągu Fibonacciego")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        recursiveChart.addSeries("times", fibonacciSettingsPanel.getSamples(), recursiveTimes);

        recursiveChart.getStyler()
            .setChartBackgroundColor(new Color(238, 238, 238))
            .setChartTitlePadding(10)
            .setLegendVisible(false)
            .setToolTipsEnabled(true)
            .setToolTipType(Styler.ToolTipType.yLabels);

        recursiveChartPanel = new XChartPanel<>(recursiveChart);
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
        iterationChart.updateXYSeries("times", fibonacciSettingsPanel.getSamples(), iterationTimes, null);
        iterationChartPanel.revalidate();
        iterationChartPanel.repaint();
    }

    public void updateRecursiveChart() {
        recursiveChart.updateXYSeries("times", fibonacciSettingsPanel.getSamples(), recursiveTimes, null);
        recursiveChartPanel.revalidate();
        recursiveChartPanel.repaint();
    }

    public List<Long> getIterationTimes() {
        return iterationTimes;
    }
}
