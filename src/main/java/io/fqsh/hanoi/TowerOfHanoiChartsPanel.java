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
            "Wykresy czasu obliczeń w zależności od podejścia",
            TitledBorder.CENTER,
            TitledBorder.TOP
        ));

        iterationChart = new XYChartBuilder()
            .title("Iteracyjnie")
            .xAxisTitle("Ilość krążków")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        iterationChart.getStyler().setChartBackgroundColor(new Color(238, 238, 238));
        iterationChart.getStyler().setChartTitlePadding(10);
        iterationChart.getStyler().setLegendVisible(false);
        iterationChart.getStyler().setToolTipsEnabled(true);
        iterationChart.getStyler().setToolTipType(Styler.ToolTipType.yLabels);

        iterationChart.addSeries("times", hanoiSettingsPanel.getSamples(), iterationTimes);

        recursiveChart = new XYChartBuilder()
            .title("Rekurencyjnie")
            .xAxisTitle("Ilość krążków")
            .yAxisTitle("Czas obliczania (ns)")
            .build();

        recursiveChart.getStyler().setChartBackgroundColor(new Color(238, 238, 238));
        recursiveChart.getStyler().setChartTitlePadding(10);
        recursiveChart.getStyler().setLegendVisible(false);
        recursiveChart.getStyler().setToolTipsEnabled(true);
        recursiveChart.getStyler().setToolTipType(Styler.ToolTipType.yLabels);

        recursiveChart.addSeries("times", hanoiSettingsPanel.getSamples(), recursiveTimes);

        iterationChartPanel = new XChartPanel<>(iterationChart);
        recursiveChartPanel = new XChartPanel<>(recursiveChart);

        hanoiChartsPanel.add(iterationChartPanel);
        hanoiChartsPanel.add(recursiveChartPanel);

        return hanoiChartsPanel;
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