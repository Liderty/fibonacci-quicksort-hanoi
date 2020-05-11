package io.fqsh;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.fqsh.fibonacci.FibonacciPanel;
import io.fqsh.hanoi.TowerOfHanoiPanel;
import io.fqsh.quickSort.QuickSortPanel;

import javax.swing.*;
import java.awt.*;

public class Application {
    private JFrame frame;
    private JTabbedPane tabbedPane;

    @Inject
    private FibonacciPanel fibonacciPanel;

    @Inject
    private QuickSortPanel quickSortPanel;

    @Inject
    private TowerOfHanoiPanel hanoiPanel;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application application = injector.getInstance(Application.class);

        SwingUtilities.invokeLater(application::run);
    }

    public void run() {
        buildTabbedPane();
        buildFrame();
    }

    private void buildFrame() {
        frame = new JFrame("Liczby Fibonacciego, Sortowanie Szybkie (Quick Sort), Wieże Hanoi - iteracyjnie i rekurencyjnie (M. Liber, P. Lyschik, 2020)");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 720);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    private void buildTabbedPane() {
        tabbedPane = new JTabbedPane();

        tabbedPane.add("Liczby Fibonacciego", fibonacciPanel.build());
        tabbedPane.add("Sortowanie Szybkie (Quick Sort)", quickSortPanel.build());
        tabbedPane.add("Wieże Hanoi", hanoiPanel.build());
    }

    public void blockTabbedPane() {
        tabbedPane.setEnabled(false);
    }

    public void unblockTabbedPane() {
        tabbedPane.setEnabled(true);
    }
}
