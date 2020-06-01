package io.fqsh.hanoi;

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
public class TowerOfHanoiChartsPanel {
    private JPanel hanoiChartsPanel;
    private XChartPanel<XYChart> iterationChartPanel;
    private XChartPanel<XYChart> recursiveChartPanel;
    private XYChart iterationChart;
    private XYChart recursiveChart;
    private final List<Long> iterationTimes = new ArrayList<>(Collections.nCopies(5, 0L));
    private final List<Long> recursiveTimes = new ArrayList<>(Collections.nCopies(5, 0L));

    @Inject
    private TowerOfHanoiSettingsPanel hanoiSettingsPanel;

    public JPanel build() {
        hanoiChartsPanel = new JPanel();
        hanoiChartsPanel.setLayout(new GridLayout(1, 2));
        hanoiChartsPanel.setBorder(BorderFactory.createTitledBorder(
            hanoiChartsPanel.getBorder(),
            "Wykresy prezentujące czas obliczania w zależności od podejścia",
            TitledBorder.CENTER,
            TitledBorder.TOP
        ));

        buildIterationChart();
        hanoiChartsPanel.add(iterationChartPanel);

        buildRecursiveChart();
        hanoiChartsPanel.add(recursiveChartPanel);

        return hanoiChartsPanel;
    }

    private void buildIterationChart() {
        iterationChart = new XYChartBuilder()
            .title("Iteracyjnie")
            .xAxisTitle("Liczba krążków")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        iterationChart.addSeries("times", hanoiSettingsPanel.getSamples(), iterationTimes);

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
            .xAxisTitle("Liczba krążków")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        recursiveChart.addSeries("times", hanoiSettingsPanel.getSamples(), recursiveTimes);

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
        iterationChart.updateXYSeries("times", hanoiSettingsPanel.getSamples(), iterationTimes, null);
        iterationChartPanel.revalidate();
        iterationChartPanel.repaint();
    }

    public void updateRecursiveChart() {
        recursiveChart.updateXYSeries("times", hanoiSettingsPanel.getSamples(), recursiveTimes, null);
        recursiveChartPanel.revalidate();
        recursiveChartPanel.repaint();
    }

    public List<Long> getIterationTimes() {
        return iterationTimes;
    }
}
