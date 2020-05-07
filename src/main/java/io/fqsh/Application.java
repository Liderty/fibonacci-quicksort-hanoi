package io.fqsh;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.swing.*;
import java.awt.*;

public class Application {
    private JFrame frame;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application application = injector.getInstance(Application.class);

        SwingUtilities.invokeLater(application::run);
    }

    public void run() {
        buildFrame();
    }

    private void buildFrame() {
        frame = new JFrame("Liczby Fibonacciego, Quick Sort, Wieże Hanoi - iteracyjnie i rekurencyjnie (M. Liber, P. Lyschik, 2020)");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 720);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
